# Apollo Client 1.8.8

Apollo Client is a modified Eaglercraft 1.8.8 client, built for performace, PvP, and for an overall better experience.


## Compiling

**JavaScript**

To compile the client run `./CompileLatestClient.sh` in the "target_teavm_javascript" folder.
If gradle crashes, ususally due to out of memory error, run `./gralde --stop`

**TeaVM**

To compile the client via WASM run `CompileWASM.sh` in the "target_teavm_wasm_gc" folder.

## Testing

The client has only been tested using the **JavaScript** version. It has NOT be tested with **Desktop** nor **WASM-GC**.

## Commiting

As a contributer, you should add your name to `CONTRIBUTORS.md`. You are also **expected** to obey the rules in `COMMIT.md`. It is important to do so to keep the client alive, and clean.

## FILES

**MODULES**

`/src/game/java/net/minecraft/apolloclient/gui/mods`

**HUDS (Click GUI)**

`/src/game/java/net/minecraft/apolloclient/gui/hud`
