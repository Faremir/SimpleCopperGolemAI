# Target Selection

[← Charts](00-CHARTS.md) · [Core Loop](01-core-loop.md) · [Container Interaction](03-container-interaction.md)

This chart documents the target-selection portion of
`TransportItemsBetweenContainers`.

There are two ways the behavior can arrive here:

1. There is no valid current target, so vanilla performs its normal search.
2. Mod has already assigned a remembered chest directly to the behavior's `target` field.

The second case is **not a special search**. The next behavior tick still runs through vanilla target validation. If the
remembered target is valid, the normal search is skipped as per vanilla flow. If it is invalid, vanilla performs its normal search instead.

```mermaid
flowchart TD
    A["Target-selection entry<br/>current target is absent or invalid"]:::vanilla

    A --> B["Search currently loaded chunks<br/>32 horizontal / 8 vertical"]:::vanilla

    B --> C["Inspect candidate block entities"]:::vanilla

    C --> D["Validate candidate"]:::vanilla

    D --> D1["Within search area"]:::vanilla
    D1 --> D2["Correct source/destination type<br/>based on hand contents"]:::vanilla
    D2 --> D3["Not visited"]:::vanilla
    D3 --> D4["Not unreachable"]:::vanilla
    D4 --> D5["Not locked"]:::vanilla
    D5 --> E{"Valid candidate found?"}:::vanilla

    E -- "NO" --> F["No matching target"]:::vanilla
    E -- "YES" --> G["Select nearest candidate<br/>set target<br/>remember as visited<br/>state = TRAVELLING"]:::vanilla

    F --> H["Return to Core Loop"]:::vanilla
    G --> H

    %% Mod-specific path starts after a successful pickup.
    I["Successful pickup"]:::vanilla
    I --> J["Remember picked item"]:::mod

    J --> K{"Preferred deposit enabled<br/>AND remembered chest exists<br/>AND remembered chest matches item?"}:::mod

    K -- "NO" --> L["Do not assign preferred target"]:::mod
    K -- "YES" --> M["Create TransportItemTarget<br/>from remembered chest position"]:::mod

    M --> N["Assign remembered chest<br/>directly to behavior.target"]:::mod
    N --> O["Next behavior tick"]:::vanilla
    L --> O

    O --> P{"Vanilla hasValidTarget()"}:::vanilla

    P -- "YES" --> Q["Keep remembered target<br/>skip normal target search"]:::vanilla
    P -- "NO" --> R["Normal vanilla target-selection flow"]:::vanilla

    Q --> S["Return to Core Loop"]:::vanilla
    R --> A

    classDef vanilla fill:#dbeafe,stroke:#2563eb,color:#111827,stroke-width:1px;
    classDef mod fill:#edd18a,stroke:#e3ae29,color:#111827,stroke-width:2px;