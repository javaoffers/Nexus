import * as d3 from 'd3';
import { DataNode, generateDataTree } from '../data';
import { LayoutNode, VerticalLayout } from './layout';
import { loadSvgIcon } from './utils/icon';

export class FlowRenderer {
  svg;

  pan;

  container;

  boundary;

  scaleExtent = [0.5, 2];

  zoom;

  dataRoot;

  layout;

  options;

  constructor(el, options) {
    this.svg = d3.select(el).append('svg').attr('width', '100%').attr('height', '100%').attr("style", "background-color: #F7F8FA;");

    loadSvgIcon(this.svg);

    this.boundary = el.getBoundingClientRect();
    this.pan = this.svg.append('g');
    this.container = this.pan.append('g');

    this.options = options;
    this.zoom = d3
      .zoom()
      .scaleExtent(this.scaleExtent)
      .on('zoom', event => {
        this.pan.attr('transform', event.transform);
        if (typeof options.onZoom === 'function') {
          options.onZoom(event);
        }
      });
    this.svg.call(this.zoom);

    this.dataRoot = generateDataTree(options.flowContext);
    this.layout = new VerticalLayout(this);
    this.refresh();
  }

  updateDatas(root) {
    this.dataRoot = root;
    this.refresh();
  }

  refresh() {
    this.layout.analysis(this.dataRoot);
    this.layout.draw();
  }

  scaleFromTop(scale) {
    const scaleExtent = this.scaleExtent;
    if (scale < scaleExtent[0]) {
      scale = scaleExtent[0];
    }
    if (scale > scaleExtent[1]) {
      scale = scaleExtent[1];
    }
    const transform = d3.zoomTransform(this.svg.node());
    const point = [transform.x + (this.boundary.width * transform.k) / 2, 0];
    this.zoom.scaleTo(this.svg, scale, point);
    return scale;
  }
}
