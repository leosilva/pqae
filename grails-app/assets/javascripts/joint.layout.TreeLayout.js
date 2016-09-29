/*! Rappid v1.7.1 - HTML5 Diagramming Framework

Copyright (c) 2015 client IO

 2016-03-03 


This Source Code Form is subject to the terms of the Rappid License
, v. 2.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_v2.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


// Tree Graph Layout.
// ==================

;(function(joint, Backbone, _, g) {

    // Layout Siblings
    // ===============
    // a group of sibling layout areas

    function LayoutSiblings(layoutAreas, parentArea, opt) {

        opt = _.defaults(opt || {}, { siblingGap: 0 });

        this.width = 0;
        this.height = 0;
        this.layoutAreas = this.sortLayoutAreas(layoutAreas);
        this.parentArea = parentArea;
        this.siblingGap = opt.siblingGap;
        this.computeSize(opt);
    };

    _.extend(LayoutSiblings.prototype, {

        sortLayoutAreas: function(layoutAreas) {

            var areas = _.sortBy(layoutAreas, 'siblingRank');

            // re-number the sibling ranks
            _.each(areas, function(area, index) {
                area.siblingRank = index;
            });

            return areas;
        },

        move: function(dx, dy) {
            _.each(this.layoutAreas, function(area) {
                area.dx += dx;
                area.dy += dy;
            });
        },

        exists: function() {
            return this.layoutAreas.length > 0;
        },

        sumGaps: function(gap) {
            var gapCount = Math.max(this.layoutAreas.length - 1, 0);
            return gapCount * gap;
        },

        getSiblingRankByPoint: function(point) {

            if (!this.exists()) {
                // minimal extreme
                return -1;
            }

            var closestArea = this.findAreaByPoint(point);
            if (!closestArea) {
                // maximal extreme
                return this.layoutAreas.length - 1;
            }

            return closestArea.siblingRank - 1;
        },

        getConnectionPoints: function(point, opt) {

            // this is wrong, method should always return points.
            if (!this.exists()) {
                return [];
            }

            var deltaCoordinates = {
                dx: point.x - this.parentArea.rootCX,
                dy: point.y - this.parentArea.rootCY
            };

            return this.layoutAreas[0].getRootVertices(deltaCoordinates, opt);
        },

        getParentConnectionPoint: function() {

            var parentArea = this.parentArea;
            var offset = this.proxyLayoutArea('getConnectionPoint', parentArea.rootSize);
            var connectionPoint = g.point(parentArea.rootCX, parentArea.rootCY);

            return connectionPoint.offset(offset.x, offset.y);
        },

        getChildConnectionPoint: function(point, rootSize) {

            var offset = this.proxyLayoutArea('getConnectionPoint', rootSize);
            return g.point(point).difference(offset);
        },

        proxyLayoutArea: function(method) {

            var args = Array.prototype.slice.call(arguments, 1);
            return LayoutArea.fromDirection(this.direction).prototype[method].apply(this.parentArea, args);
        }
    });

    LayoutSiblings.extend = Backbone.Model.extend;

    var VerticalLayoutSiblings = LayoutSiblings.extend({

        findAreaByPoint: function(point) {

            return _.find(this.layoutAreas, function(area) {
                return area.rootCY > point.y;
            });
        },

        computeSize: function(opt) {

            this.height = _.sum(this.layoutAreas, 'height') + this.sumGaps(opt.siblingGap);

            _.reduce(this.layoutAreas, function(y, area) {
                this.width = Math.max(this.width, area.getExtendedWidth());
                area.dy += y + area.height / 2;
                return y += area.height + opt.siblingGap;
            },  -this.height / 2, this);
        },

        getNeighborPointFromRank: function(rank) {

            var y;

            if (!this.exists()) {

                y = this.parentArea.rootCY;

            } else {

                var prevArea = this.layoutAreas[rank];
                var nextArea = this.layoutAreas[rank + 1];

                if (!prevArea) {
                    y = nextArea.y - this.siblingGap / 2;
                } else if (!nextArea) {
                    y = prevArea.y + prevArea.height + this.siblingGap / 2;
                } else {
                    y = (prevArea.y + prevArea.height + nextArea.y) / 2;
                }
            }

            return {
                x: this.getXTowardsParent(),
                y: y
            };
        }

    });

    var LeftLayoutSiblings = VerticalLayoutSiblings.extend({

        direction: 'L',

        getXTowardsParent: function() {

            var parentArea = this.parentArea;
            return parentArea.rootCX - parentArea.rootSize.width - parentArea.gap;
        }
    });

    var RightLayoutSiblings = VerticalLayoutSiblings.extend({

        direction: 'R',

        getXTowardsParent: function() {

            var parentArea = this.parentArea;
            return parentArea.rootCX + parentArea.rootSize.width + parentArea.gap;
        }
    });

    var HorizontalLayoutSiblings = LayoutSiblings.extend({

        findAreaByPoint: function(point) {

            return _.find(this.layoutAreas, function(area) {
                return area.rootCX > point.x;
            });
        },

        computeSize: function(opt) {

            this.width = _.sum(this.layoutAreas, 'width') + this.sumGaps(opt.siblingGap);

            _.reduce(this.layoutAreas, function(x, area) {
                this.height = Math.max(this.height, area.getExtendedHeight());
                area.dx += x + area.width / 2;
                return x + area.width + opt.siblingGap;
            },  -this.width / 2, this);
        },

        getNeighborPointFromRank: function(rank) {

            var x;

            if (!this.exists()) {

                x = this.parentArea.rootCX;

            } else {

                var prevArea = this.layoutAreas[rank];
                var nextArea = this.layoutAreas[rank + 1];

                if (!prevArea) {
                    x = nextArea.x - this.siblingGap / 2;
                } else if (!nextArea) {
                    x = prevArea.x + prevArea.width + this.siblingGap / 2;
                } else {
                    x = (prevArea.x + prevArea.width + nextArea.x) / 2;
                }
            }

            return {
                x: x,
                y: this.getYTowardsParent()
            };
        }
    });

    var TopLayoutSiblings = HorizontalLayoutSiblings.extend({

        direction: 'T',

        getYTowardsParent: function() {

            var parentArea = this.parentArea;
            return parentArea.rootCY - parentArea.getLRHeight() / 2 - parentArea.gap;
        }
    });

    var BottomLayoutSiblings = HorizontalLayoutSiblings.extend({

        direction: 'B',

        getYTowardsParent: function() {

            var parentArea = this.parentArea;
            return parentArea.rootCY + parentArea.getLRHeight() / 2 + parentArea.gap;
        }
    });




    // Layout Area
    // ===========

    function LayoutArea(root, childAreas, opt) {

        var options = _.extend({}, opt, this.getRootAttributes(root, opt.attributeNames));

        _.defaults(options, {
            siblingGap: 0,
            gap: 0
        });

        this.root = root;
        this.childAreas = childAreas;
        this.siblingRank = options.siblingRank;
        this.rootOffset = options.rootOffset;
        this.gap = options.gap;
        this.dx = 0;
        this.dy = 0;
        this.width = 0;
        this.height = 0;

        _.invoke(childAreas, 'addParentReference', this);

        this.computeRelativePosition(root, childAreas, options);
    };

    _.extend(LayoutArea, {

        create: function(direction, root, childAreas, opt) {

            var constructor = LayoutArea.fromDirection(direction, opt);
            return new constructor(root, childAreas, opt);
        },

        fromDirection: function(direction, opt) {

            var constructor;

            switch (direction) {
                case 'L':
                    constructor = LeftLayoutArea;
                    break;
                case 'T':
                    constructor = TopLayoutArea;
                    break;
                case 'R':
                    constructor = RightLayoutArea;
                    break;
                case 'B':
                    constructor = BottomLayoutArea;
                    break;
                default:
                    constructor = LayoutArea;
            };

            return constructor;
        }

    });

    _.extend(LayoutArea.prototype, {

        direction: null,

        getLRHeight: function() {

            return Math.max(this.rootSize.height, this.siblings.L.height, this.siblings.R.height);
        },

        getBBox: function(opt) {

            var bbox = g.rect(this);
            var pad = opt && opt.expandBy;

            if (pad) {

                bbox.moveAndExpand({
                    x: - pad,
                    y: - pad,
                    width: pad * 2,
                    height: pad * 2
                });
            }

            return bbox;
        },

        containsPoint: function(point, opt) {

            return this.getBBox(opt).containsPoint(point);
        },

        getLayoutSiblings: function(direction) {

            return this.siblings[direction];
        },

        getExtendedWidth: function() {

            return this.width + this.gap + this.rootOffset;
        },

        getExtendedHeight: function() {

            return this.height + this.gap + this.rootOffset;
        },

        findMinimalAreaByPoint: function(point, opt) {

            if (!this.containsPoint(point, opt)) {
                return null;
            }

            var minimalArea;

            _.some(this.childAreas, function(area) {
                minimalArea = area.findMinimalAreaByPoint(point, opt);
                return !!minimalArea;
            });

            return minimalArea || this;
        },

        getType: function() {

            return _.reduce(this.siblings, function(memo, siblingGroup, direction) {
                return siblingGroup.exists() ? memo + direction : memo;
            }, '');
        },

        addParentReference: function(parentArea) {

            this.parentArea = parentArea;
        },

        getRootAttributes: function(root, attributeNames) {

            var siblingRank = root.get(attributeNames.siblingRank || 'siblingRank');

            return {
                siblingRank: _.isNumber(siblingRank) ? siblingRank : null,
                rootOffset: root.get(attributeNames.offset || 'offset') || 0,
                rootMargin: root.get(attributeNames.margin || 'margin') || 0
            };
        },

        getRootSize: function(root, rootMargin) {

            var rootSize = _.clone(root.get('size'));

            rootSize[this.marginDimension] += rootMargin;

            return rootSize;
        },

        createSiblings: function(childAreas, opt) {

            var groupedAreas = _.groupBy(childAreas, 'direction');

            return {
                L: new LeftLayoutSiblings(groupedAreas.L, this, opt),
                T: new TopLayoutSiblings(groupedAreas.T, this, opt),
                R: new RightLayoutSiblings(groupedAreas.R, this, opt),
                B: new BottomLayoutSiblings(groupedAreas.B, this, opt)
            };
        },

        computeSize: function(siblings, rootSize) {

            var lrWidth = siblings.L.width + rootSize.width + siblings.R.width;
            var lrHeight = Math.max(siblings.L.height, rootSize.height, siblings.R.height);

            return {
                width: Math.max(siblings.T.width, siblings.B.width, lrWidth),
                height: siblings.T.height + siblings.B.height + lrHeight
            };
        },

        computeOrigin: function() {
            return {
                x: this.rootCX - Math.max(this.siblings.L.width + this.rootSize.width / 2, this.siblings.T.width / 2, this.siblings.B.width / 2),
                y: this.rootCY - this.siblings.T.height - this.getLRHeight() / 2
            };
        },

        moveSiblings: function(siblings, rootSize) {

            if (this.hasHorizontalSiblings(siblings)) {

                var dx = rootSize.width / 2;

                siblings.L.move(-dx , 0);
                siblings.R.move(dx, 0);
            }

            if (this.hasVerticalSiblings(siblings)) {

                var dy = Math.max(siblings.L.height, rootSize.height, siblings.R.height) / 2;

                siblings.T.move(0, -dy);
                siblings.B.move(0, dy);
            }
        },

        moveRootToConnectionPoint: function(rootSize) {

            var connectionPoint = this.getConnectionPoint(rootSize);

            this.dx += connectionPoint.x;
            this.dy += connectionPoint.y;
        },

        computeRelativePosition: function(root, childAreas, opt) {

            var siblings = this.siblings = this.createSiblings(childAreas, { siblingGap: opt.siblingGap });
            var rootSize = this.rootSize = this.getRootSize(root, opt.rootMargin);

            _.extend(this, this.computeSize(siblings, rootSize));

            this.moveSiblings(siblings, rootSize);
            this.moveRootToConnectionPoint(rootSize);
            this.moveRootBehindSiblings(siblings, rootSize);
            this.moveRootFromParent(opt.gap + opt.rootOffset);
        },

        computeAbsolutePosition: function() {

            if (this.parentArea) {
                this.rootCX = this.parentArea.rootCX + this.dx;
                this.rootCY = this.parentArea.rootCY + this.dy;
                this.level = this.parentArea.level + 1;
            } else {
                var rootCenter = this.root.getBBox().center();
                this.rootCX = rootCenter.x;
                this.rootCY = rootCenter.y;
                this.level = 0;
            }

            _.extend(this, this.computeOrigin());
        },

        hasVerticalSiblings: function(siblings) {

            return siblings.T.exists() || siblings.B.exists();
        },

        hasHorizontalSiblings: function(siblings) {

            return siblings.L.exists() || siblings.R.exists();
        },

        isSourceArea: function() {

            return !this.parentArea;
        },

        isSinkArea: function() {

            return this.childAreas.length === 0;
        },

        getRootPosition: function() {

            var rootSize = this.root.get('size');

            return {
                x: this.rootCX - rootSize.width / 2,
                y: this.rootCY - rootSize.height / 2
            };
        },

        getRootVertices: function(deltaCoordinates, opt) {

            opt = opt || {};
            deltaCoordinates = deltaCoordinates || this;

            if (deltaCoordinates[this.deltaCoordinate] === 0 || !this.parentArea) {
                // Pure horizontal/vertical link has no vertices.
                return [];
            }

            var parentInnerSize = this.parentArea.getInnerSize();
            var relativeVertices;

            if (!opt.ignoreSiblings && this.hasSiblingsBetweenParent()) {
                // vertices avoids the elements between the root and its parent.
                var oppositeSiblings = this.siblings[this.oppositeDirection];
                relativeVertices = this.getRelativeVerticesAvoidingSiblings(
                    parentInnerSize,
                    deltaCoordinates,
                    oppositeSiblings
                );
            } else {
                relativeVertices = this.getRelativeVertices(parentInnerSize, deltaCoordinates);
            }

            return _.invoke(relativeVertices, 'offset', this.parentArea.rootCX, this.parentArea.rootCY);
        },

        getOuterSize: function() {

            return {
                width: this.width,
                height: this.height
            };
        },

        getInnerSize: function() {

            return {
                width: this.rootSize.width,
                height: this.getLRHeight()
            };
        },

        getConnectionPoint: function() {

            // root area has no connection point
            return null;
        },

        getRelativeVertices: function() {

            // root area has no inbound link
            return null;
        },

        moveRootFromParent: function() {

            // root area has no parent
        },

        moveRootBehindSiblings: function() {

            // root area dx, dy are always 0,0
        },

        // Is there an element between the root of the area and the parent.
        hasSiblingsBetweenParent: function() {

            return !this.isSourceArea() && this.siblings[this.oppositeDirection].exists();
        }

    });

    LayoutArea.extend = Backbone.Model.extend;

    var VerticalLayoutArea = LayoutArea.extend({

        deltaCoordinate: 'dy',
        marginDimension: 'width'
    });

    var HorizontalLayoutArea = LayoutArea.extend({

        deltaCoordinate: 'dx',
        marginDimension: 'height'
    });

    var RightLayoutArea = HorizontalLayoutArea.extend({

        direction: 'R',
        oppositeDirection: 'L',

        getConnectionPoint: function(rootSize) {

            return g.point(rootSize.width / 2, 0);
        },

        moveRootBehindSiblings: function(siblings, rootSize) {

            this.dx += Math.max(
                siblings.L.width,
                (siblings.T.width - rootSize.width) / 2,
                (siblings.B.width - rootSize.width) / 2
            );

            this.dy += (siblings.T.height - siblings.B.height) / 2;
        },

        moveRootFromParent: function(distance) {

            this.dx += distance;
        },

        getRelativeVertices: function(parentRootSize, deltaCoordinates) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            return [
                connectionPoint.clone().offset(this.gap / 2, 0),
                connectionPoint.clone().offset(this.gap / 2, deltaCoordinates.dy)
            ];
        },

        getRelativeVerticesAvoidingSiblings: function(parentRootSize, deltaCoordinates, siblings) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            var siblingGap = siblings.siblingGap;

            var signY = deltaCoordinates.dy > 0 ? -1 : 1;
            var fx = siblings.layoutAreas.length > 1 ? 1.5 : 1;
            var y1 = deltaCoordinates.dy + signY * (siblings.height + siblingGap) / 2;
            var y2 = deltaCoordinates.dy + signY * this.rootSize.height / 4;
            var x1 = this.gap / 2;
            var x2 = fx * x1 + siblings.width;

            return [
                connectionPoint.clone().offset(x1, 0),
                connectionPoint.clone().offset(x1, y1),
                connectionPoint.clone().offset(x2, y1),
                connectionPoint.clone().offset(x2, y2)
            ];
        }
    });

    var LeftLayoutArea = HorizontalLayoutArea.extend({

        direction: 'L',
        oppositeDirection: 'R',

        getConnectionPoint: function(rootSize) {

            return g.point(- rootSize.width / 2, 0);
        },

        moveRootBehindSiblings: function(siblings, rootSize) {

            this.dx -= Math.max(
                siblings.R.width,
                (siblings.T.width - rootSize.width) / 2,
                (siblings.B.width - rootSize.width) / 2
            );
            this.dy += (siblings.T.height - siblings.B.height) / 2;
        },

        moveRootFromParent: function(distance) {

            this.dx -= distance;
        },

        getRelativeVertices: function(parentRootSize, deltaCoordinates) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            return [
                connectionPoint.clone().offset(-this.gap / 2, 0),
                connectionPoint.clone().offset(-this.gap / 2, deltaCoordinates.dy)
            ];
        },

        getRelativeVerticesAvoidingSiblings: function(parentRootSize, deltaCoordinates, siblings) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            var siblingGap = siblings.siblingGap;

            var signY = deltaCoordinates.dy > 0 ? -1 : 1;
            var fx = siblings.layoutAreas.length > 1 ? 1.5 : 1;
            var y1 = deltaCoordinates.dy + signY * (siblings.height + siblingGap) / 2;
            var y2 = deltaCoordinates.dy + signY * this.rootSize.height / 4;
            var x1 = this.gap / 2;
            var x2 = fx * x1 + siblings.width;

            return [
                connectionPoint.clone().offset(-x1, 0),
                connectionPoint.clone().offset(-x1, y1),
                connectionPoint.clone().offset(-x2, y1),
                connectionPoint.clone().offset(-x2, y2)
            ];
        }
    });

    var TopLayoutArea = VerticalLayoutArea.extend({

        direction: 'T',
        oppositeDirection: 'B',

        getConnectionPoint: function(rootSize) {

            return g.point(0, - rootSize.height / 2);
        },

        moveRootBehindSiblings: function(siblings, rootSize) {

            this.dx += (siblings.L.width - siblings.R.width) / 2;
            this.dy -= siblings.B.height;

            if (this.hasHorizontalSiblings(siblings)) {
                this.dy -= (this.getLRHeight() - rootSize.height) / 2;
            }
        },

        moveRootFromParent: function(distance) {

            this.dy -= distance;
        },

        getRelativeVertices: function(parentRootSize, deltaCoordinates) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            return [
                connectionPoint.clone().offset(0, -this.gap / 2),
                connectionPoint.clone().offset(deltaCoordinates.dx, -this.gap / 2)
            ];
        },

        getRelativeVerticesAvoidingSiblings: function(parentRootSize, deltaCoordinates, siblings) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            var siblingGap = siblings.siblingGap;

            var signX = deltaCoordinates.dx > 0 ? -1 : 1;
            var fy = siblings.layoutAreas.length > 1 ? 1.5 : 1;
            var y1 = this.gap / 2;
            var y2 = fy * y1 + siblings.height;
            var x1 = deltaCoordinates.dx + signX * (siblings.width + siblingGap) / 2;
            var x2 = deltaCoordinates.dx + signX * this.rootSize.width / 4;

            return [
                connectionPoint.clone().offset(0, -y1),
                connectionPoint.clone().offset(x1, -y1),
                connectionPoint.clone().offset(x1, -y2),
                connectionPoint.clone().offset(x2, -y2)
            ];
        }
    });

    var BottomLayoutArea = VerticalLayoutArea.extend({

        direction: 'B',
        oppositeDirection: 'T',

        getConnectionPoint: function(rootSize) {

            return g.point(0, rootSize.height / 2);
        },

        moveRootBehindSiblings: function(siblings, rootSize) {

            this.dx += (siblings.L.width - siblings.R.width) / 2;
            this.dy += siblings.T.height;

            if (this.hasHorizontalSiblings(siblings)) {
                this.dy += (this.getLRHeight() - rootSize.height) / 2;
            }
        },

        moveRootFromParent: function(distance) {

            this.dy += distance;
        },

        getRelativeVertices: function(parentRootSize, deltaCoordinates) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);

            return [
                connectionPoint.clone().offset(0, this.gap / 2),
                connectionPoint.clone().offset(deltaCoordinates.dx, this.gap / 2)
            ];
        },

        getRelativeVerticesAvoidingSiblings: function(parentRootSize, deltaCoordinates, siblings) {

            var connectionPoint = this.getConnectionPoint(parentRootSize);
            var siblingGap = siblings.siblingGap;

            var signX = deltaCoordinates.dx > 0 ? -1 : 1;
            var fy = siblings.layoutAreas.length > 1 ? 1.5 : 1;
            var y1 = this.gap / 2;
            var y2 = fy * y1 + siblings.height;
            var x1 = deltaCoordinates.dx + signX * (siblings.width + siblingGap) / 2;
            var x2 = deltaCoordinates.dx + signX * this.rootSize.width / 4;

            return [
                connectionPoint.clone().offset(0, y1),
                connectionPoint.clone().offset(x1, y1),
                connectionPoint.clone().offset(x1, y2),
                connectionPoint.clone().offset(x2, y2)
            ];
        }
    });

    var directionRules = {

        rotate: function(rule) {
            var directions = 'LTRB';
            var directionChange = directions.indexOf(rule[0]) - directions.indexOf(rule[1]);
            return function(direction) {
                var directionIndex = directions.indexOf(direction);
                return (directionIndex >= 0)
                    ? directions[(4 + directionIndex - directionChange) % 4]
                    : direction;
            };
        },

        flip: function(rule) {
            var from = rule[0];
            var to = rule[1];
            return function(direction) {
                if (direction === from) return to;
                if (direction === to) return from;
                return direction;
            };
        },

        straighten: function(rule) {
            return _.constant(rule[1]);
        }

    };

    var TreeLayout = Backbone.Model.extend({

        defaults: {

            // A graph to perform the layout on.
            graph: undefined,

            // Gap between a child and its parent.
            gap: 20,

            // Gap between two siblings.
            siblingGap: 20,

            // Default direction used when elements
            // doesn't explicitelly specify their direction.
            direction: 'R',

            // A rule telling how to reorganize a branch after reconnect.
            // The root of the branch can change the `direction` so
            // it migth be desirable to change also the directions of
            // its descendants.
            directionRule: directionRules.straighten,

            // Allows elements to be positioned in animated manner.
            //
            // element.transition('position', position, {
            //   delay: 300,
            //   valueFunction: joint.util.interpolate.object,
            // });
            updatePosition: function(element, position, opt) {
                element.set('position', position, opt);
            },

            // Allows vertices to be positioned in animated manner.
            // Please see `updatePosition` option.
            updateVertices: function(link, vertices, opt) {
                link.set('vertices', vertices, opt);
            },

            // Allows setting arbitrary (usually layout-related) attributes
            // on the cells as part of the layout.
            //
            // function(layoutArea, root, rootLink, opt) {
            //   /* update root and rootLink cells. */
            // }
            updateAttributes: null,

            // Arbitrary subtree can be skipped in the layout computation.
            // e.g. a collapse branch is not visible in the paper but present
            // in the graph.
            //
            // function(children, parent, opt) {
            //   /* returns filtered children */
            // }
            filter: null,

            // The element attributes that control layout
            // can be customized here.
            // e.g { direction: 'rankDir' }
            attributeNames: {
                // siblingRank: 'siblingRank',
                // direction: 'direction',
                // margin: 'margin',
                // offset: 'offset'
            }
        },

        initialize: function() {

            // Caching the graph and the other options for a quicker access.
            this._cacheOptions(this.attributes);

            // Layout areas cache.
            this.layoutAreas = {};
        },

        // Auto layout the entire graph
        // Each root is treated separately.
        layout: function(opt) {

            // delete the previous layout areas
            this.layoutAreas = {};

            _.each(this.graph.getSources(), _.partial(this.layoutTree, _, opt), this);

            // COMPAT: backwards compatibility
            this.trigger('layout:done', opt);

            return this;
        },

        // Auto layout a single tree.
        layoutTree: function(root, opt) {

            opt = opt || {};
            opt.treeLayout = true;

            // create layout areas and compute relative positions
            var rootArea = this._computeLayoutAreas(root, this.get('direction'), opt);

            this._computeAbsolutePositions(rootArea);
            this._updateCells(rootArea, opt);

            return this;
        },

        getLayoutArea: function(element) {

            return this.layoutAreas[element.id || element] || null;
        },

        getRootLayoutAreas: function() {

            return _.map(this.graph.getSources(), this.getLayoutArea, this);
        },

        // Returns a layout area with minimal size containing the given point.
        getMinimalRootAreaByPoint: function(point) {

            var rootLayoutAreas = _.filter(this.getRootLayoutAreas(), function(layoutArea) {
                return layoutArea.containsPoint(point);
            });

            if (_.isEmpty(rootLayoutAreas)) {
                return null;
            }

            return _.min(rootLayoutAreas, function(layoutArea) {
                return layoutArea.width * layoutArea.height;
            });
        },

        // Recursively (in top-down fashion) computes the layout areas
        // for every single element in the current tree.
        _computeLayoutAreas: function(element, prevDirection, opt) {

            // PHASE 1: Going from the root down to the leaves.
            var direction = element.get(this.getAttributeName('direction')) || prevDirection;
            var children = this._getChildren(element, opt);
            // Recursion start.
            var childLayoutAreas = _.map(children, _.partial(this._computeLayoutAreas, _, direction, opt), this);

            // PHASE 2: Going from the leaves up to the root.
            var layoutArea = LayoutArea.create(direction, element, childLayoutAreas, this.attributes);
            // Find the element inbound link.
            layoutArea.link = this.graph.getConnectedLinks(element, { inbound: true })[0];
            // Store the layout area on the tree layout instance.
            this.layoutAreas[element.id] = layoutArea;

            return layoutArea;
        },

        // Cache options on the instance object for quicker access.
        _cacheOptions: function(options) {

            var functionNames = [
                'updateAttributes',
                'updateVertices',
                'updatePosition',
                'filter'
            ];

            // cache functions
            _.each(functionNames, function(name) {
                this[name] = _.isFunction(options[name]) ? options[name] : null;
            }, this);

            // cache graph
            this.graph = options.graph;
        },

        // Returns filtered children for the given parent.
        _getChildren: function(parent, opt) {

            var children = this.graph.getNeighbors(parent, { outbound: true });
            if (this.filter && children.length > 0) {
                children = this.filter(children, parent, opt) || children;
            }

            return children;
        },

        // Recursively computes the elements absolute positions based on
        // the relative position and absolute position of the parent element.
        _computeAbsolutePositions: function(layoutArea) {

            layoutArea.computeAbsolutePosition(layoutArea);
            _.each(layoutArea.childAreas, this._computeAbsolutePositions, this);
        },

        // Applies computed values on the tree cells.
        _updateCells: function(layoutArea, opt) {

            var rootElement = layoutArea.root;
            var rootLink = layoutArea.link || null;

            if (rootLink) {

                // update the position of the current root.
                if (this.updatePosition) {
                    this.updatePosition(rootElement, layoutArea.getRootPosition(), opt);
                }

                // update the vertices of the link connected to the current root.
                if (this.updateVertices) {
                    this.updateVertices(rootLink, layoutArea.getRootVertices(), opt);
                }
            }

            this.changeSiblingRank(rootElement, layoutArea.siblingRank, opt);

            // update the attributes of the current root and the associated link
            if (this.updateAttributes) {
                this.updateAttributes(layoutArea, rootElement, rootLink, opt);
            }

            _.each(layoutArea.childAreas, _.partial(this._updateCells, _, opt), this);
        },

        updateDirections: function(root, rule, opt) {

            opt = opt || {};

            var directionAttribute = this.getAttributeName('direction');
            var getDirection = this.get('directionRule')(rule);

            this.graph.search(root, _.bind(function(element, level) {

                if (level === 0) return;
                var newDirection = getDirection(element.get(directionAttribute));
                this.changeDirection(element, newDirection, opt);

            }, this), { outbound: true });
        },

        reconnectElement: function(element, targetElement, opt) {

            opt = opt || {};

            var layoutArea = this.getLayoutArea(element);
            var link = layoutArea.link;

            if (link) {

                link.set('source', { id: targetElement.id || targetElement }, opt);

                var oldDirection = layoutArea.direction;
                var newDirection = opt.direction || oldDirection;
                var newSiblingRank = opt.siblingRank || undefined;

                this.changeSiblingRank(element, newSiblingRank, opt);
                this.changeDirection(element, newDirection, opt);

                if (oldDirection !== newDirection) {
                    this.updateDirections(element, [oldDirection, opt.direction], opt);
                }

                return true;
            }

            return false;
        },

        // A convenient way how to get/set cells attributes
        // as the attribute names can vary based on the tree layout settings.

        changeSiblingRank: function(element, siblingRank, opt) {

            element.set(this.getAttributeName('siblingRank'), siblingRank, opt);
        },

        changeDirection: function(element, direction, opt) {

            element.set(this.getAttributeName('direction'), direction, opt);
        },

        getAttributeName: function(attribute) {

            return this.get('attributeNames')[attribute] || attribute;
        },

        getAttribute: function(element, attribute) {

            return element.get(this.getAttributeName(attribute));
        },

        // COMPAT: backwards compatibilty

        prepare: function() {

            // This method built the adjacency table originally.
            // No need for this now as the graph build one internally.
            return this;
        }

    }, {

        directionRules: directionRules

    });

    // Export
    joint.layout.TreeLayout = TreeLayout;

})(joint, Backbone, _, g);
