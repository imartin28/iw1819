"use strict"
$(function () {

     let videoJs = videojs('videoPlay');
     videoJs.one('play', function () {
          this.currentTime(0);
     });

});