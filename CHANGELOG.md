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



# [0.4.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.3.0...v0.4.0) (2023-11-17)


### Bug Fixes

* allow flyway migration out of order ([98e4981](https://github.com/b-partners/bpartners-annotator-api/commit/98e498177ac6512cc8f059a094b9330f591b4afc))
* refactor env vars ([13f3dd2](https://github.com/b-partners/bpartners-annotator-api/commit/13f3dd2d82acc8624670b8024e94ada2a8a6d972))


### Features

* spring security ([#38](https://github.com/b-partners/bpartners-annotator-api/issues/38)) ([28b1ac1](https://github.com/b-partners/bpartners-annotator-api/commit/28b1ac19158f20b610905384235b0350b886ac68))



