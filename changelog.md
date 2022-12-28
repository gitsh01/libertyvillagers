
## [1.0.9+fabric+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.9+fabric+1.19.2) (2022-12-28)

### Features

 -  Added option for butchers to feed rabbits, chickens, sheep, and pigs. ([32524bf5ef99ef0](https://gitlab.com/html-validate/html-validate/commit/32524bf5ef99ef091a7950a505403535a7d5485b))
 -  Added option for leatherworkers and butchers to feed cows with a specified range and max number of cows. ([ecd537abec64b74](https://gitlab.com/html-validate/html-validate/commit/ecd537abec64b74a79b0412220190c6bf56e9f4c))
 -  Added option to prevent cats from climbing up ladders/vines they can't climb down. ([454961b1ef53cf9](https://gitlab.com/html-validate/html-validate/commit/454961b1ef53cf9439c1f44c98d4e1b93daa4d78))
 -  Increased range of Golems searching for scared villagers from 32 to findPOIRange. ([682587dfeffb004](https://gitlab.com/html-validate/html-validate/commit/682587dfeffb00437014674a213135bd31ccb24a))
 -  Added option to make Golems move back to shore when in water. ([f8bba3d96d6b6ee](https://gitlab.com/html-validate/html-validate/commit/f8bba3d96d6b6ee73d47c5c9eb2c3e9dbdd0573d))
 -  Added option to force Cats to stay within a specified range of the nearest meeting bell. ([bc240f038b7de1c](https://gitlab.com/html-validate/html-validate/commit/bc240f038b7de1ca9eb00691e4ae56336599d64b))
 -  Added option to force Iron Golems to stay within a specified range of the nearest meeting bell. ([f02472e272d0ca6](https://gitlab.com/html-validate/html-validate/commit/f02472e272d0ca6d6b33e5d548a045bc00309d65))

### Bug Fixes

 -  Added optional config for Librarians seeking out books. ([0b36e7c3b861d84](https://gitlab.com/html-validate/html-validate/commit/0b36e7c3b861d8431bc3e06c3dcf554aa4bf8e86))
 -  Iron Golems shouldn't run after drowned in the water. ([b20222a77d28083](https://gitlab.com/html-validate/html-validate/commit/b20222a77d28083cbb28c96088eb827b240ccb4a))
 -  Don't always run the ReturnToShoreGoal on the first load. ([49501979a26abf3](https://gitlab.com/html-validate/html-validate/commit/49501979a26abf3759c5f7760b2ff03cee29bb1d))
 -  Improve golem pathfinding to shore. ([435dbd944ee67cb](https://gitlab.com/html-validate/html-validate/commit/435dbd944ee67cb0fdde17ed9cd3039a20dfb02b))


## [1.0.8+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.8+1.19.2) (2022-12-20)

### Bug Fixes

 -  Fixed a crash on tick in server mode with optimizations. ([aafd4538b40256f](https://gitlab.com/html-validate/html-validate/commit/aafd4538b40256f7b658da29f65a90a140e18e23))

## [1.0.7+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.7+1.19.2) (2022-12-18)

### Features

 -  Added honey level and number of bees to villager info. ([ea93dd1c0de5f89](https://gitlab.com/html-validate/html-validate/commit/ea93dd1c0de5f894a8ace9aa80bdec9848a253a3))
 -  Added options for golems to avoid trapdoors and powdered snow. ([bd66ae8aaa80f98](https://gitlab.com/html-validate/html-validate/commit/bd66ae8aaa80f986688c28c8c5fffeae8b4da3e3))
 -  Added option for villagers to avoid powdered snow. ([2be62cb9c3611da](https://gitlab.com/html-validate/html-validate/commit/2be62cb9c3611dab785aa432a2de9a50532a1ec7))
 -  Added a "pathfinding" settings tab to make pathfinding settings easier to find. ([9ee5f01241313fa](https://gitlab.com/html-validate/html-validate/commit/9ee5f01241313fabd5c18761d8af9e853592f182))
 -  Added "villagers avoid trapdoors" option for keeping villagers from smashing into open trapdoors used as decorative fences. ([49735d962fb3238](https://gitlab.com/html-validate/html-validate/commit/49735d962fb32380244b75c8f0ba7b7555356698))
 -  Added "villagerreset", which forces villagers to find new meeting points and jobs near their current beds. ([5a3156d3d0511f2](https://gitlab.com/html-validate/html-validate/commit/5a3156d3d0511f2046c1573c232bd64b926c3b1f))

### Bug Fixes

 -  Fix for catSpawnLimitRange not actually working for the spawn limit range. ([a23aa66f64dc917](https://gitlab.com/html-validate/html-validate/commit/a23aa66f64dc917c5bae5daf5709c0e4d8e1cfa5))
 -  Improved the range for sleep task to account more accurate pathfinding. ([7d6c93f33207cfe](https://gitlab.com/html-validate/html-validate/commit/7d6c93f33207cfefdc437c781eac22a6618dc9cb))
 -  Fix for villagers spamming fuzzy targeting when stuck. ([98553aa05faab8f](https://gitlab.com/html-validate/html-validate/commit/98553aa05faab8fab80f5c7d9bc790330f96e10d))
 -  Improved "stuck" handling for pathfinding. ([f326fcdbb0b4007](https://gitlab.com/html-validate/html-validate/commit/f326fcdbb0b4007b07c483a3db570899fb076c81))
 -  Improvements to applying fuzzy logic and teleporting when the villager is stuck. ([23cfb5485fc0206](https://gitlab.com/html-validate/html-validate/commit/23cfb5485fc0206beba883a8b9e515e7a65bd998))
 -  Improving farmer logic when harvesting crops. ([085a894acb1e990](https://gitlab.com/html-validate/html-validate/commit/085a894acb1e9906b80b1b8559ccea08512589a7))

 ## [1.0.6+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.6+1.19.2) (2022-12-07)

### Features

 -  Added support for villagers and golems avoiding rails. Default is true. Closes #33 ([a6d6387e6e1369a](https://gitlab.com/html-validate/html-validate/commit/a6d6387e6e1369a748165415c4bf5ce33ae89ef5))

### Bug Fixes

 -  Better handling of when villagers get "stuck" on each other. At 10 seconds, attempt to fuzzy logic to a new location. At 100 seconds, teleport to the desired next path location. ([be64a3735651933](https://gitlab.com/html-validate/html-validate/commit/be64a37356519332b0c3758b2042135622e65412))
 -  Fixed villagers and golems getting stuck on Azalea bushes. ([9b2df1baab7bf24](https://gitlab.com/html-validate/html-validate/commit/9b2df1baab7bf240b4482616910ab47e7849e890))
 -  Fixed villager info overlay not showing villager memories in integrated server mode. ([997ffcc88d076bf](https://gitlab.com/html-validate/html-validate/commit/997ffcc88d076bfef6643d04fa1d542d1453ccaa))
 -  Fixed untranslated string "baby" in villager stats. ([ee54c7dce01ddfe](https://gitlab.com/html-validate/html-validate/commit/ee54c7dce01ddfe684f6273310806b55cc337051))

## [1.0.5+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.5+1.18.2) (2022-12-01)

### Bug Fixes

 -  Removed console debug spam. ([8f553294b82ce81](https://gitlab.com/html-validate/html-validate/commit/8f553294b82ce815538c35f10019673ea97999ec))
 -  Further improvements for villager bed pathfinding. ([c9561a6cd872c4e](https://gitlab.com/html-validate/html-validate/commit/c9561a6cd872c4ee3f51caa68bf2b8b33d923b8e))

 
## [1.0.4+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.4+1.18.2) (2022-11-28)

### Features

 -  Improvements for villager bed pathfinding: Checking to see if the bed is occupied before walking towards it. ([414ede405f42425](https://gitlab.com/html-validate/html-validate/commit/414ede405f4242539ad5a1e3e873f3002c11b447))
 -  Improved villager pathfinding at the job site. ([c7a51bab4ec76bf](https://gitlab.com/html-validate/html-validate/commit/c7a51bab4ec76bf0e243f720377546f29c68cf5e))
 -  Farmers can now plant and harvest melons and pumpkins. ([b5dfb0fb69cdced](https://gitlab.com/html-validate/html-validate/commit/b5dfb0fb69cdcede7967059890a58c4d6163061b))
 -  Made the amount of time for a villager to grow up configurable. ([48081109544c37c](https://gitlab.com/html-validate/html-validate/commit/48081109544c37c233aa5e5b868fc0cf3b364432))
 -  Added options to make every cat a black cat, and to have black cats spawn regardless of the moon phase. ([b1a44c8c321608c](https://gitlab.com/html-validate/html-validate/commit/b1a44c8c321608c024739fee0653befd5b129f1e))
 -  Added golem names to the golems page of villager stats. ([d7720d892eee72d](https://gitlab.com/html-validate/html-validate/commit/d7720d892eee72db9d36ffece4f62875a9038994))
 -  Added proper translations for cat variants. ([9a2cd65fd51a1db](https://gitlab.com/html-validate/html-validate/commit/9a2cd65fd51a1db733c39c0aa0e132cd730ba4ea))

### Bug Fixes

-  Fixed translations of villager professions in VillagerInfo. ([36fee31bb2f44f6](https://gitlab.com/html-validate/html-validate/commit/36fee31bb2f44f62c643cc12f69830b9ab27239f))
-  Fix for crash when doing villagerinfo at a villager who is about to die. ([bc9b31a3e47b6eb](https://gitlab.com/html-validate/html-validate/commit/bc9b31a3e47b6ebd6973070e47c043b92c4599ce))

## [1.0.3+1.18.2](https://gitlab.com/html-validate/html-validate/compare/1.0.3+1.18.2) (2022-11-19)

### Features

 -  Added support for 1.18.2. ([0dd1f1000e0b4b6](https://gitlab.com/html-validate/html-validate/commit/0dd1f1000e0b4b67b0accb3db528c148e3826d2d))


 