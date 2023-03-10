### Static assets which are served up by github pages

* [Live View](https://vzxplnhqr.github.io/fliptxo)
* [Source Code](https://github.com/VzxPLnHqr/fliptxo)

To deploy to github pages, from root of repository run:

1. `sbt fullLinkJS/esBuild` (compiles the scala code to javascript and bundles)
2. `git add js/target/esbuild/bundle.js js/target/esbuild/bundle.js.map`
3. `git commit -m "updated static assets"`
4. `git subtree push --prefix js origin gh-pages` (or [read this](https://stackoverflow.com/questions/33172857/how-do-i-force-a-subtree-push-to-overwrite-remote-changes))
