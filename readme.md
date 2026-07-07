# DAC

DAC is PaperMC plugin for Minecraft 26.1.2 inspired by the DACv2 plugin made by aumgn.

DAC aka Dé à Coudre is a french turn-based mini-game where players need to jump into a pool from a diving platform. 
Each time a player hits the water, a colored block is placed at the corresponding location. 
If a player misses the water, they are eliminated. The other players continue until there is only one player left.

## Dependencies

This plugin requires WorldEdit 7.4.3 or superior and WorldGuard 7.0.16 or superior.
These becoming optional dependencies is not planned at the moment.

## Installation

Put the .jar files of DAC and its dependencies in the plugins directory before launching your server.

## Commands

### /dac define \<dacName>

This command requires a WorldEdit selection to be made beforehand. This selection must be of type cuboid or 
polygonal. 

The selected region will be saved as the region where players can initiate a DAC game with "/dac init" and join
an initiated game with "/dac join \<color>".

### /dac pool \<dacName>

This command requires a WorldEdit selection to be made beforehand. This selection must be of type cuboid or
polygonal.

The selected region will be saved as the pool region, which will be filled with water when the game starts and 
where the players can jump during the game. It will be associated to the region saved with "/dac define".

### /dac diving \<dacName>

This command will save the position of the player executing it and associate it to the DAC with the provided name.
Players will be teleported to that location when it is their turn to jump into the pool.

### /dac init

This command must be executed inside a DAC region saved with "/dac define". It will initiate DAC game players can join
with "/dac join".

### /dac join \<color>

This command must be executed inside a DAC region saved with "/dac define". It will save the position of the player when
the command is executed, and teleport them to that location after their dive into the pool.

### /dac start

This command must be executed inside a DAC region saved with "/dac define". It will start a DAC game previously
initiated with "/dac init" and joined by at least one player.

### /dac stop

This command must be executed inside a DAC region saved with "/dac define". It will stop the current DAC game of this 
region and fill the pool region with water.

### /dac fill \<pattern>

This command must be executed inside a DAC region saved with "/dac define". It will fill the corresponding pool
with the provided pattern.

Patterns are "water", "grid" or "dac". "water" just fills the pool with water, "grid" fills the pool with a grid of 
water blocks surrounded by obsidian blocks, and "dac" fills the pool completely with obsidian blocks except one.

### /dac delete \<dacName>

This command deletes the DAC with the provided name from the DAC plugin config file and the associated regions in the 
WorldGuard config.
