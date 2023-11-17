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



