/*
 * jQuery UI Autocomplete 1.8.16
 *
 * Copyright 2011, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Autocomplete
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	jquery.ui.position.js
 */
(function(d) {
    var e = 0;
    d.widget("ui.autocomplete", {options: {appendTo: "body", autoFocus: false, delay: 300, minLength: 1, position: {my: "left top", at: "left bottom", collision: "none"}, source: null}, pending: 0, _create: function() {
            var a = this, b = this.element[0].ownerDocument, g;
            this.element.addClass("ui-autocomplete-input").attr("autocomplete", "off").attr({role: "textbox", "aria-autocomplete": "list", "aria-haspopup": "true"}).bind("keydown.autocomplete", function(c) {
                if (!(a.options.disabled || a.element.propAttr("readOnly"))) {
                    g =
                            false;
                    var f = d.ui.keyCode;
                    switch (c.keyCode) {
                        case f.PAGE_UP:
                            a._move("previousPage", c);
                            break;
                        case f.PAGE_DOWN:
                            a._move("nextPage", c);
                            break;
                        case f.UP:
                            a._move("previous", c);
                            c.preventDefault();
                            break;
                        case f.DOWN:
                            a._move("next", c);
                            c.preventDefault();
                            break;
                        case f.ENTER:
                        case f.NUMPAD_ENTER:
                            if (a.menu.active) {
                                g = true;
                                c.preventDefault()
                            }
                        case f.TAB:
                            if (!a.menu.active)
                                return;
                            a.menu.select(c);
                            break;
                        case f.ESCAPE:
                            a.element.val(a.term);
                            a.close(c);
                            break;
                        default:
                            clearTimeout(a.searching);
                            a.searching = setTimeout(function() {
                                if (a.term !=
                                        a.element.val()) {
                                    a.selectedItem = null;
                                    a.search(null, c)
                                }
                            }, a.options.delay);
                            break
                    }
                }
            }).bind("keypress.autocomplete", function(c) {
                if (g) {
                    g = false;
                    c.preventDefault()
                }
            }).bind("focus.autocomplete", function() {
                if (!a.options.disabled) {
                    a.selectedItem = null;
                    a.previous = a.element.val()
                }
            }).bind("blur.autocomplete", function(c) {
                if (!a.options.disabled) {
                    clearTimeout(a.searching);
                    a.closing = setTimeout(function() {
                        a.close(c);
                        a._change(c)
                    }, 150)
                }
            });
            this._initSource();
            this.response = function() {
                return a._response.apply(a, arguments)
            };
            this.menu = d("<ul></ul>").addClass("ui-autocomplete").appendTo(d(this.options.appendTo || "body", b)[0]).mousedown(function(c) {
                var f = a.menu.element[0];
                d(c.target).closest(".ui-menu-item").length || setTimeout(function() {
                    d(document).one("mousedown", function(h) {
                        h.target !== a.element[0] && h.target !== f && !d.ui.contains(f, h.target) && a.close()
                    })
                }, 1);
                setTimeout(function() {
                    clearTimeout(a.closing)
                }, 13)
            }).menu({focus: function(c, f) {
                    f = f.item.data("item.autocomplete");
                    false !== a._trigger("focus", c, {item: f}) && /^key/.test(c.originalEvent.type) &&
                            a.element.val(f.value)
                }, selected: function(c, f) {
                    var h = f.item.data("item.autocomplete"), i = a.previous;
                    if (a.element[0] !== b.activeElement) {
                        a.element.focus();
                        a.previous = i;
                        setTimeout(function() {
                            a.previous = i;
                            a.selectedItem = h
                        }, 1)
                    }
                    false !== a._trigger("select", c, {item: h}) && a.element.val(h.value);
                    a.term = a.element.val();
                    a.close(c);
                    a.selectedItem = h
                }, blur: function() {
                    a.menu.element.is(":visible") && a.element.val() !== a.term && a.element.val(a.term)
                }}).zIndex(this.element.zIndex() + 1).css({top: 0, left: 0}).hide().data("menu");
            d.fn.bgiframe && this.menu.element.bgiframe()
        }, destroy: function() {
            this.element.removeClass("ui-autocomplete-input").removeAttr("autocomplete").removeAttr("role").removeAttr("aria-autocomplete").removeAttr("aria-haspopup");
            this.menu.element.remove();
            d.Widget.prototype.destroy.call(this)
        }, _setOption: function(a, b) {
            d.Widget.prototype._setOption.apply(this, arguments);
            a === "source" && this._initSource();
            if (a === "appendTo")
                this.menu.element.appendTo(d(b || "body", this.element[0].ownerDocument)[0]);
            a === "disabled" &&
                    b && this.xhr && this.xhr.abort()
        }, _initSource: function() {
            var a = this, b, g;
            if (d.isArray(this.options.source)) {
                b = this.options.source;
                this.source = function(c, f) {
                    f(d.ui.autocomplete.filter(b, c.term))
                }
            } else if (typeof this.options.source === "string") {
                g = this.options.source;
                this.source = function(c, f) {
                    a.xhr && a.xhr.abort();
                    a.xhr = d.ajax({url: g, data: c, dataType: "json", autocompleteRequest: ++e, success: function(h) {
                            this.autocompleteRequest === e && f(h)
                        }, error: function() {
                            this.autocompleteRequest === e && f([])
                        }})
                }
            } else
                this.source =
                        this.options.source
        }, search: function(a, b) {
            a = a != null ? a : this.element.val();
            this.term = this.element.val();
            if (a.length < this.options.minLength)
                return this.close(b);
            clearTimeout(this.closing);
            if (this._trigger("search", b) !== false)
                return this._search(a)
        }, _search: function(a) {
            this.pending++;
            this.element.addClass("ui-autocomplete-loading");
            this.source({term: a}, this.response)
        }, _response: function(a) {
            if (!this.options.disabled && a && a.length) {
                a = this._normalize(a);
                this._suggest(a);
                this._trigger("open")
            } else
                this.close();
            this.pending--;
            this.pending || this.element.removeClass("ui-autocomplete-loading")
        }, close: function(a) {
            clearTimeout(this.closing);
            if (this.menu.element.is(":visible")) {
                this.menu.element.hide();
                this.menu.deactivate();
                this._trigger("close", a)
            }
        }, _change: function(a) {
            this.previous !== this.element.val() && this._trigger("change", a, {item: this.selectedItem})
        }, _normalize: function(a) {
            if (a.length && a[0].label && a[0].value)
                return a;
            return d.map(a, function(b) {
                if (typeof b === "string")
                    return{label: b, value: b};
                return d.extend({label: b.label ||
                            b.value, value: b.value || b.label}, b)
            })
        }, _suggest: function(a) {
            var b = this.menu.element.empty().zIndex(this.element.zIndex() + 1);
            this._renderMenu(b, a);
            this.menu.deactivate();
            this.menu.refresh();
            b.show();
            this._resizeMenu();
            b.position(d.extend({of: this.element}, this.options.position));
            this.options.autoFocus && this.menu.next(new d.Event("mouseover"))
        }, _resizeMenu: function() {
            var a = this.menu.element;
            a.outerWidth(Math.max(a.width("").outerWidth(), this.element.outerWidth()))
        }, _renderMenu: function(a, b) {
            var g = this;
            d.each(b, function(c, f) {
                g._renderItem(a, f);
            })
        }, _renderItem: function(a, b) {
            return d("<li></li>").data("item.autocomplete", b).append(d("<a></a>").text(b.label)).appendTo(a)
        }, _move: function(a, b) {
            if (this.menu.element.is(":visible"))
                if (this.menu.first() && /^previous/.test(a) || this.menu.last() && /^next/.test(a)) {
                    this.element.val(this.term);
                    this.menu.deactivate()
                } else
                    this.menu[a](b);
            else
                this.search(null, b)
        }, widget: function() {
            return this.menu.element
        }});
    d.extend(d.ui.autocomplete, {escapeRegex: function(a) {
            return a.replace(/[-[\]{}()*+?.,\\^$|#\s]/g,
                    "\\$&")
        }, filter: function(a, b) {
            var g = new RegExp(d.ui.autocomplete.escapeRegex(b), "i");
            return d.grep(a, function(c) {
                return g.test(c.label || c.value || c)
            })
        }})
})(jQuery);
(function(d) {
    d.widget("ui.menu", {_create: function() {
            var e = this;
            this.element.addClass("ui-menu ui-widget ui-widget-content").attr({role: "listbox", "aria-activedescendant": "ui-active-menuitem"}).click(function(a) {
                if (d(a.target).closest(".ui-menu-item a").length) {
                    a.preventDefault();
                    e.select(a)
                }
            });
            this.refresh()
        }, refresh: function() {
            var e = this;
            this.element.children("li:not(.ui-menu-item):has(a)").addClass("ui-menu-item").attr("role", "menuitem").children("a").addClass("ui-corner-allm").attr("tabindex",
                    -1).mouseenter(function(a) {
                e.activate(a, d(this).parent())
            }).mouseleave(function() {
                e.deactivate()
            })
        }, activate: function(e, a) {
            this.deactivate();
            if (this.hasScroll()) {
                var b = a.offset().top - this.element.offset().top, g = this.element.scrollTop(), c = this.element.height();
                if (b < 0)
                    this.element.scrollTop(g + b);
                else
                    b >= c && this.element.scrollTop(g + b - c + a.height())
            }
            this.active = a.eq(0).children("a").addClass("ui-state-hover").attr("id", "ui-active-menuitem").end();
            this._trigger("focus", e, {item: a})
        }, deactivate: function() {
            if (this.active) {
                this.active.children("a").removeClass("ui-state-hover").removeAttr("id");
                this._trigger("blur");
                this.active = null
            }
        }, next: function(e) {
            this.move("next", ".ui-menu-item:first", e)
        }, previous: function(e) {
            this.move("prev", ".ui-menu-item:last", e)
        }, first: function() {
            return this.active && !this.active.prevAll(".ui-menu-item").length
        }, last: function() {
            return this.active && !this.active.nextAll(".ui-menu-item").length
        }, move: function(e, a, b) {
            if (this.active) {
                e = this.active[e + "All"](".ui-menu-item").eq(0);
                e.length ? this.activate(b, e) : this.activate(b, this.element.children(a))
            } else
                this.activate(b,
                        this.element.children(a))
        }, nextPage: function(e) {
            if (this.hasScroll())
                if (!this.active || this.last())
                    this.activate(e, this.element.children(".ui-menu-item:first"));
                else {
                    var a = this.active.offset().top, b = this.element.height(), g = this.element.children(".ui-menu-item").filter(function() {
                        var c = d(this).offset().top - a - b + d(this).height();
                        return c < 10 && c > -10
                    });
                    g.length || (g = this.element.children(".ui-menu-item:last"));
                    this.activate(e, g)
                }
            else
                this.activate(e, this.element.children(".ui-menu-item").filter(!this.active ||
                        this.last() ? ":first" : ":last"))
        }, previousPage: function(e) {
            if (this.hasScroll())
                if (!this.active || this.first())
                    this.activate(e, this.element.children(".ui-menu-item:last"));
                else {
                    var a = this.active.offset().top, b = this.element.height();
                    result = this.element.children(".ui-menu-item").filter(function() {
                        var g = d(this).offset().top - a + b - d(this).height();
                        return g < 10 && g > -10
                    });
                    result.length || (result = this.element.children(".ui-menu-item:first"));
                    this.activate(e, result)
                }
            else
                this.activate(e, this.element.children(".ui-menu-item").filter(!this.active ||
                        this.first() ? ":last" : ":first"))
        }, hasScroll: function() {
            return this.element.height() < this.element[d.fn.prop ? "prop" : "attr"]("scrollHeight")
        }, select: function(e) {
            this._trigger("selected", e, {item: this.active})
        }})
})(jQuery);