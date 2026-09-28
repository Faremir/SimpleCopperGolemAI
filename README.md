# Smarter Golems

[![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)](https://fabricmc.net/)
[![Static Badge](https://img.shields.io/badge/Mod%20Menu-134BFF)](https://modrinth.com/mod/modmenu)

Give your Copper Golems a little memory.

**Smarter Golems** adds small improvements to the Copper Golem item and chest handling behavior, making them useful for
early/mid-game storage system solutions. It keeps the vanilla behavior intact and only adds a couple of useful
preferences on top.

## Features

### 🧠 Remember Picked Items

Copper Golems can remember the **last item they picked up from a Copper Chest**.

When they return to that chest, they will look for that item first instead of always starting from the first available
item.

If the remembered item is no longer there, the golem simply continues with its normal behavior.

### 📦 Remember Destination Chests

Copper Golems can also remember the **last chest where they successfully deposited an item**.

When they pick up that same item again, they will try the remembered chest first instead of searching for a destination
from scratch.

If the chest is unavailable or cannot accept the item, normal chest searching takes over.

<sub>These are two independent options and can be enabled or disabled separately.</sub>

## ⚙️ Configuration

Smarter Golems has two configuration options:

* **Remember Picked Items**
  Remembers the last item picked from a Copper Chest and looks for it first the next time that chest is searched.

* **Remember Destination Chests**
  Remembers the last chest used for an item and tries it first when that same item is picked up again.

#### In-game settings

The configuration screen is available through [Mod Menu](https://modrinth.com/mod/modmenu).

If you have Mod Menu installed, open the **Smarter Golems** configuration from the Mods screen.

**Mod Menu is optional.** Smarter Golems works without it.

#### Without Mod Menu

If you don't use Mod Menu, the configuration can still be changed manually in the mod's configuration file:

`config/smartergolems.json`

#### Reloading the configuration

The configuration can be reloaded without restarting the game or server:

```text
/smartergolems reload
```

The command requires moderator permissions and can be used both on dedicated servers and in singleplayer.

## 💡 Designed to Stay Simple

Smarter Golems builds on the vanilla Copper Golem instead of replacing its existing behavior. The mod adds small,
focused improvements that make Copper Golems more useful while keeping their familiar behavior and mechanics intact.

When an added behavior cannot be used, the Copper Golem continues using its normal vanilla behavior.

## Contributing

Bug reports, suggestions and pull requests are welcome.

The project is intentionally kept small and focused. Contributions should follow the existing approach of making
targeted changes to the vanilla Copper Golem behavior rather than introducing unnecessary systems or dependencies.

For larger changes, opening an issue first is recommended so the proposed approach can be discussed before
implementation.

## License

Smarter Golems is licensed under the **PolyForm Shield License 1.0.0**. See [LICENSE](LICENSE) for details.