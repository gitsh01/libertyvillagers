# Liberty's Villagers

Are you the mayor of a village? Are you tired of your villagers failing to find their beds at nightfall, or forgetting
their workstations in the morning? Have you seen one too many villager die from jumping off stairs and injuring
themselves over and over? Well, Liberty's Villagers is here to help!

Liberty's Villagers is a fabric mod that improves the logic and allows the mayor (you) to modify villager behavior to
provide a better experience for your villagers!

## Features:

### General

* Adjust the range which villagers will find Points Of Interest (useful for vertical or large cities)
* Adjust how long a villager will travel to Points of Interest
* Adjust how close a villager needs to get to use a Point of Interest
* Villagers only gather food items, or items needed by their profession (seeds for farmers, etc)
* Optional - Heal Villagers when they wake up (like Bedrock)
* Optional - Prioritize villagers avoiding cactus, water, rails, trapdoors, and powdered snow.
* Optional - Pathfinding fix for villagers becoming "stuck" near walls/fences
* Optional - Prevent villagers from climbing up ladders and vines (they don't know how to climb back down, poor dears)
* Adjust the distance villagers will consider "safe" for jumping down from ledges
* Optional - Villagers can consume melon slices, pumpkin pie, and cooked cod and salmon for food
* Optional - Prevent villagers from breeding (for when you've got a bed that isn't pathable that is causing a population
  boom)
* Optional - Require a free workstation when Villagers decide to have a baby
* Optional - Prevent villagers from trampling crops
* Optional - Stop villagers from seeking workstations and meeting points in the middle of the night
* Optional - No nitwit villagers
* Optional - Every villager is a nitwit
* Optional - Every villager is a baby
* Optional - Babies never grow up
* Optional - Configure the time it takes for a baby villager to grow up

### Villager Professions

* Optional - Armorers seek out and heal Iron Golems during work hours
* Optional - Clerics seek out and heal Villagers and Players during work hours
* Adjust how far farmers will look for crops in x/z and y planes.
* Optional - Farmers prefer to plant the same type of crop they just harvested
* Optional - Farmers can plant and harvest melons and/or pumpkins
* Optional - Butchers can feed chickens, cows, pigs, rabbits, and sheep if they have the appropriate food
* Optional - Fletchers can feed chickens if they have seeds.
* Optional - Leatherworkers can feed cows if they have wheat.
* Optional - Shepherds can feed sheep if they have wheat.
* Adjust how far villagers will look for animals to feed.
* Prevent the villager from feeding if there are too many specific animals in range.
* Optional - Fisherman go fishing for raw cod/salmon, will cook them when restocking at their workstations.
* Optional - Librarians will seek out nearby bookcases.

### Golems

* Optional - Golems avoid cactus, waters, rails, trapdoors, and powdered snow.
* Optional - Golems don't attack players
* Optional - Prevent Villagers from summoning Golems (for when there's a Golem overpopulation problem)
* Optional - Choose how many golems can spawn in a specified radius
* Optional - Underwater Golems path back to shore when done attacking monsters.
* Optional - Provide a maxmum range for Golems to path away from the nearest meeting point.
* Optional - Prevent Golems from climbing up ladders/vines.

### Cats

* Optional - Choose how many cats can spawn in a specified radius
* Optional - Cats don't despawn
* Optional - Black cats can spawn at any time (not just during a full moon)
* Optional - Every cat is a black cat (as ordered by my cat)
* Optional - Provide a maxmum range for cats to path away from the nearest meeting point.
* Optional - Prevent cats from climbing up ladders/vines.

### Debug

* Villagerstats command which gives you a summary how how many Villagers are in your town and what their occupations
  are, how many are homeless, number of golems, number and types of cats, and how many open beds are available, in an
  easy-to-read book format.
* Villagerinfo command which tells you a summary of a Villager standing before you - where there bed is, where their
  workstation is, where their meeting place is, and what they are holding in their inventory. If looking at a Point of
  Interest (such as a workplace, bed, or bell), it will tell you whether that POI is claimed by a Villager.
* VillagerSetPOI command which lets you toggle whether a block (such as a bed, workplace, or bell) is a valid Point Of
  Interest or not, so you can disable decorative blocks from being considered a workplace, and reserve your own bed to
  sleep in at night.
* Villager Info overlay - for single player and integrated servers, you can see the data from Villagerinfo update as you
  look around, useful for quick debugging during the Villager's meeting times.

## Screenshots
![general_1](https://user-images.githubusercontent.com/56774556/210156131-72da4c82-6416-4b0f-9958-614c5932a1ba.png)
![general_2](https://user-images.githubusercontent.com/56774556/210156132-fcfc03a9-201d-4358-a640-1cdd8ec77e66.png)
![pathfinding_1](https://user-images.githubusercontent.com/56774556/210156136-6cca8449-7cf4-4e93-a7ce-9da19bd77936.png)
![pathfinding_2](https://user-images.githubusercontent.com/56774556/210156137-7d5f7d72-8ec7-4609-ba5d-2f8e3a242f0a.png)
![profession_1](https://user-images.githubusercontent.com/56774556/210156138-ecb9a421-083c-4581-84b5-6e2e04da751e.png)
![profession_2](https://user-images.githubusercontent.com/56774556/210156139-76f1b268-d6cb-473c-9c89-406a8b5c525c.png)
![profession_3](https://user-images.githubusercontent.com/56774556/210156140-c7cb2a03-a88d-4156-997c-0c283aa90f82.png)
![profession_4](https://user-images.githubusercontent.com/56774556/210156141-ae345d5d-4fe0-442c-8b66-13a9ef125ef7.png)
![golems_1](https://user-images.githubusercontent.com/56774556/210156133-8fd42d53-5d7e-447b-a3bf-7f4617026a89.png)
![golems_2](https://user-images.githubusercontent.com/56774556/210156134-45446127-b6a4-419a-b4ee-1d758745745a.png)
![golems_3](https://user-images.githubusercontent.com/56774556/210156135-e530310d-a4e7-4500-a4c5-fbd87bd3c0aa.png)
![cats_1](https://user-images.githubusercontent.com/56774556/210156128-e7ce17ba-35db-48fe-9c2b-90a2a9f7eafb.png)
![cats_2](https://user-images.githubusercontent.com/56774556/210156129-d138e4ff-0d00-4403-b931-cb16e16e9b10.png)
![debug_1](https://user-images.githubusercontent.com/56774556/210156130-71e33651-6a52-4c9c-8172-e60833bb8eeb.png)

## Dependencies

If you want to run this on a dedicated server, you will need the "Server Translation API" version 1.4.17 or higher.

To edit the mod's options on the client, you will need to the "Mod Menu" mod.

## Credits

Thanks to [SuperSaiyanSubtlety's Enchantment Lore fabic mod](https://gitlab.com/supersaiyansubtlety/enchantment_lore)
for writing code under the [MIT license](https://will-lucic.mit-license.org) for opening the book UI on the
server.

Thanks to kressety for the Simplified Chinese translations.
Thanks to Cape-City for the German translations.