# Contributing

This document describes the branch and pull request workflow used by **Smarter Golems**.

The repository uses a small number of permanent branches and short-lived branches for individual changes. Keep changes
isolated, use pull requests for integration, and let the automated workflows keep development history aligned with the
current stable release.

## Branches

| Branch       | Purpose                                            |
|--------------|----------------------------------------------------|
| `dev`        | Development and integration branch                 |
| `feature/*`  | New functionality                                  |
| `backport/*` | Adaptations of changes for an older stable version |
| `alpha/*`    | Alpha release line                                 |
| `beta/*`     | Beta release line                                  |
| `stable/*`   | Released, version-specific code                    |

`feature/*` and `backport/*` branches are temporary and are deleted after their stable pull request is merged.

## Feature workflow

New functionality follows this path:

```text
stable/*
   │
   └──▶ feature/*
          │
          ├── PR → dev
          │
          ▼
         dev
          │
          └── PR → stable/*
```

### 1. Create the feature branch

Create the branch from the **latest stable branch**:

```bash
git switch stable/26.3
git pull
git switch -c feature/configurable-golem-behavior
```

A feature branch should contain the feature itself, not unrelated changes.

### 2. Develop and test

Push the feature branch and open a pull request to `dev`.

```text
feature/* → dev
```

The `dev` branch is the integration and testing environment. Other features may already be present there.

### 3. Promote the feature to stable

Once the feature has been tested on `dev`, open a pull request from the same feature branch to the appropriate stable
branch:

```text
feature/* → stable/26.3
```

Do not rewrite or modify the feature branch simply because it has already been merged into `dev`.

If problems are found, fix them with additional commits on the feature branch and update the pull request.

### 4. Release the feature

When the feature is merged into `stable/*`, automation rebases `dev` onto that stable branch.

Conceptually:

```text
stable:  A ── B ── C ── F
                  \
dev:     A' ── X ── B' ── Y ── C' ── Z
```

becomes:

```text
stable:  A ── B ── C ── F
                         \
dev:                     X' ── Y' ── Z'
```

Git's rebase operation automatically skips changes that are already present in the stable history, so the
development-only changes are replayed on top of the new stable tip.

The `dev` branch therefore stays based on the latest stable release without manually cherry-picking or maintaining a
commit mapping.

The original `feature/*` branch is then deleted automatically.

The feature is **not published automatically when it is merged**. Releases are published manually from the appropriate
release branch using the **Release** workflow in GitHub Actions.

To publish a release:

1. Make sure the intended `alpha/*`, `beta/*`, or `stable/*` branch contains the exact version to publish.
2. Open **Actions → Release** on GitHub.
3. Select **Run workflow**.
4. Select the intended release branch.
5. Run the workflow.

The workflow builds the project and publishes the mod using the release type determined by the branch:

```text
alpha/*  → Alpha release
beta/*   → Beta release
stable/* → Stable release
```

Do not run the release workflow from `dev`, `feature/*`, or `backport/*`.

## Backports

When a change needs to be adapted for an older stable release, create a temporary `backport/*` branch from the relevant
code and adapt the change there.

```text
backport/* → stable/*
```

The amount of adaptation is intentionally left to the developer. A simple change may require only minor adjustments; a
larger incompatibility may require a dedicated implementation for that Minecraft version.

Backports should contain only the changes necessary for the target stable version.

After the pull request is merged, the `backport/*` branch is deleted automatically.

## Release branches

Release branches form a progression rather than a pull request chain:

```text
stable/26.3
    │
    └── alpha/26.4
            │
            └── beta/26.4
                    │
                    └── stable/26.4
```

Alpha, beta, and stable branches are created from the preceding release line.

They are **not merged into each other with pull requests**.

Release branches are permanent for their respective release line and are protected against direct history rewriting.

## Rules of thumb

* Start new features from the **latest stable** branch.
* Integrate features into `dev` through a pull request.
* Test new functionality on `dev`.
* Promote the feature to the appropriate `stable/*` branch through a pull request.
* Let automation rebase `dev` after a stable feature merge.
* Use `backport/*` for changes targeting older stable releases.
* Publish releases only from `alpha/*`, `beta/*`, or `stable/*` using the **Release** workflow.
* Do not manually cherry-pick stable changes into `dev`.
* Do not force-push protected branches manually.
* Keep temporary branches focused and short-lived.
* Keep unrelated changes out of feature and backport branches.

The goal is simple:

> **Stable contains what has been released. Dev contains stable plus what is currently being developed. Features are
> developed against the latest stable release.**
