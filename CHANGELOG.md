## [2.4.1](https://github.com/b-partners/bpartners-annotator-api/compare/v2.4.0...v2.4.1) (2024-02-02)


### Bug Fixes

* health endpoints are now publicly accessible ([ba11f45](https://github.com/b-partners/bpartners-annotator-api/commit/ba11f45d46190f302927516ff332e0a503b295db))
* throw bad request if task status is set to correct without setting userId ([7084261](https://github.com/b-partners/bpartners-annotator-api/commit/7084261bc242738522828f8abc9f2c6f2f340611))



# [2.4.0](https://github.com/b-partners/bpartners-annotator-api/compare/v2.1.0...v2.4.0) (2024-01-30)


### Bug Fixes

* add unique constraint to job.name and handle duplicates ([44c60b4](https://github.com/b-partners/bpartners-annotator-api/commit/44c60b452a38b91caeb5b2619e60557f8de3f47a))
* Annotation.Polygon.Point must not be null ([fe3a43f](https://github.com/b-partners/bpartners-annotator-api/commit/fe3a43f7bb821bd276c7a2a770cd2d8719803a15))
* annotations not correctly saved in batch ([b703531](https://github.com/b-partners/bpartners-annotator-api/commit/b703531d7d3414c3754bca0572748865df74bc89))
* available tasks are either your own (to_correct, under_completion) tasks or any pending task ([6527e98](https://github.com/b-partners/bpartners-annotator-api/commit/6527e9874ed323c84d69985992d81e9adc46ec50))
* correctly name variables and correct sql logic for querying annotation batch review ([9b92929](https://github.com/b-partners/bpartners-annotator-api/commit/9b929296797503196d4d172bf6b3222a82cec2b4))
* labels are not updatable on a job ([34eba69](https://github.com/b-partners/bpartners-annotator-api/commit/34eba69482898780fde4e79e37e09829d7b6ee8c))
* some annotation attributes are mandatory, label is not nullable anywhere ([7b7411c](https://github.com/b-partners/bpartners-annotator-api/commit/7b7411cbdca0886ac24d8a8a76851887d1723dd5))
* vgg task key is its filename ([e3727d7](https://github.com/b-partners/bpartners-annotator-api/commit/e3727d75c35ef0d50f170c3e957f485176f9c714))


### Features

* add creationDatetime to annotationBatch fields ([b33f9b7](https://github.com/b-partners/bpartners-annotator-api/commit/b33f9b7048350413ec092a4c2492ad4c18e4e713))
* add remainingTaskForUserId in TaskStatistics attribute ([7dff804](https://github.com/b-partners/bpartners-annotator-api/commit/7dff804b4dde1053700092cb58652893beecd192))
* synchronous export for VGG ([1884c4c](https://github.com/b-partners/bpartners-annotator-api/commit/1884c4c07f068142e954580f172fdc2552b16262))



# [2.1.0](https://github.com/b-partners/bpartners-annotator-api/compare/v2.0.0...v2.1.0) (2023-12-07)


### Bug Fixes

* only complete job if it finishes all tasks ([bc10020](https://github.com/b-partners/bpartners-annotator-api/commit/bc10020fad15cbcafa618ae07d7beeb32e9b04a0))


### Features

* filter by status and paginate jobs ([1aefefa](https://github.com/b-partners/bpartners-annotator-api/commit/1aefefa292d9c2a8cb8a7ce3b51debebc4844605))
* filter tasks by status and userId ([608ef02](https://github.com/b-partners/bpartners-annotator-api/commit/608ef021ad5dffb6e1d85410f874bc58f53efc53))
* whenever reviewed, the annotation's task's job status will become TO_CORRECT ([e1f2aa8](https://github.com/b-partners/bpartners-annotator-api/commit/e1f2aa8b5bbbd3677bc4e612ce24c22616c73d23))



# [2.0.0](https://github.com/b-partners/bpartners-annotator-api/compare/v1.0.0...v2.0.0) (2023-12-01)


### Bug Fixes

* wrong value for isTaskAnnotable variable ([844d66b](https://github.com/b-partners/bpartners-annotator-api/commit/844d66bb21b82c0e1e24ab7053acc5a66ab21181))


* feat!: annotation_batch_review reviews a batch ([3a7d16d](https://github.com/b-partners/bpartners-annotator-api/commit/3a7d16d5d90e190be98bf844baa283b5753f6824))


### BREAKING CHANGES

* annotation_reviews are grouped in a batch_review



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



