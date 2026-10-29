# Controls Mapping: StarterbotTeleopWithMods

This document provides the complete control mapping for the [`StarterbotTeleopWithMods.java`](StarterbotTeleopWithMods.java) OpMode.

---

## 🎮 Gamepad 1: Chassis & Drivetrain Controls

| Control | Action / Function | Description |
| :--- | :--- | :--- |
| **Left Stick Y** (`left_stick_y`) | Forward / Backward | Drives the robot forward/backward (inverted input). |
| **Left Stick X** (`left_stick_x`) | Strafe Left / Right | Strafes the robot laterally. |
| **Right Stick X** (`right_stick_x`) | Rotate Left / Right | Rotates the robot chassis. |
| **X Button** (`gamepad1.x`) | Brake Mode | Sets all drive motors to **BRAKE** zero power behavior. |
| **Y / Triangle Button** (`gamepad1.y` / `triangle`) | Float Mode | Sets all drive motors to **FLOAT** zero power behavior. |
| **L1** (`gamepad1.left_bumper`) | 180 degree turn | Executes an exact 180 degree turn clockwise.|
| **R1** (`gamepad1.right_bumper`) | 180 degree turn | Executes an exact 180 degree turn anticlockwise.|

---

## 🎮 Gamepad 2: Intake, Launcher & Feeder Controls

### Intake Controls
| Control | Action / Function | Description |
| :--- | :--- | :--- |
| **Left Trigger** (`gamepad2.left_trigger`) | Intake Forward | Runs the broom intake motor and intake servos forward (`1.0`). |
| **Right Trigger / Triangle Button** (`gamepad2.right_trigger` / `triangle`) | Intake Backwards | Runs the broom intake motor and intake servos backwards (`-1.0`). |

### Launcher & Flywheel Controls
| Control | Action / Function | Description |
| :--- | :--- | :--- |
| **Left Stick Y** (`left_stick_y`) | Change Flywheel Speed | Modulates/scales the target launcher velocity. |
| **Left Bumper** (`gamepad2.left_bumper`) | Activate Launcher | Turn on the launcher flywheel. |
| **Right  Bumper** (`gamepad2.right_bumper`) | Activate Launcher | Turns on the launcher flywheel. |
| **D-pad Up** (`gamepad2.dpad_up`) | High Speed Preset | Sets target launcher velocity to 1500 ticks/sec. |
| **D-pad Right** (`gamepad2.dpad_right`) | Medium Speed Preset | Sets target launcher velocity to 1250 ticks/sec. |
| **D-pad Down** (`gamepad2.dpad_down`) | Low Speed Preset | Sets target launcher velocity to 1000 ticks/sec. |
| **D-pad Left** (`gamepad2.dpad_left`) | Off Preset | Turns target launcher velocity to 0 ticks/sec. |

### Windmill / Feeder Controls
| Control | Action / Function | Description |
| :--- | :--- | :--- |
| **Right Stick Y** (`right_stick_y`) | Change Windmill Speed | Adjusts the windmill servo speed when active. |
| **Right Trigger / Right Bumper** (`right_trigger > 0.3` or `right_bumper`) | Enable Windmill Feeder | Safety enabler for feeding game pieces when the launcher has reached target velocity. |


