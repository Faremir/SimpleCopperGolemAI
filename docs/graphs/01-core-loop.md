# Core Loop

[← Charts](00-CHARTS.md) · [Target Selection](02-target-selection.md) · [Container Interaction](03-container-interaction.md)

The core loop shows the persistent state machine.

```mermaid
flowchart TD
    A["IDLE activity active"]:::vanilla
    B{"Transport behavior can run?"}:::vanilla

    A --> B

    B -- "NO<br/>cooldown present, panicking, or leashed" --> A
    B -- "YES" --> C["Behavior tick"]:::vanilla

    C --> D{"Current target valid?"}:::vanilla

    D -- "NO" --> E["Target Selection"]:::vanilla
    E --> F{"Matching target found?"}:::vanilla

    F -- "NO" --> G["Cooldown = 140 ticks<br/>clear target and search memories"]:::vanilla
    G --> C

    F -- "YES" --> H["Current target<br/>state = TRAVELLING"]:::vanilla
    H --> I["Container Interaction"]:::vanilla

    D -- "YES" --> I

    I --> J{"Interaction completed?"}:::vanilla

    J -- "NO" --> C
    J -- "YES" --> K["state = TRAVELLING"]:::vanilla
    K --> C

    classDef vanilla fill:#dbeafe,stroke:#2563eb,color:#111827,stroke-width:1px;
    classDef mod fill:#edd18a,stroke:#e3ae29,color:#111827,stroke-width:2px;
