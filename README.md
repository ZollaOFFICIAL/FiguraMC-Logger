# Figura Logger (Fabric 1.21.1)

A simple client-side logger that captures **Figura** avatars you render in-game and saves them locally for inspection and reproduction.

> Scope: This is a *logger* for other users’ currently rendered avatars. It saves the raw Figura NBT and also mirrors a copy into `.minecraft/figura/local` so you can quickly load it with Figura’s local loader.

---

## How it works (today)

- Hooks Figura’s avatar load path (client-side) and grabs the **NBT** tag for each avatar you render.
- Writes two outputs:
  1. **Moderation/archival zip** → `.minecraft/LoggedModels/<player-uuid>/<uuid>_<hash>_<timestamp>.zip` containing:
     - `avatar.nbt` (compressed NBT as sent to the client)
     - `provenance.json` (owner UUID, timestamp, short hash)
  2. **Quick mirror for Figura local** → `.minecraft/figura/local/avatar.nbt` (always the latest capture), plus time-stamped copies at  
     `.minecraft/figura/local/captures/<player-uuid>/<timestamp>/avatar.nbt`

> Note: The NBT is the canonical artifact Figura uses. Some community models are distributed as `script.lua`, `avatar.json`, `*.bbmodel`, etc.—those are **not** what Figura’s network sends. Converting NBT back to those files is non-trivial and often lossy. The logger intentionally saves the exact NBT so the model can be reloaded via Figura’s **local loader**.

---

## Current status

- ✅ Fabric 1.21.1 project compiles and runs with **Fabric Loader** + **Fabric API** + **Figura 0.1.5 (1.21.1)** in `run/mods/`.
- ✅ NBT capture + zipped output works.
- ✅ “Mirror to local” writes `avatar.nbt` directly to `.minecraft/figura/local` for one-click testing.
- ❗ **Open task:** add precise hooks for Figura 1.21.1 class names to ensure we capture at the best points.

---

## Where we’re stuck (and what we need)

Figura bundles classes inside **nested jars**. To make the mixins 100% precise for 1.21.1 we need to locate:
org/figuramc/figura/avatar/Avatar.class
org/figuramc/figura/avatar/AvatarManager.class
org/figuramc/figura/avatar/UserData.class

**Action:** open `figura-0.1.5+1.21.1-fabric-mc.jar`, inspect `META-INF/jars/*`, and find which inner jar actually contains the `org/figuramc/figura/avatar/*` classes. Once we have those exact method signatures (esp. `loadAvatar(...)` / `setAvatar(...)`), we’ll lock the mixins.

---

## Build / run (dev)

1. Put these jars in **run/mods/**:
   - Fabric API (for 1.21.1)
   - Figura `0.1.5+1.21.1-fabric-mc.jar` (the mod jar you already have)

2. In IntelliJ Gradle tool window, run:  
   **Tasks → fabric → runClient**

3. Join a world with players using Figura. Captures will appear in:
   - `.minecraft/LoggedModels/...`
   - `.minecraft/figura/local/avatar.nbt` (latest)

Open Figura’s **Local** tab and load the mirrored `avatar.nbt`.

---

## Dev notes

- Java 21, Fabric Loom, Yarn mappings for 1.21.1.
- No redistribution of someone’s model files here, this logs exactly what the client receives (NBT), to reproduce what you see.

---

## Roadmap

- [ ] Confirm final mixin targets for 1.21.1 (exact class/method descriptors).
- [ ] Add simple `/flog` toggle & hotkeys.
- [ ] structured export (owner UUID folders, server name, world seed hash).
- [ ] on-disk index JSON and viewer.



