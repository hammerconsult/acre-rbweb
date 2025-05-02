PrimeFaces.widget.InputTextarea = PrimeFaces.widget.BaseWidget.extend({
    occurrences: function (string, subString, allowOverlapping) {
        string += "";
        subString += "";
        if (subString.length <= 0) return (string.length + 1);

        var n = 0,
            pos = 0,
            step = allowOverlapping ? 1 : subString.length;

        while (true) {
            pos = string.indexOf(subString, pos);
            if (pos >= 0) {
                ++n;
                pos += step;
            } else break;
        }
        return n;
    },
    init: function (a) {
        this._super(a);
        this.cfg.rowsDefault = this.jq.attr("rows");
        this.cfg.colsDefault = this.jq.attr("cols");
        PrimeFaces.skinInput(this.jq);
        if (this.cfg.autoComplete) {
            this.setupAutoComplete()
        }
        if (this.cfg.autoResize) {
            this.setupAutoResize()
        }
        if (this.cfg.maxlength) {
            this.applyMaxlength()
        }
        if (this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors)
        }
        if (this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate || "{0}";
            this.updateCounter()
        }
    }, refresh: function (a) {
        if (a.autoComplete) {
            $(PrimeFaces.escapeClientId(a.id + "_panel")).remove();
            $(PrimeFaces.escapeClientId("textarea_simulator")).remove()
        }
        this.init(a)
    }, setupAutoResize: function () {
        var a = this;
        this.jq.keyup(function () {
            a.resize()
        }).focus(function () {
            a.resize()
        }).blur(function () {
            a.resize()
        })
    }, resize: function () {
        var d = 0, a = this.jq.val().split("\n");
        for (var b = a.length - 1; b >= 0; --b) {
            d += Math.floor((a[b].length / this.cfg.colsDefault) + 1)
        }
        var c = (d >= this.cfg.rowsDefault) ? (d + 1) : this.cfg.rowsDefault;
        this.jq.attr("rows", c)
    }, applyMaxlength: function () {
        var _self = this;

        this.jq.keyup(function (e) {
            var value = _self.jq.val(),

            // the length of the value when it has empty
            // lines, counts them as only 1 char. In order to fix
            // problems with data truncation on the database, we simply
            // add the number of occurrences of the empty
            // line so that each empty line counts as 2 characters
            // instead of 1
                newLineOccurrences = _self.occurrences(value, '\n'),
                length = value.length + newLineOccurrences;

            if (length > _self.cfg.maxlength) {
                _self.jq.val(value.substr(0, _self.cfg.maxlength - newLineOccurrences));
            }

            if (_self.counter) {
                _self.updateCounter();
            }
        });
    }, updateCounter: function () {
        var value = this.jq.val(),
        // the length of the value when it has empty lines,
        // counts them as only 1 char. In order to fix
        // problems with data truncation on the database, we simply add
        // the number of occurrences of the empty
        // line so that each empty line counts as 2 characters instead
        // of 1
            length = value.length + this.occurrences(value, '\n');

        if (this.counter) {
            var remaining = this.cfg.maxlength - length, remainingText = this.cfg.counterTemplate
                .replace('{0}', remaining);

            this.counter.html(remainingText);
        }
    },
    setupAutoComplete: function () {
        var c = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow"></div>', a = this;
        this.panel = $(c).appendTo(document.body);
        this.jq.keyup(function (g) {
            var f = $.ui.keyCode;
            switch (g.which) {
                case f.UP:
                case f.LEFT:
                case f.DOWN:
                case f.RIGHT:
                case f.ENTER:
                case f.NUMPAD_ENTER:
                case f.TAB:
                case f.SPACE:
                case f.CONTROL:
                case f.ALT:
                case f.ESCAPE:
                case 224:
                    break;
                default:
                    var d = a.extractQuery();
                    if (d && d.length >= a.cfg.minQueryLength) {
                        if (a.timeout) {
                            a.clearTimeout(a.timeout)
                        }
                        a.timeout = setTimeout(function () {
                            a.search(d)
                        }, a.cfg.queryDelay)
                    }
                    break
            }
        }).keydown(function (j) {
            var d = a.panel.is(":visible"), i = $.ui.keyCode;
            switch (j.which) {
                case i.UP:
                case i.LEFT:
                    if (d) {
                        var h = a.items.filter(".ui-state-highlight"), g = h.length == 0 ? a.items.eq(0) : h.prev();
                        if (g.length == 1) {
                            h.removeClass("ui-state-highlight");
                            g.addClass("ui-state-highlight")
                        }
                        if (a.cfg.scrollHeight) {
                            a.alignScrollbar(g)
                        }
                        j.preventDefault()
                    } else {
                        a.clearTimeout()
                    }
                    break;
                case i.DOWN:
                case i.RIGHT:
                    if (d) {
                        var h = a.items.filter(".ui-state-highlight"), f = h.length == 0 ? a.items.eq(0) : h.next();
                        if (f.length == 1) {
                            h.removeClass("ui-state-highlight");
                            f.addClass("ui-state-highlight")
                        }
                        if (a.cfg.scrollHeight) {
                            a.alignScrollbar(f)
                        }
                        j.preventDefault()
                    } else {
                        a.clearTimeout()
                    }
                    break;
                case i.ENTER:
                case i.NUMPAD_ENTER:
                    if (d) {
                        a.items.filter(".ui-state-highlight").trigger("click");
                        j.preventDefault()
                    } else {
                        a.clearTimeout()
                    }
                    break;
                case i.SPACE:
                case i.CONTROL:
                case i.ALT:
                case i.BACKSPACE:
                case i.ESCAPE:
                case 224:
                    a.clearTimeout();
                    if (d) {
                        a.hide()
                    }
                    break;
                case i.TAB:
                    a.clearTimeout();
                    if (d) {
                        a.items.filter(".ui-state-highlight").trigger("click");
                        a.hide()
                    }
                    break
            }
        });
        $(document.body).bind("mousedown.ui-inputtextarea", function (d) {
            if (a.panel.is(":hidden")) {
                return
            }
            var f = a.panel.offset();
            if (d.target === a.jq.get(0)) {
                return
            }
            if (d.pageX < f.left || d.pageX > f.left + a.panel.width() || d.pageY < f.top || d.pageY > f.top + a.panel.height()) {
                a.hide()
            }
        });
        var b = "resize." + this.id;
        $(window).unbind(b).bind(b, function () {
            if (a.panel.is(":visible")) {
                a.hide()
            }
        });
        this.setupDialogSupport()
    }, bindDynamicEvents: function () {
        var a = this;
        this.items.bind("mouseover", function () {
            var b = $(this);
            if (!b.hasClass("ui-state-highlight")) {
                a.items.filter(".ui-state-highlight").removeClass("ui-state-highlight");
                b.addClass("ui-state-highlight")
            }
        }).bind("click", function (d) {
            var c = $(this), e = c.attr("data-item-value"), b = e.substring(a.query.length);
            a.jq.focus();
            a.jq.insertText(b, a.jq.getSelection().start, true);
            a.invokeItemSelectBehavior(d, e);
            a.hide()
        })
    }, invokeItemSelectBehavior: function (b, d) {
        if (this.cfg.behaviors) {
            var c = this.cfg.behaviors.itemSelect;
            if (c) {
                var a = {params: [{name: this.id + "_itemSelect", value: d}]};
                c.call(this, b, a)
            }
        }
    }, clearTimeout: function () {
        if (this.timeout) {
            clearTimeout(this.timeout)
        }
        this.timeout = null
    }, extractQuery: function () {
        var b = this.jq.getSelection().end, a = /\S+$/.exec(this.jq.get(0).value.slice(0, b)), c = a ? a[0] : null;
        return c
    }, search: function (c) {
        var a = this;
        this.query = c;
        var b = {
            source: this.id, update: this.id, onsuccess: function (h) {
                var f = $(h.documentElement), g = f.find("update");
                for (var d = 0; d < g.length; d++) {
                    var k = g.eq(d), j = k.attr("id"), e = k.text();
                    if (j == a.id) {
                        a.panel.html(e);
                        a.items = a.panel.find(".ui-autocomplete-item");
                        a.bindDynamicEvents();
                        if (a.items.length > 0) {
                            a.items.eq(0).addClass("ui-state-highlight");
                            if (a.cfg.scrollHeight && a.panel.height() > a.cfg.scrollHeight) {
                                a.panel.height(a.cfg.scrollHeight)
                            }
                            if (a.panel.is(":hidden")) {
                                a.show()
                            } else {
                                a.alignPanel()
                            }
                        } else {
                            a.panel.hide()
                        }
                    } else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, j, e)
                    }
                }
                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, f);
                return true
            }
        };
        b.params = [{name: this.id + "_query", value: c}];
        PrimeFaces.ajax.AjaxRequest(b)
    }, alignPanel: function () {
        var b = this.jq.getCaretPosition(), a = this.jq.offset();
        this.panel.css({
            left: a.left + b.left,
            top: a.top + b.top,
            width: this.jq.innerWidth(),
            "z-index": ++PrimeFaces.zindex
        })
    }, alignScrollbar: function (d) {
        var c = d.offset().top - this.items.eq(0).offset().top, a = c + d.height(), g = this.panel.scrollTop(), e = g + this.cfg.scrollHeight, b = parseInt(this.cfg.scrollHeight / d.outerHeight(true));
        if (a < g) {
            this.panel.scrollTop(c)
        } else {
            if (a > e) {
                var f = this.items.eq(d.index() - b + 1);
                this.panel.scrollTop(f.offset().top - this.items.eq(0).offset().top)
            }
        }
    }, show: function () {
        this.alignPanel();
        this.panel.show()
    }, hide: function () {
        this.panel.hide()
    }, setupDialogSupport: function () {
        var a = this.jq.parents(".ui-dialog:first");
        if (a.length == 1) {
            this.panel.css("position", "fixed")
        }
    }
});
