var browserify = require('browserify'),
    glob = require('glob'),
    streamify = require('gulp-streamify'),
    uglify = require('gulp-uglify'),
    connect = require('gulp-connect'),
    source = require('vinyl-source-stream'),
    gulp = require('gulp');

var paths = {
    root : 'app/',
    src : 'app/js/',
    dist : '../resources/html/'
};

var liveReload = true;

gulp.task('browserify', function () {
  return browserify(paths.src + 'app.js', {debug: true})
  .bundle()
  .pipe(source('app.js'))
  .pipe(gulp.dest(paths.dist))
  .pipe(connect.reload());
});

gulp.task('browserify-min', ['ngAnnotate'], function () {
  return browserify(paths.root + 'ngAnnotate/app.js')
  .bundle()
  .pipe(source('app.min.js'))
  .pipe(streamify(uglify({mangle: false})))
  .pipe(gulp.dest(paths.dist));
});


gulp.task('server', ['browserify'], function () {
  connect.server({
    root: 'app',
    livereload: liveReload,
  });
});

gulp.task('watch', function () {
  gulp.start('server');
  gulp.watch([
    paths.src + '**/*.js',
    '!' + paths.src + 'third-party/**',
    paths.test + '**/*.js',
  ], ['browserify']);
});

gulp.task('default', ['browserify']);
