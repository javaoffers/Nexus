import { TreeNode, DataNode } from '../../data';

export class LayoutNode extends TreeNode {
  constructor(params) {
    super();
    this._data = params.data;
    if (params.linesTo) {
      this._linesTo = params.linesTo;
    }
    this.setSize(params.width, params.height);
    this.setRelative(params.left, params.top);
  }

  _data;

  _linesTo = [];

  _width = 0;

  _height = 0;

  _top = 0;

  _left = 0;

  get data() {
    return this._data;
  }

  get linesTo() {
    return this._linesTo;
  }

  get width() {
    return this._width;
  }

  get height() {
    return this._height;
  }

  get top() {
    return this._top;
  }

  get left() {
    return this._left;
  }

  get bottom() {
    return this.top + this.height;
  }

  get right() {
    return this.left + this.width;
  }

  get x() {
    const parent = this.getParent();
    const parentX = parent?.x || 0;
    return this.left + parentX;
  }

  get y() {
    const parent = this.getParent();
    const parentY = parent?.y || 0;
    return this.top + parentY;
  }

  setSize(width, height) {
    this._width = width;
    this._height = height;
  }

  setRelative(left, top) {
    this._left = left;
    this._top = top;
  }

  getChildren() {
    return super.getChildren();
  }

  getParent() {
    return super.getParent();
  }

  line(node) {
    if (this._linesTo.includes(node)) return;
    this._linesTo.push(node);
  }

  // 内容盒子
  contentBox = {
    left: 0,
    top: 0,
    width: 0,
    height: 0,
  };

  // 设置内容盒子
  setContentBox(box) {
    this.contentBox = Object.assign(this.contentBox, box);
  }

  // 获取内容盒子中心点
  getContentBoxCenter() {
    return [this.x + this.contentBox.left + this.contentBox.width / 2, this.y + this.contentBox.top + this.contentBox.height / 2];
  }

  getTopCenter() {
    return [this.x + this.width / 2, this.y];
  }

  getBottomCenter() {
    return [this.x + this.width / 2, this.y + this.height];
  }
}
