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



# [0.3.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.2.0...v0.3.0) (2023-11-17)


### Bug Fixes

* add converter for pagination parameters ([0991185](https://github.com/b-partners/bpartners-annotator-api/commit/09911858eaab24e1517b26d43d2a8f89b92144e1))
* Annotation ID is not a generated value, set task job as the saved one before updating it ([082a8cf](https://github.com/b-partners/bpartners-annotator-api/commit/082a8cfb19a92882669987cdbb901f3800f2bd16))
* java client deps version ([4c6a007](https://github.com/b-partners/bpartners-annotator-api/commit/4c6a007c64b223e2187a7c618ebb02a57ee656a9))
* use javax instead of jakarta ([af5b7eb](https://github.com/b-partners/bpartners-annotator-api/commit/af5b7ebdc2a5fa4aaae9c7bda0330c050f9405e9))


### Features

* add name attribute to job ([2919a0a](https://github.com/b-partners/bpartners-annotator-api/commit/2919a0a0defb94c8891100606779ccbe48a1feb5))
* manually update code version ([2aaa464](https://github.com/b-partners/bpartners-annotator-api/commit/2aaa464de4a839a5c9890d2bf8455b811fc0fd20))
* paginate get all tasks ([213fd79](https://github.com/b-partners/bpartners-annotator-api/commit/213fd796aeca076147a2364ed6402375cc79a016))


### Reverts

* Revert "chore(release): rename react_app env to next_public env" ([197a58d](https://github.com/b-partners/bpartners-annotator-api/commit/197a58d8e820a70155ae3913097c86b6c72b1f28))



# [0.2.0](https://github.com/b-partners/bpartners-annotator-api/compare/v0.1.1...v0.2.0) (2023-11-03)


### Bug Fixes

* if job folder path is null then it's valid, check for ownerEmail null value before checking pattern ([9330054](https://github.com/b-partners/bpartners-annotator-api/commit/93300540571e13a0e92d436339bf1563f985f705))
* label ID is not a generated value, drop unique constraint on label name ([82f26bd](https://github.com/b-partners/bpartners-annotator-api/commit/82f26bd3de8acf0ce535bca52f3087b6291a18e5))
* tests ([03c8033](https://github.com/b-partners/bpartners-annotator-api/commit/03c8033c498b238154d762d3fab70f13496fd85c))


### Features

* annotate tasks and save annotations ([501bc5c](https://github.com/b-partners/bpartners-annotator-api/commit/501bc5cff374c088ad81ab9d543d907848c259ec))
* configure events ([241e927](https://github.com/b-partners/bpartners-annotator-api/commit/241e9277b9a007478adb4c060f1b095329a8ca6c))
* configure ses send email ([5dac6ef](https://github.com/b-partners/bpartners-annotator-api/commit/5dac6ef5218e9444423752fdef839bbaa36bea8f))
* implement jobs endpoints ([e615902](https://github.com/b-partners/bpartners-annotator-api/commit/e615902123fa777f766efc09ca84a23b503cbac3))
* implement tasks endpoints ([6850f9b](https://github.com/b-partners/bpartners-annotator-api/commit/6850f9b764db0c6530dded1c573a9f4e44f68e55))
* send email to job owner when job initialized ([408f454](https://github.com/b-partners/bpartners-annotator-api/commit/408f454a88526e41d22923cf59f25bbc803005f6))
* update job attributes ([5dc01f5](https://github.com/b-partners/bpartners-annotator-api/commit/5dc01f567270aa393494823d2889129abe4baafb))
* validate data on entry ([4d8cda2](https://github.com/b-partners/bpartners-annotator-api/commit/4d8cda26d4da222d0225956ed40caa328e3a40f1))



## [0.1.1](https://github.com/b-partners/bpartners-annotator-api/compare/v0.1.0...v0.1.1) (2023-10-18)


### Bug Fixes

* npm install on react client ([218d4e8](https://github.com/b-partners/bpartners-annotator-api/commit/218d4e85c72b8359fd376e969412a92be9c73abb))



# [0.1.0](https://github.com/b-partners/bpartners-annotator-api/compare/c8bffe77f83466cdc066fb6e71ec3ee1f948249a...v0.1.0) (2023-10-18)


### Features

* **docs-api:** create api specification ([9890918](https://github.com/b-partners/bpartners-annotator-api/commit/98909182742feb18d282b9e70e9e5b2d32269759))
* **docs-api:** specify exceptions components ([3695f6d](https://github.com/b-partners/bpartners-annotator-api/commit/3695f6d58661d447cb3d16098e8c0cc9ac9bbffd))
* implement healthController ([c8bffe7](https://github.com/b-partners/bpartners-annotator-api/commit/c8bffe77f83466cdc066fb6e71ec3ee1f948249a))
* turn spring boot into serverless appication ([e71c8ff](https://github.com/b-partners/bpartners-annotator-api/commit/e71c8ff6485fe539bc491f38a40fc8d471a85eea))



