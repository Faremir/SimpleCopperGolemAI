# Smarter Golems

Smarter Golems is a lightweight Minecraft mod that slightly improves the vanilla Copper Golem item transport behavior.

The mod gives each Copper Golem a small amount of memory when moving items between Copper Chests and normal chests. This allows it to reuse successful item-to-chest routes instead of repeatedly starting from scratch.

## Features

- Remembers the last item picked from a Copper Chest.
- Remembers the last normal chest where that item was successfully deposited.
- When visiting a Copper Chest again, looks for the previously picked item first instead of automatically taking the first non-empty slot.
- When that item is picked again, prefers the previously successful destination chest.
- Falls back to the normal vanilla behavior when the preferred item or chest cannot be used.
- Keeps the memory separately for each Copper Golem.
- Does not store chest contents or maintain a global item-to-chest mapping.

The goal is not to replace or redesign the Copper Golem AI. The mod only adds a small preference to the existing behavior.

## How it works

When a Copper Golem picks an item from a Copper Chest, it remembers that item.

When it successfully deposits the item into a normal chest, it remembers that chest.

When the golem later searches a Copper Chest for another item, the remembered item is preferred if it is available in that chest. If it is picked, the golem then prefers the normal chest where that item was previously deposited.

If the preferred item or chest is unavailable, the golem falls back to the normal Minecraft behavior.

The memory is intentionally temporary and lightweight. Nothing is stored in the world, in a block, or in a separate database.

## Contributing

Bug reports, suggestions and pull requests are welcome.

The project is intentionally kept small and focused. Contributions should follow the existing approach of making targeted changes to the vanilla Copper Golem behavior rather than introducing unnecessary systems or dependencies.

For larger changes, opening an issue first is recommended so the proposed approach can be discussed before implementation.

## License

Smarter Golems is licensed under the **PolyForm Shield License 1.0.0**. See [LICENSE](LICENSE) for details.