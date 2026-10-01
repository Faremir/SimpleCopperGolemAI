# Copper Golem Behavior Charts

These charts document the copper golem transport behavior together with the behavior added by our mod.

The behavior is one repeating state machine. The diagrams are different views of that same cycle rather than separate
workflows.

## Charts

- [Core Loop](01-core-loop.md) — the persistent transport state machine and its repeating tick cycle.
- [Target Selection](02-target-selection.md) — how a valid target is retained, how vanilla searches for a new target, and
  how the mod can pre-populate the target with the previously remembered deposit chest.
- [Container Interaction](03-container-interaction.md) — travelling, queuing, the 60-tick interaction window, and the
  pickup/place operation.

## Legend

- **Blue** — vanilla Minecraft behavior
- **Orange** — SimpleCopperGolemAI behavior
