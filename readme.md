# DAC

DAC is PaperMC plugin for Minecraft 26.1.2 inspired by the DACv2 plugin made by aumgn.

DAC aka Dé à Coudre is french turn-based mini-game where players need to jump into a pool from a diving platform. 
Each time a player hits the water, a colored block is placed at the corresponding location. 
If a player misses the water, they are eliminated. The other players continue until there is only one player left.

## Dependencies

This plugin requires WorldEdit 7.4.3 or superior and WorldGuard 7.0.16 or superior.

## Installation

Just put the .jar in the plugins directory before launching your server.

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
where the players can jump during the game. It will be associated to region saved with "/dac define".

### /dac diving \<dacName>

This command will save the position of the player executing it and associate it to the DAC of which the name is 
provided. Players will be teleported to that location when it is their turn to jump into the pool.

### /dac init

The command must be executed inside a DAC region saved with "/dac define". It will initiate DAC game players can join
with "/dac join".

### /dac join \<color>

The command must be executed inside a DAC region saved with "/dac define". It will save the position of the player when
the command is executed, and teleport them to that location after their dive into the pool.

### /dac start

The command must be executed inside a DAC region saved with "/dac define". It will start a DAC game previously
initiated with "/dac init" and joined by at least one player.
