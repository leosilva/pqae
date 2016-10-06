// Example usage:

//      var paperScroller = new joint.ui.PaperScroller;
//      var paper = new joint.dia.Paper({ el: paperScroller.el });
//      paperScroller.options.paper = paper;
//      $appElement.append(paperScroller.render().el);

//      paperScroller.center();
//      paper.on('blank:pointerdown', paperScroller.startPanning);



joint.ui.PaperScroller = Backbone.View.extend ({
    className: "paper-scroller",
    events: {
        mousedown: "pointerdown",
        mousemove: "pointermove",
        touchmove: "pointermove"
    },
    options: {
        paper: void 0,
        padding: 0,
        autoResizePaper: !1,
        baseWidth: void 0,
        baseHeight: void 0,
        contentOptions: void 0
    },
    init: function() {
        _.bindAll(this, "startPanning", "stopPanning", "pan");
        var a = this.options.paper, b = V(a.viewport).scale();
        this._sx = b.sx, this._sy = b.sy, _.isUndefined(this.options.baseWidth) && (this.options.baseWidth = a.options.width), _.isUndefined(this.options.baseHeight) && (this.options.baseHeight = a.options.height), this.$el.append(a.el), this.addPadding(), this.listenTo(a, "scale", this.onScale), this.listenTo(a, "resize", this.onResize), this.options.autoResizePaper && this.listenTo(a.model, "change add remove reset", this.adjustPaper)
    },
    onResize: function() {
        this._center && this.center(this._center.x, this._center.y)
    },
    onScale: function(a, b, c, d) {
        this.adjustScale(a, b), this._sx = a, this._sy = b, (c || d) && this.center(c, d)
    },
    beforePaperManipulation: function() {
        this.$el.css("visibility", "hidden")
    },
    afterPaperManipulation: function() {
        this.$el.css("visibility", "visible")
    },
    toLocalPoint: function(a, b) {
        var c = this.options.paper.viewport.getCTM();
        return a += this.el.scrollLeft - this.padding.paddingLeft - c.e, a/=c.a, b += this.el.scrollTop - this.padding.paddingTop - c.f, b/=c.d, g.point(a, b)
    },
    adjustPaper: function() {
        this._center = this.toLocalPoint(this.el.clientWidth / 2, this.el.clientHeight / 2);
        var a = _.extend({
            gridWidth: this.options.baseWidth,
            gridHeight: this.options.baseHeight,
            allowNewOrigin: "negative"
        }, this.options.contentOptions);
        return this.options.paper.fitToContent(this.transformContentOptions(a)), this
    },
    adjustScale: function(a, b) {
        var c = this.options.paper.options, d = a / this._sx, e = b / this._sy;
        this.options.paper.setOrigin(c.origin.x * d, c.origin.y * e), this.options.paper.setDimensions(c.width * d, c.height * e)
    },
    transformContentOptions: function(a) {
        var b = this._sx, c = this._sy;
        return a.gridWidth && (a.gridWidth*=b), a.gridHeight && (a.gridHeight*=c), a.minWidth && (a.minWidth*=b), a.minHeight && (a.minHeight*=c), _.isObject(a.padding) ? a.padding = {
            left: (a.padding.left || 0) * b,
            right: (a.padding.right || 0) * b,
            top: (a.padding.top || 0) * c,
            bottom: (a.padding.bottom || 0) * c
        } : _.isNumber(a.padding) && (a.padding = a.padding * b), a
    },
    center: function(a, b, c) {
        var d = this.options.paper.viewport.getCTM(), e =- d.e, f =- d.f, g = e + this.options.paper.options.width, h = f + this.options.paper.options.height;
        _.isUndefined(a) || _.isUndefined(b) ? (a = (e + g) / 2, b = (f + h) / 2) : (a*=d.a, b*=d.d);
        var i = this.options.padding, j = this.el.clientWidth / 2, k = this.el.clientHeight / 2, l = j - i - a + e, m = j - i + a - g, n = k - i - b + f, o = k - i + b - h;
        return this.addPadding(Math.max(l, 0), Math.max(m, 0), Math.max(n, 0), Math.max(o, 0)), this.scroll(a, b, _.isUndefined(c) ? a || null : c), this
    },
    centerContent: function(a) {
        var b = V(this.options.paper.viewport).bbox(!0, this.options.paper.svg);
        return this.center(b.x + b.width / 2, b.y + b.height / 2, a), this
    },
    centerElement: function(a) {
        this.checkElement(a, "centerElement");
        var b = a.getBBox().center();
        return this.center(b.x, b.y)
    },
    scroll: function(a, b, c) {
    	console.log(this.options)
        var d = this.options.paper.viewport.getCTM(), e = {};
        if (_.isNumber(a)) {
            var f = this.el.clientWidth / 2;
            e.scrollLeft = a - f + d.e + this.padding.paddingLeft
        }
        if (_.isNumber(b)) {
            var g = this.el.clientHeight / 2;
            e.scrollTop = b - g + d.f + this.padding.paddingTop
        }
        c && c.animation ? this.$el.animate(e, c.animation) : this.$el.prop(e)
    },
    scrollToElement: function(a, b) {
        this.checkElement(a, "scrollToElement");
        var c = a.getBBox().center(), d = this._sx, e = this._sy;
        return c.x*=d, c.y*=e, this.scroll(c.x, c.y, b)
    },
    addPadding: function(a, b, c, d) {
        var e = this.options.padding, f = this.padding = {
            paddingLeft: Math.round(e + (a || 0)),
            paddingTop: Math.round(e + (c || 0))
        }, g = {
            marginBottom: Math.round(e + (d || 0)),
            marginRight: Math.round(e + (b || 0))
        };
        return f.paddingLeft = Math.min(f.paddingLeft, .9 * this.el.clientWidth), f.paddingTop = Math.min(f.paddingTop, .9 * this.el.clientHeight), this.$el.css(f), this.options.paper.$el.css(g), this
    },
    zoom: function(a, b) {
        b = b || {};
        var c, d, e = this.toLocalPoint(this.el.clientWidth / 2, this.el.clientHeight / 2), f = a, g = a;
        if (b.absolute || (f += this._sx, g += this._sy), b.grid && (f = Math.round(f / b.grid) * b.grid, g = Math.round(g / b.grid) * b.grid), b.max && (f = Math.min(b.max, f), g = Math.min(b.max, g)), b.min && (f = Math.max(b.min, f), g = Math.max(b.min, g)), _.isUndefined(b.ox) || _.isUndefined(b.oy))
            c = e.x, d = e.y;
        else {
            var h = f / this._sx, i = g / this._sy;
            c = b.ox - (b.ox - e.x) / h, d = b.oy - (b.oy - e.y) / i
        }
        return this.beforePaperManipulation(), this.options.paper.scale(f, g), this.center(c, d), this.afterPaperManipulation(), this
    },
    zoomToFit: function(a) {
        a = a || {};
        var b = this.options.paper, c = _.clone(b.options.origin);
        return a.fittingBBox = a.fittingBBox || _.extend({}, g.point(c), {
                width: this.$el.width() + this.padding.paddingLeft,
                height: this.$el.height() + this.padding.paddingTop
            }), this.beforePaperManipulation(), b.scaleContentToFit(a), b.setOrigin(c.x, c.y), this.adjustPaper().centerContent(), this.afterPaperManipulation(), this
    },
    startPanning: function(a) {
        a = joint.util.normalizeEvent(a), this._clientX = a.clientX, this._clientY = a.clientY, $(document.body).on({
            "mousemove.panning touchmove.panning": this.pan,
            "mouseup.panning touchend.panning": this.stopPanning
        })
    },
    pan: function(a) {
        a = joint.util.normalizeEvent(a);
        var b = a.clientX - this._clientX, c = a.clientY - this._clientY;
        this.el.scrollTop -= c, this.el.scrollLeft -= b, this._clientX = a.clientX, this._clientY = a.clientY
    },
    stopPanning: function() {
        $(document.body).off(".panning")
    },
    pointerdown: function(a) {
        a.target === this.el && this.options.paper.pointerdown.apply(this.options.paper, arguments)
    },
    pointermove: function(a) {
        a.target === this.el && this.options.paper.pointermove.apply(this.options.paper, arguments)
    },
    getVisibleArea: function() {
        var a = this.options.paper.viewport.getCTM(), b = {
            x: this.el.scrollLeft || 0,
            y: this.el.scrollTop || 0,
            width: this.el.clientWidth,
            height: this.el.clientHeight
        }, c = g.rect(V.transformRect(b, a.inverse()));
        return c.x -= this.padding.paddingLeft || 0, c.y -= this.padding.paddingTop || 0, c
    },
    isElementVisible: function(a) {
        return this.checkElement(a, "isElementVisible"), this.getVisibleArea().containsRect(a.getBBox())
    },
    isPointVisible: function(a) {
        return this.getVisibleArea().containsPoint(a)
    },
    checkElement: function(a, b) {
        if (!(a && a instanceof joint.dia.Element))
            throw new TypeError("ui.PaperScroller." + b + "() accepts instance of joint.dia.Element only")
    },
    onRemove: function() {
        this.stopPanning()
    }
});