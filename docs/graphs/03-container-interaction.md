# Container Interaction

[← Charts](00-CHARTS.md) · [Core Loop](01-core-loop.md) · [Target Selection](02-target-selection.md)

This chart documents what happens after a valid target exists.

The vanilla behavior owns the travelling, queuing, interaction timing, and actual container operation.
Mod modifies the pickup operation when preferred-item pickup is enabled.

The preferred deposit target is intentionally documented in [Target Selection](02-target-selection.md), because the mod
writes directly to the behavior's `target` field after pickup rather than changing the interaction state machine.

```mermaid
flowchart TD
    A["TRAVELLING<br/>valid current target"]:::vanilla

    A --> B{"Within 3 blocks<br/>AND another mob is interacting?"}:::vanilla

    B -- "YES" --> C["QUEUING<br/>stop in place<br/>keep same target"]:::vanilla

    C --> D{"Target still occupied?"}:::vanilla

    D -- "YES" --> C
    D -- "NO" --> E["Resume TRAVELLING<br/>same target"]:::vanilla
    E --> A

    B -- "NO" --> F{"Within interaction range?"}:::vanilla

    F -- "NO" --> G["Continue walking<br/>toward same target"]:::vanilla
    G --> A

    F -- "YES" --> H["Determine ContainerInteractionState"]:::vanilla

    H --> H1["PICKUP_ITEM"]:::vanilla
    H --> H2["PICKUP_NO_ITEM"]:::vanilla
    H --> H3["PLACE_ITEM"]:::vanilla
    H --> H4["PLACE_NO_ITEM"]:::vanilla

    H1 --> I["INTERACTING<br/>60 ticks"]:::vanilla
    H2 --> I
    H3 --> I
    H4 --> I

    I --> J["tick 1<br/>open chest<br/>remember opened chest<br/>set CopperGolemState"]:::vanilla

    J --> K["tick 9<br/>play sound"]:::vanilla

    K --> L{"60 ticks reached?"}:::vanilla

    L -- "NO" --> I
    L -- "YES" --> M{"ContainerInteractionState"}:::vanilla

    M -- "PICKUP_ITEM" --> N["pickUpItems()"]:::vanilla
    M -- "PICKUP_NO_ITEM" --> O["No pickup"]:::vanilla
    M -- "PLACE_ITEM" --> P["putDownItem()"]:::vanilla
    M -- "PLACE_NO_ITEM" --> Q["No placement"]:::vanilla

    %% Mod's Redirect affects only the item-selection operation.
    N --> R{"Preferred item pickup enabled<br/>AND remembered item exists?"}:::mod

    R -- "YES" --> S["Search this source container<br/>for the remembered item"]:::mod

    S --> T{"Remembered item found?"}:::mod

    T -- "YES" --> U["Remove up to 16 matching items"]:::mod
    T -- "NO" --> V["Fallback to vanilla<br/>pickupItemFromContainer()"]:::vanilla

    R -- "NO" --> V

    U --> W["Pickup completed"]:::vanilla
    V --> W

    W --> X["Mod records the newly picked item<br/>and may assign the remembered<br/>deposit chest as behavior.target"]:::mod

    X --> Y["Return to Core Loop<br/>next behavior tick"]:::vanilla

    P --> Z["Vanilla placement<br/>matching partial stack or first empty slot"]:::vanilla

    Z --> AA{"Hand empty after placement?"}:::vanilla

    AA -- "YES" --> AB["Mod remembers current target<br/>as last successfully used chest"]:::mod
    AA -- "NO" --> AC["Do not remember this chest<br/>as successful deposit"]:::mod

    AB --> AD["Finish interaction"]:::vanilla
    AC --> AD

    O --> AD
    Q --> AD

    AD --> AE["Clear opened-chest state<br/>state = TRAVELLING"]:::vanilla

    AE --> Y

    classDef vanilla fill:#dbeafe,stroke:#2563eb,color:#111827,stroke-width:1px;
    classDef mod fill:#edd18a,stroke:#e3ae29,color:#111827,stroke-width:2px;
