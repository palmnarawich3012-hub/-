# Pepper Spray (Fabric 1.20.1)
Hold right-click: unlimited orange mist from the nozzle (range 5 blocks), one-handed normal item pose.
Every living thing hit (mobs, undead, players) gets:
- Pepper Burn: half a heart (1 HP) every second for 5s (can kill) - edit PepperBurnEffect.DAMAGE / INTERVAL_TICKS
- Slowness II 5s, Blindness 5s
Build: push to GitHub -> Actions -> download artifact `pepperspray-mod`. Requires Fabric API.

Held model
- Third person: the can stands upright in front of the hand, nozzle forward (display.thirdperson_*).
- First person: the game's own arm is drawn and the can is attached to its hand
  (mixin/HeldItemRendererMixin + PlayerEntityRendererMixin; transforms in display.firstperson_*, computed by fp_design.py logic).
Tuning: models/item/pepper_spray.json -> display (translation = 1/16 block units).
