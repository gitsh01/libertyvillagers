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


 