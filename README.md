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
* Optional - Heal Villagers when they wake up (like Bedrock)
* Optional - Prioritize villagers avoiding cactus, water, and rails
* Optional - Pathfinding fix for villagers becoming "stuck" near walls/fences
* Optional - Prevent villagers from climbing up ladders and vines (they don't know how to climb back down, poor dears)
* Adjust the distance villagers will consider "safe" for jumping down from ledges
* Optional - Villagers can consume melon slices and pumpkin pie for food
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

### Golems

* Optional - Golems avoid cactus
* Optional - Golems avoid water
* Optional - Golems avoid rails
* Optional - Golems don't attack players
* Optional - Prevent Villagers from summoning Golems (for when there's a Golem overpopulation problem)
* Optional - Choose how many golems can spawn in a specified radius

### Cats

* Optional - Choose how many cats can spawn in a specified radius
* Optional - Cats don't despawn
* Optional - Black cats can spawn at any time (not just during a full moon)
* Optional - Every cat is a black cat (as ordered by my cat)

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
![Villagers General #1](https://user-images.githubusercontent.com/56774556/204126062-88d94ea3-b933-4671-82a3-e11aa09775a8.png)
![Villagers General #2](https://user-images.githubusercontent.com/56774556/204126072-a67d7cfc-452e-41b5-b1e3-eace50e8664e.png)
![Villagers General #3](https://user-images.githubusercontent.com/56774556/204126087-6df8ea10-460f-42e6-b437-2f22371fbf1c.png)
![Villagers General #4](https://user-images.githubusercontent.com/56774556/204126098-47b53700-57e8-4a84-8ff8-b750e188e846.png)
![Villagers Professions #1](https://user-images.githubusercontent.com/56774556/204126106-4dbd3999-48f5-400d-bb30-46eb1060980d.png)
![Villagers Professions #2](https://user-images.githubusercontent.com/56774556/204126110-21d06d08-51fa-461a-8b20-46c63307e1da.png)
![Golems #1](https://user-images.githubusercontent.com/56774556/204126114-533644b7-63d6-420b-97b9-290a0306e456.png)
![Golems #2](https://user-images.githubusercontent.com/56774556/204126119-3a9939d6-1a84-4d54-aa48-5253fb8f1350.png)
![Cats #1](https://user-images.githubusercontent.com/56774556/204126122-402e6e94-b971-4be2-bc67-e615ba97c84f.png)
![Debug #1](https://user-images.githubusercontent.com/56774556/204126125-f4f7ed8a-b3ad-424e-8fbb-e8a31ce7ea5b.png)
![Villager Info Overlay](https://user-images.githubusercontent.com/56774556/204127033-5e14ab13-b208-4aa1-80c5-67f05b3a8503.png)

![Villager Stats Title](https://user-images.githubusercontent.com/56774556/204127110-ae7cd50e-c643-4b67-b427-13e05e38a67a.png)
![Villager Stats Jobs](https://user-images.githubusercontent.com/56774556/204127111-64508eca-3aea-4b73-bcae-4b6b7df7b8e5.png)
![Villager Stats Workstations](https://user-images.githubusercontent.com/56774556/204127120-d554df12-e05d-4a5f-addc-7835c0ef4386.png)
![Villager Stats - Homeless](https://user-images.githubusercontent.com/56774556/204127188-2c36a9cd-247b-44bc-830d-564376bb801a.png)
![Villager Stats Beds](https://user-images.githubusercontent.com/56774556/204127134-01998696-2cfe-43be-b397-ec9161af4905.png)
![Villager Stats Golems](https://user-images.githubusercontent.com/56774556/204127145-8b29a4e7-7731-4a4d-96b9-10681715d5ef.png)
![Villager Stats Cats](https://user-images.githubusercontent.com/56774556/204127151-95777fd9-f6a9-4ee5-a864-b2f973652ed7.png)

## Dependencies

If you want to run this on a dedicated server, you will need the "Server Translation API" version 1.4.17 or higher.

To edit the mod's options on the client, you will need to the "Mod Menu" mod.

## Credits

Thanks to [SuperSaiyanSubtlety's Enchantment Lore fabic mod](https://gitlab.com/supersaiyansubtlety/enchantment_lore)
for writing code under the [MIT license](https://will-lucic.mit-license.org) for opening the book UI on the
server.

Thanks to kressety for the Simplified Chinese translations.