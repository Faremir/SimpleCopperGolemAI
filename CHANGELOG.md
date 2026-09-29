# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-09-29

### Added

- Added configuration options for Copper Golem item pickup and chest selection
- Added Mod Menu support for changing mod settings in-game
- Added the ability to enable or disable individual behavior preferences
- Added support for running the mod without Mod Menu by configuring the mod directly
- Added a server command for reloading the configuration without restarting

### Changed

* Improved the separation between client-side configuration screens and server-side Copper Golem behavior

## [1.0.0] - 2026-09-28

### Added

- Copper Golems remember the last item they picked up
- Copper Golems prefer picking up the same item again when available
- Copper Golems remember the last chest where they successfully deposited an item
- Copper Golems prefer that chest when depositing the same item
- Vanilla item and chest selection remains available as a fallback