<script>
function randomColor() {
  var r = Math.floor(Math.random() * 128) + 128;
  var g = Math.floor(Math.random() * 128) + 128;
  var b = Math.floor(Math.random() * 128) + 128;
  var hexR = r.toString(16).padStart(2, '0');
  var hexG = g.toString(16).padStart(2, '0');
  var hexB = b.toString(16).padStart(2, '0');
  var colorCode = '#' + hexR + hexG + hexB;
  return colorCode;
}

export default {
  data() {
    var diamonds = new Array(36).fill(1).map(function (_, index) {
      var size = 30 + 4 * index;
      var top = (1.15 - Math.random() * Math.random()) * window.innerHeight;
      var left = Math.random() * window.innerWidth;
      return {
        index: index,
        style: {
          width: size + 'px',
          height: size + 'px',
          top: top + 'px',
          left: left + 'px',
          background: randomColor(),
        },
      };
    });
    return {
      diamonds: diamonds,
    };
  },
};
</script>
<template>
  <div class="entry-bg">
    <div class="diamond" v-for="item in diamonds" :style="item.style" :key="item.index"></div>
  </div>
</template>
<style scoped>
.entry-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  pointer-events: none;
  background: #f0f2f5;
}
.entry-bg .diamond {
  position: absolute;
  background: #fff;
  transform: rotate(45deg);
  box-shadow: var(--el-box-shadow-dark);
}
</style>
