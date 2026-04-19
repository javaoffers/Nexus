/**
 * Standalone utility: read a drawer's persisted size from localStorage.
 * @param {string} drawerKey
 * @param {number} defaultSize
 * @returns {number}
 */
export function getDrawerSize(drawerKey, defaultSize) {
  if (!drawerKey) return defaultSize;
  const drawList = JSON.parse(localStorage.getItem('drawList') || '[]');
  const drawer = drawList.find((item) => item.key === drawerKey);
  return drawer ? drawer.size : defaultSize;
}

/**
 * Standalone utility: persist a drawer's size to localStorage.
 * @param {string} drawerKey
 * @param {number} size
 */
export function setDrawerSize(drawerKey, size) {
  if (!drawerKey) return;
  const drawList = JSON.parse(localStorage.getItem('drawList') || '[]');
  const drawer = drawList.find((item) => item.key === drawerKey);
  if (drawer) {
    drawer.size = size;
  } else {
    drawList.push({ key: drawerKey, size: size });
  }
  localStorage.setItem('drawList', JSON.stringify(drawList));
}

/**
 * Mixin that provides resizable-drawer behaviour.
 *
 * The consuming component MUST supply the following props:
 *   - drawerKey  {String}
 *   - size       {Number}   (default drawer size)
 *   - direction  {String}   one of 'ltr' | 'rtl' | 'ttb' | 'btt'
 *
 * The consuming component should emit 'closed' and 'update:modelValue'.
 *
 * Data provided:  drawerSize
 * Computed:       drawerClass, resizeBarClass
 * Methods:        onMouseDown, onClosed
 */
export const resizableDrawerMixin = {
  data() {
    return {
      drawerSize: getDrawerSize(this.drawerKey, this.size),
      _startX: 0,
      _startY: 0,
      _initialSize: 0,
    };
  },

  computed: {
    drawerClass() {
      return `resize-drawer-${this.direction}`;
    },
    resizeBarClass() {
      return `resize-bar resize-bar-${this.direction}`;
    },
  },

  methods: {
    _onMouseMove(e) {
      if (this.direction === 'ltr' || this.direction === 'rtl') {
        const deltaX =
          this.direction === 'ltr'
            ? e.clientX - this._startX
            : this._startX - e.clientX;
        this.drawerSize = Math.max(200, this._initialSize + deltaX);
      } else if (this.direction === 'ttb' || this.direction === 'btt') {
        const deltaY =
          this.direction === 'ttb'
            ? e.clientY - this._startY
            : this._startY - e.clientY;
        this.drawerSize = Math.max(200, this._initialSize + deltaY);
      }
    },

    _onMouseUp() {
      document.removeEventListener('mousemove', this._onMouseMove);
      document.removeEventListener('mouseup', this._onMouseUp);
      if (this.drawerKey) {
        setDrawerSize(this.drawerKey, this.drawerSize);
      }
    },

    onMouseDown(e) {
      e.preventDefault();
      this._startX = e.clientX;
      this._startY = e.clientY;
      this._initialSize = this.drawerSize;
      document.addEventListener('mousemove', this._onMouseMove);
      document.addEventListener('mouseup', this._onMouseUp);
    },

    onClosed() {
      this.$emit('closed');
      this.$emit('update:modelValue', false);
    },
  },

  beforeUnmount() {
    document.removeEventListener('mousemove', this._onMouseMove);
    document.removeEventListener('mouseup', this._onMouseUp);
  },
};
