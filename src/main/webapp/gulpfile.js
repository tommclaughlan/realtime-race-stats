var browserify = require('browserify'),
    glob = require('glob'),
    streamify = require('gulp-streamify'),
    uglify = require('gulp-uglify'),
    connect = require('gulp-connect'),
    source = require('vinyl-source-stream'),
    buffer = require('vinyl-buffer'),
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
  .pipe(source('bundle.js'))
  .pipe(buffer())
  .pipe(uglify())
  .pipe(gulp.dest(paths.dist))
  .pipe(connect.reload());
});

gulp.task('watch', ['browserify'], function () {
  gulp.watch([
    paths.src + '**/*.js',
    '!' + paths.src + 'third-party/**',
    paths.test + '**/*.js',
  ], ['browserify']);
});

gulp.task('default', ['browserify']);
