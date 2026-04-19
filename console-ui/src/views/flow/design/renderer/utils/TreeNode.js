export class TreeNode {
  parent = null;
  children = [];

  getParent() {
    return this.parent;
  }

  getChildren() {
    return [...this.children];
  }

  addChild(node) {
    const currentParent = node.getParent();
    if (currentParent) {
      currentParent.removeChild(node);
    }
    node.parent = this;
    this.children.push(node);
  }

  removeChild(node) {
    const index = this.children.indexOf(node);
    if (index !== -1) {
      node.parent = null;
      this.children.splice(index, 1);
    }
  }

  delete() {
    while (this.children.length > 0) {
      this.children[0].delete();
    }

    if (this.parent) {
      this.parent.removeChild(this);
    }
  }

  insertChild(node, index) {
    if (index < 0 || index > this.children.length) {
      this.addChild(node);
      return;
    }
    const currentParent = node.getParent();
    if (currentParent) {
      currentParent.removeChild(node);
    }
    node.parent = this;
    this.children.splice(index, 0, node);
  }

  insertAfter(node, after) {
    const index = this.children.indexOf(after);
    if (index === -1) {
      this.addChild(node);
    } else {
      this.insertChild(node, index + 1);
    }
  }
}
