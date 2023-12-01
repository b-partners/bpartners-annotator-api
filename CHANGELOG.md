# [1.0.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.10.1...v1.0.0) (2023-12-01)


### Bug Fixes

* ADMIN can't annotate tasks or get random Task, it is reserved to annotators ([842d4c0](https://github.com/b-partners/bpartners-annotator-api/commit/842d4c07c4ef700c02207cb037faeb2e27312c60))
* SelfMatcher DOES NOT handle ADMIN access ([e812d99](https://github.com/b-partners/bpartners-annotator-api/commit/e812d994a77f1d958372b2f4e95e36eea0675e5d))


* feat!: group annotations by batch ([61cc339](https://github.com/b-partners/bpartners-annotator-api/commit/61cc339f35f6a9807ebc65012ff259047f2763c0))


### Features

* admin annotation batch review ([602a1d3](https://github.com/b-partners/bpartners-annotator-api/commit/602a1d39be412df24c1b0416ebb840d0c7505a28))
* get annotations and annotation ([1ffd591](https://github.com/b-partners/bpartners-annotator-api/commit/1ffd5917613e4c33ecd05b62a369e32ecece021c))
* let user see annotation batches ([4101bde](https://github.com/b-partners/bpartners-annotator-api/commit/4101bded5dcd68bbaa49a4ad47addba9309b8334))
* upgrade poja to v4.0.0 ([1ed1c63](https://github.com/b-partners/bpartners-annotator-api/commit/1ed1c635aab19c9a8ea0bb0ac9afb5b8de2b94b2))
* user get annotation batch reviews and annotation batch review ([af9bb62](https://github.com/b-partners/bpartners-annotator-api/commit/af9bb62058bc09890be850e7050d1e954dfc8279))


### BREAKING CHANGES

* annotations are now grouped in a batch



## [0.10.1](https://github.com/b-partners/bpartners-annotator-api/compare/v0.10.0...v0.10.1) (2023-11-29)


### Bug Fixes

* JobCreated ignores some properties inferred from getter ([833a109](https://github.com/b-partners/bpartners-annotator-api/commit/833a109645f01f1396e3430ac6edbc7715cc7842))



# [0.10.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.9.0...v0.10.0) (2023-11-28)


### Features

* implement update user team endpoint ([605550f](https://github.com/b-partners/bpartners-annotator-api/commit/605550fabf3baef0d46ea5cec7e65e4d1cec69b0))



# [0.9.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.8.1...v0.9.0) (2023-11-23)


### Bug Fixes

* map imageUri correctly ([dc3e324](https://github.com/b-partners/bpartners-annotator-api/commit/dc3e3245cf2d210cb788a6b1b9a60b4d6df66c83))


### Features

* job has TaskStatistics ([eddc2a9](https://github.com/b-partners/bpartners-annotator-api/commit/eddc2a90c92665b89456039f4a0a36ed7ed5df8e))



## [0.8.1](https://github.com/b-partners/bpartners-annotator-api/compare/v0.8.0...v0.8.1) (2023-11-23)


### Bug Fixes

* imageUri instead of imageURI ([4b08d2b](https://github.com/b-partners/bpartners-annotator-api/commit/4b08d2b7a18a024770f9dfb02d9f0bfac0f6de81))



# [0.8.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.7.1...v0.8.0) (2023-11-23)


### Bug Fixes

* userId is null when getting new task ([32e4aef](https://github.com/b-partners/bpartners-annotator-api/commit/32e4aefe6c5cb0045ac6b112647426a8837b65cd))


### Features

* add task statistics ([#65](https://github.com/b-partners/bpartners-annotator-api/issues/65)) ([8af1ecc](https://github.com/b-partners/bpartners-annotator-api/commit/8af1eccb02ebb207ba532823e8efb95af3bbfa22))



## [0.7.1](https://github.com/b-partners/bpartners-annotator-api/compare/v0.7.0...v0.7.1) (2023-11-23)


### Bug Fixes

* add id generation strategy for user and team ([058440d](https://github.com/b-partners/bpartners-annotator-api/commit/058440d48b3d48913c410d969df48abb282318a7))



# [0.7.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.6.0...v0.7.0) (2023-11-22)


### Bug Fixes

* allow OPTIONS on all paths for CORS ([715b46f](https://github.com/b-partners/bpartners-annotator-api/commit/715b46fe97d4664b0ee7d9f5734ca79c62fae2b8))
* team id is not a generated value ([09c7373](https://github.com/b-partners/bpartners-annotator-api/commit/09c7373f87f4899ad82e693fa6dab178d7d62250))
* use post method for user creation ([36bb752](https://github.com/b-partners/bpartners-annotator-api/commit/36bb75225f915064e903949ae5152f78efba556b))


### Features

* create users with post method ([#58](https://github.com/b-partners/bpartners-annotator-api/issues/58)) ([77d1f35](https://github.com/b-partners/bpartners-annotator-api/commit/77d1f35cca5cf421a6b412e275852c697b104907))



# [0.6.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.5.0...v0.6.0) (2023-11-17)


### Bug Fixes

* correct whoami authorization and tags in api doc ([449be1e](https://github.com/b-partners/bpartners-annotator-api/commit/449be1e054efa60d236fe379dd87a1fc5f0242d5))
* mismapped team in UserMapper ([6db8905](https://github.com/b-partners/bpartners-annotator-api/commit/6db89051b7e28dc04d44a6a7ddf57b17fe0befc6))


### Features

* users ([2680fc4](https://github.com/b-partners/bpartners-annotator-api/commit/2680fc44b55f91b3990d1616682df2d1f69ad9b5))
* whoami ([fe73e5d](https://github.com/b-partners/bpartners-annotator-api/commit/fe73e5dc207f51e77e5105f34cc9f1f06199c9fb))



# [0.5.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.4.0...v0.5.0) (2023-11-17)


### Features

* create teams ([140ae66](https://github.com/b-partners/bpartners-annotator-api/commit/140ae66b89278a260c2e04426193b7fbd863afad))
* get teams ([b90c12c](https://github.com/b-partners/bpartners-annotator-api/commit/b90c12cd5bb8897918024a05dc757d8f315f156c))



