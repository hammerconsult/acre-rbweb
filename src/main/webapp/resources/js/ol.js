// OpenLayers 3. See http://openlayers.org/
// License: https://raw.githubusercontent.com/openlayers/ol3/master/LICENSE.md
// Version: v3.3.0

(function (root, factory) {
    if (typeof define === "function" && define.amd) {
        define([], factory);
    } else if (typeof exports === "object") {
        module.exports = factory();
    } else {
        root.ol = factory();
    }
}(this, function () {
    var OPENLAYERS = {};
    var l, aa = aa || {}, ba = this;

    function m(b) {
        return void 0 !== b
    }

    function t(b, c, d) {
        b = b.split(".");
        d = d || ba;
        b[0] in d || !d.execScript || d.execScript("var " + b[0]);
        for (var e; b.length && (e = b.shift());) !b.length && m(c) ? d[e] = c : d[e] ? d = d[e] : d = d[e] = {}
    }

    function ca() {
    }

    function da(b) {
        b.Pa = function () {
            return b.yf ? b.yf : b.yf = new b
        }
    }

    function ea(b) {
        var c = typeof b;
        if ("object" == c) if (b) {
            if (b instanceof Array) return "array";
            if (b instanceof Object) return c;
            var d = Object.prototype.toString.call(b);
            if ("[object Window]" == d) return "object";
            if ("[object Array]" == d || "number" == typeof b.length && "undefined" != typeof b.splice && "undefined" != typeof b.propertyIsEnumerable && !b.propertyIsEnumerable("splice")) return "array";
            if ("[object Function]" == d || "undefined" != typeof b.call && "undefined" != typeof b.propertyIsEnumerable && !b.propertyIsEnumerable("call")) return "function"
        } else return "null";
        else if ("function" == c && "undefined" == typeof b.call) return "object";
        return c
    }

    function fa(b) {
        return null === b
    }

    function ga(b) {
        return "array" == ea(b)
    }

    function ha(b) {
        var c = ea(b);
        return "array" == c || "object" == c && "number" == typeof b.length
    }

    function ia(b) {
        return "string" == typeof b
    }

    function ja(b) {
        return "number" == typeof b
    }

    function ka(b) {
        return "function" == ea(b)
    }

    function la(b) {
        var c = typeof b;
        return "object" == c && null != b || "function" == c
    }

    function ma(b) {
        return b[na] || (b[na] = ++oa)
    }

    var na = "closure_uid_" + (1E9 * Math.random() >>> 0), oa = 0;

    function pa(b, c, d) {
        return b.call.apply(b.bind, arguments)
    }

    function qa(b, c, d) {
        if (!b) throw Error();
        if (2 < arguments.length) {
            var e = Array.prototype.slice.call(arguments, 2);
            return function () {
                var d = Array.prototype.slice.call(arguments);
                Array.prototype.unshift.apply(d, e);
                return b.apply(c, d)
            }
        }
        return function () {
            return b.apply(c, arguments)
        }
    }

    function ra(b, c, d) {
        ra = Function.prototype.bind && -1 != Function.prototype.bind.toString().indexOf("native code") ? pa : qa;
        return ra.apply(null, arguments)
    }

    function sa(b, c) {
        var d = Array.prototype.slice.call(arguments, 1);
        return function () {
            var c = d.slice();
            c.push.apply(c, arguments);
            return b.apply(this, c)
        }
    }

    var ta = Date.now || function () {
        return +new Date
    };

    function v(b, c) {
        function d() {
        }

        d.prototype = c.prototype;
        b.T = c.prototype;
        b.prototype = new d;
        b.prototype.constructor = b;
        b.Nm = function (b, d, g) {
            for (var h = Array(arguments.length - 2), k = 2; k < arguments.length; k++) h[k - 2] = arguments[k];
            return c.prototype[d].apply(b, h)
        }
    };var ua, va;

    function wa(b) {
        if (Error.captureStackTrace) Error.captureStackTrace(this, wa); else {
            var c = Error().stack;
            c && (this.stack = c)
        }
        b && (this.message = String(b))
    }

    v(wa, Error);
    wa.prototype.name = "CustomError";
    var xa;

    function ya(b, c) {
        var d = b.length - c.length;
        return 0 <= d && b.indexOf(c, d) == d
    }

    function za(b, c) {
        for (var d = b.split("%s"), e = "", f = Array.prototype.slice.call(arguments, 1); f.length && 1 < d.length;) e += d.shift() + f.shift();
        return e + d.join("%s")
    }

    var Aa = String.prototype.trim ? function (b) {
        return b.trim()
    } : function (b) {
        return b.replace(/^[\s\xa0]+|[\s\xa0]+$/g, "")
    };

    function Ba(b) {
        if (!Ca.test(b)) return b;
        -1 != b.indexOf("&") && (b = b.replace(Da, "&amp;"));
        -1 != b.indexOf("<") && (b = b.replace(Ea, "&lt;"));
        -1 != b.indexOf(">") && (b = b.replace(Fa, "&gt;"));
        -1 != b.indexOf('"') && (b = b.replace(Ga, "&quot;"));
        -1 != b.indexOf("'") && (b = b.replace(Ha, "&#39;"));
        -1 != b.indexOf("\x00") && (b = b.replace(Ia, "&#0;"));
        return b
    }

    var Da = /&/g, Ea = /</g, Fa = />/g, Ga = /"/g, Ha = /'/g, Ia = /\x00/g, Ca = /[\x00&<>"']/;

    function La(b) {
        b = m(void 0) ? b.toFixed(void 0) : String(b);
        var c = b.indexOf(".");
        -1 == c && (c = b.length);
        c = Math.max(0, 2 - c);
        return Array(c + 1).join("0") + b
    }

    function Ma(b, c) {
        for (var d = 0, e = Aa(String(b)).split("."), f = Aa(String(c)).split("."), g = Math.max(e.length, f.length), h = 0; 0 == d && h < g; h++) {
            var k = e[h] || "", n = f[h] || "", p = RegExp("(\\d*)(\\D*)", "g"), q = RegExp("(\\d*)(\\D*)", "g");
            do {
                var r = p.exec(k) || ["", "", ""], s = q.exec(n) || ["", "", ""];
                if (0 == r[0].length && 0 == s[0].length) break;
                d = Na(0 == r[1].length ? 0 : parseInt(r[1], 10), 0 == s[1].length ? 0 : parseInt(s[1], 10)) || Na(0 == r[2].length, 0 == s[2].length) || Na(r[2], s[2])
            } while (0 == d)
        }
        return d
    }

    function Na(b, c) {
        return b < c ? -1 : b > c ? 1 : 0
    };var Oa = Array.prototype;

    function Pa(b, c, d) {
        Oa.forEach.call(b, c, d)
    }

    function Qa(b, c) {
        return Oa.filter.call(b, c, void 0)
    }

    function Ra(b, c, d) {
        return Oa.map.call(b, c, d)
    }

    function Sa(b, c) {
        return Oa.some.call(b, c, void 0)
    }

    function Ta(b, c) {
        var d = Ua(b, c, void 0);
        return 0 > d ? null : ia(b) ? b.charAt(d) : b[d]
    }

    function Ua(b, c, d) {
        for (var e = b.length, f = ia(b) ? b.split("") : b, g = 0; g < e; g++) if (g in f && c.call(d, f[g], g, b)) return g;
        return -1
    }

    function Va(b, c) {
        return 0 <= Oa.indexOf.call(b, c, void 0)
    }

    function Wa(b, c) {
        var d = Oa.indexOf.call(b, c, void 0), e;
        (e = 0 <= d) && Oa.splice.call(b, d, 1);
        return e
    }

    function Xa(b) {
        return Oa.concat.apply(Oa, arguments)
    }

    function Za(b) {
        var c = b.length;
        if (0 < c) {
            for (var d = Array(c), e = 0; e < c; e++) d[e] = b[e];
            return d
        }
        return []
    }

    function $a(b, c) {
        for (var d = 1; d < arguments.length; d++) {
            var e = arguments[d];
            if (ha(e)) {
                var f = b.length || 0, g = e.length || 0;
                b.length = f + g;
                for (var h = 0; h < g; h++) b[f + h] = e[h]
            } else b.push(e)
        }
    }

    function ab(b, c, d, e) {
        Oa.splice.apply(b, bb(arguments, 1))
    }

    function bb(b, c, d) {
        return 2 >= arguments.length ? Oa.slice.call(b, c) : Oa.slice.call(b, c, d)
    }

    function cb(b, c) {
        b.sort(c || db)
    }

    function eb(b, c) {
        if (!ha(b) || !ha(c) || b.length != c.length) return !1;
        for (var d = b.length, e = fb, f = 0; f < d; f++) if (!e(b[f], c[f])) return !1;
        return !0
    }

    function db(b, c) {
        return b > c ? 1 : b < c ? -1 : 0
    }

    function fb(b, c) {
        return b === c
    };var gb;
    a:{
        var hb = ba.navigator;
        if (hb) {
            var ib = hb.userAgent;
            if (ib) {
                gb = ib;
                break a
            }
        }
        gb = ""
    }

    function jb(b) {
        return -1 != gb.indexOf(b)
    };

    function kb(b, c, d) {
        for (var e in b) c.call(d, b[e], e, b)
    }

    function ob(b, c) {
        for (var d in b) if (c.call(void 0, b[d], d, b)) return !0;
        return !1
    }

    function pb(b) {
        var c = 0, d;
        for (d in b) c++;
        return c
    }

    function qb(b) {
        var c = [], d = 0, e;
        for (e in b) c[d++] = b[e];
        return c
    }

    function rb(b) {
        var c = [], d = 0, e;
        for (e in b) c[d++] = e;
        return c
    }

    function sb(b, c) {
        return c in b
    }

    function tb(b) {
        var c = ub, d;
        for (d in c) if (b.call(void 0, c[d], d, c)) return d
    }

    function vb(b) {
        for (var c in b) return !1;
        return !0
    }

    function wb(b) {
        for (var c in b) delete b[c]
    }

    function xb(b, c) {
        c in b && delete b[c]
    }

    function yb(b, c, d) {
        return c in b ? b[c] : d
    }

    function zb(b, c) {
        var d = [];
        return c in b ? b[c] : b[c] = d
    }

    function Ab(b) {
        var c = {}, d;
        for (d in b) c[d] = b[d];
        return c
    }

    var Bb = "constructor hasOwnProperty isPrototypeOf propertyIsEnumerable toLocaleString toString valueOf".split(" ");

    function Cb(b, c) {
        for (var d, e, f = 1; f < arguments.length; f++) {
            e = arguments[f];
            for (d in e) b[d] = e[d];
            for (var g = 0; g < Bb.length; g++) d = Bb[g], Object.prototype.hasOwnProperty.call(e, d) && (b[d] = e[d])
        }
    }

    function Db(b) {
        var c = arguments.length;
        if (1 == c && ga(arguments[0])) return Db.apply(null, arguments[0]);
        for (var d = {}, e = 0; e < c; e++) d[arguments[e]] = !0;
        return d
    };var Eb = jb("Opera") || jb("OPR"), Fb = jb("Trident") || jb("MSIE"),
        Gb = jb("Gecko") && -1 == gb.toLowerCase().indexOf("webkit") && !(jb("Trident") || jb("MSIE")),
        Hb = -1 != gb.toLowerCase().indexOf("webkit"), Ib = jb("Macintosh"), Jb = jb("Windows"),
        Kb = jb("Linux") || jb("CrOS");

    function Lb() {
        var b = ba.document;
        return b ? b.documentMode : void 0
    }

    var Mb = function () {
        var b = "", c;
        if (Eb && ba.opera) return b = ba.opera.version, ka(b) ? b() : b;
        Gb ? c = /rv\:([^\);]+)(\)|;)/ : Fb ? c = /\b(?:MSIE|rv)[: ]([^\);]+)(\)|;)/ : Hb && (c = /WebKit\/(\S+)/);
        c && (b = (b = c.exec(gb)) ? b[1] : "");
        return Fb && (c = Lb(), c > parseFloat(b)) ? String(c) : b
    }(), Nb = {};

    function Ob(b) {
        return Nb[b] || (Nb[b] = 0 <= Ma(Mb, b))
    }

    var Qb = ba.document, Rb = Qb && Fb ? Lb() || ("CSS1Compat" == Qb.compatMode ? parseInt(Mb, 10) : 5) : void 0;
    var Sb = "https:" === ba.location.protocol, Tb = Fb && !Ob("9.0") && "" !== Mb;

    function Vb(b, c, d) {
        return Math.min(Math.max(b, c), d)
    }

    function Wb(b, c) {
        var d = b % c;
        return 0 > d * c ? d + c : d
    }

    function Xb(b, c, d) {
        return b + d * (c - b)
    }

    function Yb(b) {
        return b * Math.PI / 180
    };

    function Zb(b) {
        return function (c) {
            if (m(c)) return [Vb(c[0], b[0], b[2]), Vb(c[1], b[1], b[3])]
        }
    }

    function $b(b) {
        return b
    };

    function ac(b, c, d) {
        var e = b.length;
        if (b[0] <= c) return 0;
        if (!(c <= b[e - 1])) if (0 < d) for (d = 1; d < e; ++d) {
            if (b[d] < c) return d - 1
        } else if (0 > d) for (d = 1; d < e; ++d) {
            if (b[d] <= c) return d
        } else for (d = 1; d < e; ++d) {
            if (b[d] == c) return d;
            if (b[d] < c) return b[d - 1] - c < c - b[d] ? d - 1 : d
        }
        return e - 1
    };

    function bc(b) {
        return function (c, d, e) {
            if (m(c)) return c = ac(b, c, e), c = Vb(c + d, 0, b.length - 1), b[c]
        }
    }

    function cc(b, c, d) {
        return function (e, f, g) {
            if (m(e)) return g = 0 < g ? 0 : 0 > g ? 1 : .5, e = Math.floor(Math.log(c / e) / Math.log(b) + g), f = Math.max(e + f, 0), m(d) && (f = Math.min(f, d)), c / Math.pow(b, f)
        }
    };

    function dc(b) {
        if (m(b)) return 0
    }

    function ec(b, c) {
        if (m(b)) return b + c
    }

    function fc(b) {
        var c = 2 * Math.PI / b;
        return function (b, e) {
            if (m(b)) return b = Math.floor((b + e) / c + .5) * c
        }
    }

    function gc() {
        var b = Yb(5);
        return function (c, d) {
            if (m(c)) return Math.abs(c + d) <= b ? 0 : c + d
        }
    };

    function hc(b, c, d) {
        this.center = b;
        this.resolution = c;
        this.rotation = d
    };var ic = !Fb || Fb && 9 <= Rb, jc = !Fb || Fb && 9 <= Rb, kc = Fb && !Ob("9");
    !Hb || Ob("528");
    Gb && Ob("1.9b") || Fb && Ob("8") || Eb && Ob("9.5") || Hb && Ob("528");
    Gb && !Ob("8") || Fb && Ob("9");

    function lc() {
        0 != mc && (nc[ma(this)] = this);
        this.oa = this.oa;
        this.pa = this.pa
    }

    var mc = 0, nc = {};
    lc.prototype.oa = !1;
    lc.prototype.Jc = function () {
        if (!this.oa && (this.oa = !0, this.P(), 0 != mc)) {
            var b = ma(this);
            delete nc[b]
        }
    };

    function oc(b, c) {
        var d = sa(pc, c);
        b.oa ? d.call(void 0) : (b.pa || (b.pa = []), b.pa.push(m(void 0) ? ra(d, void 0) : d))
    }

    lc.prototype.P = function () {
        if (this.pa) for (; this.pa.length;) this.pa.shift()()
    };

    function pc(b) {
        b && "function" == typeof b.Jc && b.Jc()
    };

    function qc(b, c) {
        this.type = b;
        this.b = this.target = c;
        this.e = !1;
        this.ng = !0
    }

    qc.prototype.pb = function () {
        this.e = !0
    };
    qc.prototype.preventDefault = function () {
        this.ng = !1
    };

    function rc(b) {
        b.pb()
    }

    function tc(b) {
        b.preventDefault()
    };var uc = Fb ? "focusout" : "DOMFocusOut";

    function vc(b) {
        vc[" "](b);
        return b
    }

    vc[" "] = ca;

    function wc(b, c) {
        qc.call(this, b ? b.type : "");
        this.relatedTarget = this.b = this.target = null;
        this.i = this.f = this.button = this.screenY = this.screenX = this.clientY = this.clientX = this.offsetY = this.offsetX = 0;
        this.j = this.c = this.d = this.n = !1;
        this.state = null;
        this.g = !1;
        this.a = null;
        b && xc(this, b, c)
    }

    v(wc, qc);
    var yc = [1, 4, 2];

    function xc(b, c, d) {
        b.a = c;
        var e = b.type = c.type;
        b.target = c.target || c.srcElement;
        b.b = d;
        if (d = c.relatedTarget) {
            if (Gb) {
                var f;
                a:{
                    try {
                        vc(d.nodeName);
                        f = !0;
                        break a
                    } catch (g) {
                    }
                    f = !1
                }
                f || (d = null)
            }
        } else "mouseover" == e ? d = c.fromElement : "mouseout" == e && (d = c.toElement);
        b.relatedTarget = d;
        Object.defineProperties ? Object.defineProperties(b, {
            offsetX: {
                configurable: !0,
                enumerable: !0,
                get: b.pf,
                set: b.$l
            }, offsetY: {configurable: !0, enumerable: !0, get: b.qf, set: b.am}
        }) : (b.offsetX = b.pf(), b.offsetY = b.qf());
        b.clientX = void 0 !== c.clientX ? c.clientX :
            c.pageX;
        b.clientY = void 0 !== c.clientY ? c.clientY : c.pageY;
        b.screenX = c.screenX || 0;
        b.screenY = c.screenY || 0;
        b.button = c.button;
        b.f = c.keyCode || 0;
        b.i = c.charCode || ("keypress" == e ? c.keyCode : 0);
        b.n = c.ctrlKey;
        b.d = c.altKey;
        b.c = c.shiftKey;
        b.j = c.metaKey;
        b.g = Ib ? c.metaKey : c.ctrlKey;
        b.state = c.state;
        c.defaultPrevented && b.preventDefault()
    }

    function zc(b) {
        return (ic ? 0 == b.a.button : "click" == b.type ? !0 : !!(b.a.button & yc[0])) && !(Hb && Ib && b.n)
    }

    l = wc.prototype;
    l.pb = function () {
        wc.T.pb.call(this);
        this.a.stopPropagation ? this.a.stopPropagation() : this.a.cancelBubble = !0
    };
    l.preventDefault = function () {
        wc.T.preventDefault.call(this);
        var b = this.a;
        if (b.preventDefault) b.preventDefault(); else if (b.returnValue = !1, kc) try {
            if (b.ctrlKey || 112 <= b.keyCode && 123 >= b.keyCode) b.keyCode = -1
        } catch (c) {
        }
    };
    l.xh = function () {
        return this.a
    };
    l.pf = function () {
        return Hb || void 0 !== this.a.offsetX ? this.a.offsetX : this.a.layerX
    };
    l.$l = function (b) {
        Object.defineProperties(this, {offsetX: {writable: !0, enumerable: !0, configurable: !0, value: b}})
    };
    l.qf = function () {
        return Hb || void 0 !== this.a.offsetY ? this.a.offsetY : this.a.layerY
    };
    l.am = function (b) {
        Object.defineProperties(this, {offsetY: {writable: !0, enumerable: !0, configurable: !0, value: b}})
    };
    var Ac = "closure_listenable_" + (1E6 * Math.random() | 0);

    function Bc(b) {
        return !(!b || !b[Ac])
    }

    var Cc = 0;

    function Dc(b, c, d, e, f) {
        this.Zb = b;
        this.a = null;
        this.src = c;
        this.type = d;
        this.Bc = !!e;
        this.xd = f;
        this.key = ++Cc;
        this.uc = this.bd = !1
    }

    function Ec(b) {
        b.uc = !0;
        b.Zb = null;
        b.a = null;
        b.src = null;
        b.xd = null
    };

    function Fc(b) {
        this.src = b;
        this.a = {};
        this.d = 0
    }

    Fc.prototype.add = function (b, c, d, e, f) {
        var g = b.toString();
        b = this.a[g];
        b || (b = this.a[g] = [], this.d++);
        var h = Gc(b, c, e, f);
        -1 < h ? (c = b[h], d || (c.bd = !1)) : (c = new Dc(c, this.src, g, !!e, f), c.bd = d, b.push(c));
        return c
    };
    Fc.prototype.remove = function (b, c, d, e) {
        b = b.toString();
        if (!(b in this.a)) return !1;
        var f = this.a[b];
        c = Gc(f, c, d, e);
        return -1 < c ? (Ec(f[c]), Oa.splice.call(f, c, 1), 0 == f.length && (delete this.a[b], this.d--), !0) : !1
    };

    function Hc(b, c) {
        var d = c.type;
        if (!(d in b.a)) return !1;
        var e = Wa(b.a[d], c);
        e && (Ec(c), 0 == b.a[d].length && (delete b.a[d], b.d--));
        return e
    }

    function Ic(b, c, d, e, f) {
        b = b.a[c.toString()];
        c = -1;
        b && (c = Gc(b, d, e, f));
        return -1 < c ? b[c] : null
    }

    function Jc(b, c, d) {
        var e = m(c), f = e ? c.toString() : "", g = m(d);
        return ob(b.a, function (b) {
            for (var c = 0; c < b.length; ++c) if (!(e && b[c].type != f || g && b[c].Bc != d)) return !0;
            return !1
        })
    }

    function Gc(b, c, d, e) {
        for (var f = 0; f < b.length; ++f) {
            var g = b[f];
            if (!g.uc && g.Zb == c && g.Bc == !!d && g.xd == e) return f
        }
        return -1
    };var Kc = "closure_lm_" + (1E6 * Math.random() | 0), Lc = {}, Nc = 0;

    function w(b, c, d, e, f) {
        if (ga(c)) {
            for (var g = 0; g < c.length; g++) w(b, c[g], d, e, f);
            return null
        }
        d = Oc(d);
        return Bc(b) ? b.Ra(c, d, e, f) : Pc(b, c, d, !1, e, f)
    }

    function Pc(b, c, d, e, f, g) {
        if (!c) throw Error("Invalid event type");
        var h = !!f, k = Qc(b);
        k || (b[Kc] = k = new Fc(b));
        d = k.add(c, d, e, f, g);
        if (d.a) return d;
        e = Rc();
        d.a = e;
        e.src = b;
        e.Zb = d;
        b.addEventListener ? b.addEventListener(c.toString(), e, h) : b.attachEvent(Sc(c.toString()), e);
        Nc++;
        return d
    }

    function Rc() {
        var b = Tc, c = jc ? function (d) {
            return b.call(c.src, c.Zb, d)
        } : function (d) {
            d = b.call(c.src, c.Zb, d);
            if (!d) return d
        };
        return c
    }

    function Uc(b, c, d, e, f) {
        if (ga(c)) {
            for (var g = 0; g < c.length; g++) Uc(b, c[g], d, e, f);
            return null
        }
        d = Oc(d);
        return Bc(b) ? b.mb.add(String(c), d, !0, e, f) : Pc(b, c, d, !0, e, f)
    }

    function Vc(b, c, d, e, f) {
        if (ga(c)) for (var g = 0; g < c.length; g++) Vc(b, c[g], d, e, f); else d = Oc(d), Bc(b) ? b.Ne(c, d, e, f) : b && (b = Qc(b)) && (c = Ic(b, c, d, !!e, f)) && Wc(c)
    }

    function Wc(b) {
        if (ja(b) || !b || b.uc) return !1;
        var c = b.src;
        if (Bc(c)) return Hc(c.mb, b);
        var d = b.type, e = b.a;
        c.removeEventListener ? c.removeEventListener(d, e, b.Bc) : c.detachEvent && c.detachEvent(Sc(d), e);
        Nc--;
        (d = Qc(c)) ? (Hc(d, b), 0 == d.d && (d.src = null, c[Kc] = null)) : Ec(b);
        return !0
    }

    function Sc(b) {
        return b in Lc ? Lc[b] : Lc[b] = "on" + b
    }

    function Xc(b, c, d, e) {
        var f = !0;
        if (b = Qc(b)) if (c = b.a[c.toString()]) for (c = c.concat(), b = 0; b < c.length; b++) {
            var g = c[b];
            g && g.Bc == d && !g.uc && (g = Yc(g, e), f = f && !1 !== g)
        }
        return f
    }

    function Yc(b, c) {
        var d = b.Zb, e = b.xd || b.src;
        b.bd && Wc(b);
        return d.call(e, c)
    }

    function Tc(b, c) {
        if (b.uc) return !0;
        if (!jc) {
            var d;
            if (!(d = c)) a:{
                d = ["window", "event"];
                for (var e = ba, f; f = d.shift();) if (null != e[f]) e = e[f]; else {
                    d = null;
                    break a
                }
                d = e
            }
            f = d;
            d = new wc(f, this);
            e = !0;
            if (!(0 > f.keyCode || void 0 != f.returnValue)) {
                a:{
                    var g = !1;
                    if (0 == f.keyCode) try {
                        f.keyCode = -1;
                        break a
                    } catch (h) {
                        g = !0
                    }
                    if (g || void 0 == f.returnValue) f.returnValue = !0
                }
                f = [];
                for (g = d.b; g; g = g.parentNode) f.push(g);
                for (var g = b.type, k = f.length - 1; !d.e && 0 <= k; k--) {
                    d.b = f[k];
                    var n = Xc(f[k], g, !0, d), e = e && n
                }
                for (k = 0; !d.e && k < f.length; k++) d.b = f[k], n =
                    Xc(f[k], g, !1, d), e = e && n
            }
            return e
        }
        return Yc(b, new wc(c, this))
    }

    function Qc(b) {
        b = b[Kc];
        return b instanceof Fc ? b : null
    }

    var Zc = "__closure_events_fn_" + (1E9 * Math.random() >>> 0);

    function Oc(b) {
        if (ka(b)) return b;
        b[Zc] || (b[Zc] = function (c) {
            return b.handleEvent(c)
        });
        return b[Zc]
    };

    function $c(b) {
        return function () {
            return b
        }
    }

    var ad = $c(!1), bd = $c(!0), cd = $c(null);

    function dd(b) {
        return b
    }

    function ed(b) {
        var c;
        c = c || 0;
        return function () {
            return b.apply(this, Array.prototype.slice.call(arguments, 0, c))
        }
    }

    function fd(b) {
        var c = arguments, d = c.length;
        return function () {
            for (var b, f = 0; f < d; f++) b = c[f].apply(this, arguments);
            return b
        }
    }

    function gd(b) {
        var c = arguments, d = c.length;
        return function () {
            for (var b = 0; b < d; b++) if (!c[b].apply(this, arguments)) return !1;
            return !0
        }
    };

    function hd() {
        lc.call(this);
        this.mb = new Fc(this);
        this.oh = this;
        this.fe = null
    }

    v(hd, lc);
    hd.prototype[Ac] = !0;
    l = hd.prototype;
    l.addEventListener = function (b, c, d, e) {
        w(this, b, c, d, e)
    };
    l.removeEventListener = function (b, c, d, e) {
        Vc(this, b, c, d, e)
    };
    l.dispatchEvent = function (b) {
        var c, d = this.fe;
        if (d) for (c = []; d; d = d.fe) c.push(d);
        var d = this.oh, e = b.type || b;
        if (ia(b)) b = new qc(b, d); else if (b instanceof qc) b.target = b.target || d; else {
            var f = b;
            b = new qc(e, d);
            Cb(b, f)
        }
        var f = !0, g;
        if (c) for (var h = c.length - 1; !b.e && 0 <= h; h--) g = b.b = c[h], f = id(g, e, !0, b) && f;
        b.e || (g = b.b = d, f = id(g, e, !0, b) && f, b.e || (f = id(g, e, !1, b) && f));
        if (c) for (h = 0; !b.e && h < c.length; h++) g = b.b = c[h], f = id(g, e, !1, b) && f;
        return f
    };
    l.P = function () {
        hd.T.P.call(this);
        if (this.mb) {
            var b = this.mb, c = 0, d;
            for (d in b.a) {
                for (var e = b.a[d], f = 0; f < e.length; f++) ++c, Ec(e[f]);
                delete b.a[d];
                b.d--
            }
        }
        this.fe = null
    };
    l.Ra = function (b, c, d, e) {
        return this.mb.add(String(b), c, !1, d, e)
    };
    l.Ne = function (b, c, d, e) {
        return this.mb.remove(String(b), c, d, e)
    };

    function id(b, c, d, e) {
        c = b.mb.a[String(c)];
        if (!c) return !0;
        c = c.concat();
        for (var f = !0, g = 0; g < c.length; ++g) {
            var h = c[g];
            if (h && !h.uc && h.Bc == d) {
                var k = h.Zb, n = h.xd || h.src;
                h.bd && Hc(b.mb, h);
                f = !1 !== k.call(n, e) && f
            }
        }
        return f && 0 != e.ng
    }

    function jd(b, c, d) {
        return Jc(b.mb, m(c) ? String(c) : void 0, d)
    };

    function kd() {
        hd.call(this);
        this.d = 0
    }

    v(kd, hd);

    function ld(b) {
        Wc(b)
    }

    l = kd.prototype;
    l.l = function () {
        ++this.d;
        this.dispatchEvent("change")
    };
    l.u = function () {
        return this.d
    };
    l.s = function (b, c, d) {
        return w(this, b, c, !1, d)
    };
    l.v = function (b, c, d) {
        return Uc(this, b, c, !1, d)
    };
    l.t = function (b, c, d) {
        Vc(this, b, c, !1, d)
    };
    l.A = ld;

    function md(b, c, d) {
        qc.call(this, b);
        this.key = c;
        this.oldValue = d
    }

    v(md, qc);

    function nd(b, c, d, e) {
        this.source = b;
        this.target = c;
        this.b = d;
        this.d = e;
        this.c = this.a = dd
    }

    nd.prototype.transform = function (b, c) {
        var d = od(this.source, this.b);
        this.a = b;
        this.c = c;
        pd(this.source, this.b, d)
    };

    function qd(b) {
        kd.call(this);
        ma(this);
        this.n = {};
        this.Da = {};
        this.ee = {};
        m(b) && this.C(b)
    }

    v(qd, kd);
    var rd = {}, sd = {}, td = {};

    function ud(b) {
        return rd.hasOwnProperty(b) ? rd[b] : rd[b] = "change:" + b
    }

    function od(b, c) {
        var d = sd.hasOwnProperty(c) ? sd[c] : sd[c] = "get" + (String(c.charAt(0)).toUpperCase() + String(c.substr(1)).toLowerCase()),
            d = b[d];
        return m(d) ? d.call(b) : b.get(c)
    }

    l = qd.prototype;
    l.K = function (b, c, d) {
        d = d || b;
        this.L(b);
        var e = ud(d);
        this.ee[b] = w(c, e, function (c) {
            pd(this, b, c.oldValue)
        }, void 0, this);
        c = new nd(this, c, b, d);
        this.Da[b] = c;
        pd(this, b, this.n[b]);
        return c
    };
    l.get = function (b) {
        var c, d = this.Da;
        d.hasOwnProperty(b) ? (b = d[b], c = od(b.target, b.d), c = b.c(c)) : this.n.hasOwnProperty(b) && (c = this.n[b]);
        return c
    };
    l.G = function () {
        var b = this.Da, c;
        if (vb(this.n)) {
            if (vb(b)) return [];
            c = b
        } else if (vb(b)) c = this.n; else {
            c = {};
            for (var d in this.n) c[d] = !0;
            for (d in b) c[d] = !0
        }
        return rb(c)
    };
    l.I = function () {
        var b = {}, c;
        for (c in this.n) b[c] = this.n[c];
        for (c in this.Da) b[c] = this.get(c);
        return b
    };

    function pd(b, c, d) {
        var e;
        e = ud(c);
        b.dispatchEvent(new md(e, c, d));
        b.dispatchEvent(new md("propertychange", c, d))
    }

    l.set = function (b, c) {
        var d = this.Da;
        if (d.hasOwnProperty(b)) {
            var e = d[b];
            c = e.a(c);
            var d = e.target, e = e.d, f = c,
                g = td.hasOwnProperty(e) ? td[e] : td[e] = "set" + (String(e.charAt(0)).toUpperCase() + String(e.substr(1)).toLowerCase()),
                g = d[g];
            m(g) ? g.call(d, f) : d.set(e, f)
        } else d = this.n[b], this.n[b] = c, pd(this, b, d)
    };
    l.C = function (b) {
        for (var c in b) this.set(c, b[c])
    };
    l.L = function (b) {
        var c = this.ee, d = c[b];
        d && (delete c[b], Wc(d), c = this.get(b), delete this.Da[b], this.n[b] = c)
    };
    l.M = function () {
        for (var b in this.ee) this.L(b)
    };

    function vd(b, c) {
        b[0] += c[0];
        b[1] += c[1];
        return b
    }

    function wd(b, c) {
        var d = b[0], e = b[1], f = c[0], g = c[1], h = f[0], f = f[1], k = g[0], g = g[1], n = k - h, p = g - f,
            d = 0 === n && 0 === p ? 0 : (n * (d - h) + p * (e - f)) / (n * n + p * p || 0);
        0 >= d || (1 <= d ? (h = k, f = g) : (h += d * n, f += d * p));
        return [h, f]
    }

    function xd(b, c) {
        var d = Wb(b + 180, 360) - 180, e = Math.abs(Math.round(3600 * d));
        return Math.floor(e / 3600) + "\u00b0 " + Math.floor(e / 60 % 60) + "\u2032 " + Math.floor(e % 60) + "\u2033 " + c.charAt(0 > d ? 1 : 0)
    }

    function yd(b, c, d) {
        return m(b) ? c.replace("{x}", b[0].toFixed(d)).replace("{y}", b[1].toFixed(d)) : ""
    }

    function zd(b, c) {
        for (var d = !0, e = b.length - 1; 0 <= e; --e) if (b[e] != c[e]) {
            d = !1;
            break
        }
        return d
    }

    function Ad(b, c) {
        var d = Math.cos(c), e = Math.sin(c), f = b[1] * d + b[0] * e;
        b[0] = b[0] * d - b[1] * e;
        b[1] = f;
        return b
    }

    function Bd(b, c) {
        var d = b[0] - c[0], e = b[1] - c[1];
        return d * d + e * e
    }

    function Cd(b, c) {
        return yd(b, "{x}, {y}", c)
    };

    function Dd(b) {
        this.length = b.length || b;
        for (var c = 0; c < this.length; c++) this[c] = b[c] || 0
    }

    Dd.prototype.a = 4;
    Dd.prototype.set = function (b, c) {
        c = c || 0;
        for (var d = 0; d < b.length && c + d < this.length; d++) this[c + d] = b[d]
    };
    Dd.prototype.toString = Array.prototype.join;
    "undefined" == typeof Float32Array && (Dd.BYTES_PER_ELEMENT = 4, Dd.prototype.BYTES_PER_ELEMENT = Dd.prototype.a, Dd.prototype.set = Dd.prototype.set, Dd.prototype.toString = Dd.prototype.toString, t("Float32Array", Dd, void 0));

    function Ed(b) {
        this.length = b.length || b;
        for (var c = 0; c < this.length; c++) this[c] = b[c] || 0
    }

    Ed.prototype.a = 8;
    Ed.prototype.set = function (b, c) {
        c = c || 0;
        for (var d = 0; d < b.length && c + d < this.length; d++) this[c + d] = b[d]
    };
    Ed.prototype.toString = Array.prototype.join;
    if ("undefined" == typeof Float64Array) {
        try {
            Ed.BYTES_PER_ELEMENT = 8
        } catch (Fd) {
        }
        Ed.prototype.BYTES_PER_ELEMENT = Ed.prototype.a;
        Ed.prototype.set = Ed.prototype.set;
        Ed.prototype.toString = Ed.prototype.toString;
        t("Float64Array", Ed, void 0)
    }
    ;

    function Gd(b, c, d, e, f) {
        b[0] = c;
        b[1] = d;
        b[2] = e;
        b[3] = f
    };

    function Hd() {
        var b = Array(16);
        Id(b, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        return b
    }

    function Jd() {
        var b = Array(16);
        Id(b, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        return b
    }

    function Id(b, c, d, e, f, g, h, k, n, p, q, r, s, u, z, y, A) {
        b[0] = c;
        b[1] = d;
        b[2] = e;
        b[3] = f;
        b[4] = g;
        b[5] = h;
        b[6] = k;
        b[7] = n;
        b[8] = p;
        b[9] = q;
        b[10] = r;
        b[11] = s;
        b[12] = u;
        b[13] = z;
        b[14] = y;
        b[15] = A
    }

    function Kd(b, c) {
        b[0] = c[0];
        b[1] = c[1];
        b[2] = c[2];
        b[3] = c[3];
        b[4] = c[4];
        b[5] = c[5];
        b[6] = c[6];
        b[7] = c[7];
        b[8] = c[8];
        b[9] = c[9];
        b[10] = c[10];
        b[11] = c[11];
        b[12] = c[12];
        b[13] = c[13];
        b[14] = c[14];
        b[15] = c[15]
    }

    function Ld(b) {
        b[0] = 1;
        b[1] = 0;
        b[2] = 0;
        b[3] = 0;
        b[4] = 0;
        b[5] = 1;
        b[6] = 0;
        b[7] = 0;
        b[8] = 0;
        b[9] = 0;
        b[10] = 1;
        b[11] = 0;
        b[12] = 0;
        b[13] = 0;
        b[14] = 0;
        b[15] = 1
    }

    function Md(b, c, d) {
        var e = b[0], f = b[1], g = b[2], h = b[3], k = b[4], n = b[5], p = b[6], q = b[7], r = b[8], s = b[9],
            u = b[10], z = b[11], y = b[12], A = b[13], D = b[14];
        b = b[15];
        var x = c[0], M = c[1], Q = c[2], O = c[3], W = c[4], Ja = c[5], lb = c[6], Ka = c[7], mb = c[8], Pb = c[9],
            Ya = c[10], Ub = c[11], nb = c[12], Mc = c[13], sc = c[14];
        c = c[15];
        d[0] = e * x + k * M + r * Q + y * O;
        d[1] = f * x + n * M + s * Q + A * O;
        d[2] = g * x + p * M + u * Q + D * O;
        d[3] = h * x + q * M + z * Q + b * O;
        d[4] = e * W + k * Ja + r * lb + y * Ka;
        d[5] = f * W + n * Ja + s * lb + A * Ka;
        d[6] = g * W + p * Ja + u * lb + D * Ka;
        d[7] = h * W + q * Ja + z * lb + b * Ka;
        d[8] = e * mb + k * Pb + r * Ya + y * Ub;
        d[9] = f * mb + n * Pb + s * Ya + A * Ub;
        d[10] = g * mb + p * Pb + u * Ya + D * Ub;
        d[11] = h * mb + q * Pb + z * Ya + b * Ub;
        d[12] = e * nb + k * Mc + r * sc + y * c;
        d[13] = f * nb + n * Mc + s * sc + A * c;
        d[14] = g * nb + p * Mc + u * sc + D * c;
        d[15] = h * nb + q * Mc + z * sc + b * c
    }

    function Nd(b, c) {
        var d = b[0], e = b[1], f = b[2], g = b[3], h = b[4], k = b[5], n = b[6], p = b[7], q = b[8], r = b[9],
            s = b[10], u = b[11], z = b[12], y = b[13], A = b[14], D = b[15], x = d * k - e * h, M = d * n - f * h,
            Q = d * p - g * h, O = e * n - f * k, W = e * p - g * k, Ja = f * p - g * n, lb = q * y - r * z,
            Ka = q * A - s * z, mb = q * D - u * z, Pb = r * A - s * y, Ya = r * D - u * y, Ub = s * D - u * A,
            nb = x * Ub - M * Ya + Q * Pb + O * mb - W * Ka + Ja * lb;
        0 != nb && (nb = 1 / nb, c[0] = (k * Ub - n * Ya + p * Pb) * nb, c[1] = (-e * Ub + f * Ya - g * Pb) * nb, c[2] = (y * Ja - A * W + D * O) * nb, c[3] = (-r * Ja + s * W - u * O) * nb, c[4] = (-h * Ub + n * mb - p * Ka) * nb, c[5] = (d * Ub - f * mb + g * Ka) * nb, c[6] = (-z * Ja + A * Q - D * M) * nb, c[7] = (q * Ja - s *
            Q + u * M) * nb, c[8] = (h * Ya - k * mb + p * lb) * nb, c[9] = (-d * Ya + e * mb - g * lb) * nb, c[10] = (z * W - y * Q + D * x) * nb, c[11] = (-q * W + r * Q - u * x) * nb, c[12] = (-h * Pb + k * Ka - n * lb) * nb, c[13] = (d * Pb - e * Ka + f * lb) * nb, c[14] = (-z * O + y * M - A * x) * nb, c[15] = (q * O - r * M + s * x) * nb)
    }

    function Od(b, c, d) {
        var e = b[1] * c + b[5] * d + 0 * b[9] + b[13], f = b[2] * c + b[6] * d + 0 * b[10] + b[14],
            g = b[3] * c + b[7] * d + 0 * b[11] + b[15];
        b[12] = b[0] * c + b[4] * d + 0 * b[8] + b[12];
        b[13] = e;
        b[14] = f;
        b[15] = g
    }

    function Pd(b, c, d) {
        Id(b, b[0] * c, b[1] * c, b[2] * c, b[3] * c, b[4] * d, b[5] * d, b[6] * d, b[7] * d, 1 * b[8], 1 * b[9], 1 * b[10], 1 * b[11], b[12], b[13], b[14], b[15])
    }

    function Qd(b, c) {
        var d = b[0], e = b[1], f = b[2], g = b[3], h = b[4], k = b[5], n = b[6], p = b[7], q = Math.cos(c),
            r = Math.sin(c);
        b[0] = d * q + h * r;
        b[1] = e * q + k * r;
        b[2] = f * q + n * r;
        b[3] = g * q + p * r;
        b[4] = d * -r + h * q;
        b[5] = e * -r + k * q;
        b[6] = f * -r + n * q;
        b[7] = g * -r + p * q
    }

    new Float64Array(3);
    new Float64Array(3);
    new Float64Array(4);
    new Float64Array(4);
    new Float64Array(4);
    new Float64Array(16);

    function Rd(b) {
        for (var c = Sd(), d = 0, e = b.length; d < e; ++d) Td(c, b[d]);
        return c
    }

    function Ud(b, c, d) {
        var e = Math.min.apply(null, b), f = Math.min.apply(null, c);
        b = Math.max.apply(null, b);
        c = Math.max.apply(null, c);
        return Vd(e, f, b, c, d)
    }

    function Wd(b, c, d) {
        return m(d) ? (d[0] = b[0] - c, d[1] = b[1] - c, d[2] = b[2] + c, d[3] = b[3] + c, d) : [b[0] - c, b[1] - c, b[2] + c, b[3] + c]
    }

    function Xd(b, c) {
        return m(c) ? (c[0] = b[0], c[1] = b[1], c[2] = b[2], c[3] = b[3], c) : b.slice()
    }

    function Yd(b, c, d) {
        c = c < b[0] ? b[0] - c : b[2] < c ? c - b[2] : 0;
        b = d < b[1] ? b[1] - d : b[3] < d ? d - b[3] : 0;
        return c * c + b * b
    }

    function Zd(b, c) {
        return b[0] <= c[0] && c[2] <= b[2] && b[1] <= c[1] && c[3] <= b[3]
    }

    function $d(b, c, d) {
        return b[0] <= c && c <= b[2] && b[1] <= d && d <= b[3]
    }

    function ae(b, c) {
        var d = b[1], e = b[2], f = b[3], g = c[0], h = c[1], k = 0;
        g < b[0] ? k = k | 16 : g > e && (k = k | 4);
        h < d ? k |= 8 : h > f && (k |= 2);
        0 === k && (k = 1);
        return k
    }

    function Sd() {
        return [Infinity, Infinity, -Infinity, -Infinity]
    }

    function Vd(b, c, d, e, f) {
        return m(f) ? (f[0] = b, f[1] = c, f[2] = d, f[3] = e, f) : [b, c, d, e]
    }

    function be(b, c) {
        var d = b[0], e = b[1];
        return Vd(d, e, d, e, c)
    }

    function ce(b, c) {
        return b[0] == c[0] && b[2] == c[2] && b[1] == c[1] && b[3] == c[3]
    }

    function de(b, c) {
        c[0] < b[0] && (b[0] = c[0]);
        c[2] > b[2] && (b[2] = c[2]);
        c[1] < b[1] && (b[1] = c[1]);
        c[3] > b[3] && (b[3] = c[3]);
        return b
    }

    function Td(b, c) {
        c[0] < b[0] && (b[0] = c[0]);
        c[0] > b[2] && (b[2] = c[0]);
        c[1] < b[1] && (b[1] = c[1]);
        c[1] > b[3] && (b[3] = c[1])
    }

    function ee(b, c, d, e, f) {
        for (; d < e; d += f) {
            var g = b, h = c[d], k = c[d + 1];
            g[0] = Math.min(g[0], h);
            g[1] = Math.min(g[1], k);
            g[2] = Math.max(g[2], h);
            g[3] = Math.max(g[3], k)
        }
        return b
    }

    function fe(b, c) {
        var d;
        return (d = c.call(void 0, ge(b))) || (d = c.call(void 0, he(b))) || (d = c.call(void 0, ie(b))) ? d : (d = c.call(void 0, je(b))) ? d : !1
    }

    function ge(b) {
        return [b[0], b[1]]
    }

    function he(b) {
        return [b[2], b[1]]
    }

    function ke(b) {
        return [(b[0] + b[2]) / 2, (b[1] + b[3]) / 2]
    }

    function le(b, c) {
        var d;
        "bottom-left" === c ? d = ge(b) : "bottom-right" === c ? d = he(b) : "top-left" === c ? d = je(b) : "top-right" === c && (d = ie(b));
        return d
    }

    function me(b, c, d, e) {
        var f = c * e[0] / 2;
        e = c * e[1] / 2;
        c = Math.cos(d);
        d = Math.sin(d);
        f = [-f, -f, f, f];
        e = [-e, e, -e, e];
        var g, h, k;
        for (g = 0; 4 > g; ++g) h = f[g], k = e[g], f[g] = b[0] + h * c - k * d, e[g] = b[1] + h * d + k * c;
        return Ud(f, e, void 0)
    }

    function ne(b) {
        return b[3] - b[1]
    }

    function oe(b, c, d) {
        d = m(d) ? d : Sd();
        pe(b, c) && (d[0] = b[0] > c[0] ? b[0] : c[0], d[1] = b[1] > c[1] ? b[1] : c[1], d[2] = b[2] < c[2] ? b[2] : c[2], d[3] = b[3] < c[3] ? b[3] : c[3]);
        return d
    }

    function je(b) {
        return [b[0], b[3]]
    }

    function ie(b) {
        return [b[2], b[3]]
    }

    function qe(b) {
        return b[2] - b[0]
    }

    function pe(b, c) {
        return b[0] <= c[2] && b[2] >= c[0] && b[1] <= c[3] && b[3] >= c[1]
    }

    function re(b) {
        return b[2] < b[0] || b[3] < b[1]
    }

    function se(b, c) {
        var d = (b[2] - b[0]) / 2 * (c - 1), e = (b[3] - b[1]) / 2 * (c - 1);
        b[0] -= d;
        b[2] += d;
        b[1] -= e;
        b[3] += e
    }

    function te(b, c, d) {
        b = [b[0], b[1], b[0], b[3], b[2], b[1], b[2], b[3]];
        c(b, b, 2);
        return Ud([b[0], b[2], b[4], b[6]], [b[1], b[3], b[5], b[7]], d)
    };/*

 Latitude/longitude spherical geodesy formulae taken from
 http://www.movable-type.co.uk/scripts/latlong.html
 Licensed under CC-BY-3.0.
*/
    function ue(b) {
        this.radius = b
    }

    ue.prototype.d = function (b) {
        for (var c = 0, d = b.length, e = b[d - 1][0], f = b[d - 1][1], g = 0; g < d; g++) var h = b[g][0], k = b[g][1], c = c + Yb(h - e) * (2 + Math.sin(Yb(f)) + Math.sin(Yb(k))), e = h, f = k;
        return c * this.radius * this.radius / 2
    };
    ue.prototype.a = function (b, c) {
        var d = Yb(b[1]), e = Yb(c[1]), f = (e - d) / 2, g = Yb(c[0] - b[0]) / 2,
            d = Math.sin(f) * Math.sin(f) + Math.sin(g) * Math.sin(g) * Math.cos(d) * Math.cos(e);
        return 2 * this.radius * Math.atan2(Math.sqrt(d), Math.sqrt(1 - d))
    };
    ue.prototype.offset = function (b, c, d) {
        var e = Yb(b[1]);
        c /= this.radius;
        var f = Math.asin(Math.sin(e) * Math.cos(c) + Math.cos(e) * Math.sin(c) * Math.cos(d));
        return [180 * (Yb(b[0]) + Math.atan2(Math.sin(d) * Math.sin(c) * Math.cos(e), Math.cos(c) - Math.sin(e) * Math.sin(f))) / Math.PI, 180 * f / Math.PI]
    };
    var ve = new ue(6370997);
    var we = {};
    we.degrees = 2 * Math.PI * ve.radius / 360;
    we.ft = .3048;
    we.m = 1;
    we["us-ft"] = 1200 / 3937;

    function xe(b) {
        this.a = b.code;
        this.d = b.units;
        this.g = m(b.extent) ? b.extent : null;
        this.c = m(b.worldExtent) ? b.worldExtent : null;
        this.b = m(b.axisOrientation) ? b.axisOrientation : "enu";
        this.e = m(b.global) ? b.global : !1;
        this.f = null
    }

    l = xe.prototype;
    l.yh = function () {
        return this.a
    };
    l.J = function () {
        return this.g
    };
    l.Rj = function () {
        return this.d
    };
    l.md = function () {
        return we[this.d]
    };
    l.fi = function () {
        return this.c
    };

    function ye(b) {
        return b.b
    }

    l.Si = function () {
        return this.e
    };
    l.Sj = function (b) {
        this.g = b
    };
    l.gm = function (b) {
        this.c = b
    };
    l.re = function (b, c) {
        if ("degrees" == this.d) return b;
        var d = ze(this, Ae("EPSG:4326")),
            e = [c[0] - b / 2, c[1], c[0] + b / 2, c[1], c[0], c[1] - b / 2, c[0], c[1] + b / 2], e = d(e, e, 2),
            d = (ve.a(e.slice(0, 2), e.slice(2, 4)) + ve.a(e.slice(4, 6), e.slice(6, 8))) / 2, e = this.md();
        m(e) && (d /= e);
        return d
    };
    var Be = {}, Ce = {};

    function De(b) {
        Ee(b);
        Pa(b, function (c) {
            Pa(b, function (b) {
                c !== b && Fe(c, b, Ge)
            })
        })
    }

    function He() {
        var b = Ie, c = Je, d = Ke;
        Pa(Le, function (e) {
            Pa(b, function (b) {
                Fe(e, b, c);
                Fe(b, e, d)
            })
        })
    }

    function Me(b) {
        Be[b.a] = b;
        Fe(b, b, Ge)
    }

    function Ee(b) {
        var c = [];
        Pa(b, function (b) {
            c.push(Me(b))
        })
    }

    function Ne(b) {
        return null != b ? ia(b) ? Ae(b) : b : Ae("EPSG:3857")
    }

    function Fe(b, c, d) {
        b = b.a;
        c = c.a;
        b in Ce || (Ce[b] = {});
        Ce[b][c] = d
    }

    function Pe(b, c, d, e) {
        b = Ae(b);
        c = Ae(c);
        Fe(b, c, Qe(d));
        Fe(c, b, Qe(e))
    }

    function Qe(b) {
        return function (c, d, e) {
            var f = c.length;
            e = m(e) ? e : 2;
            d = m(d) ? d : Array(f);
            var g, h;
            for (h = 0; h < f; h += e) for (g = b([c[h], c[h + 1]]), d[h] = g[0], d[h + 1] = g[1], g = e - 1; 2 <= g; --g) d[h + g] = c[h + g];
            return d
        }
    }

    function Ae(b) {
        var c;
        if (b instanceof xe) c = b; else if (ia(b)) {
            if (c = Be[b], !m(c) && "function" == typeof proj4) {
                var d = proj4.defs(b);
                if (m(d)) {
                    c = d.units;
                    !m(c) && m(d.to_meter) && (c = d.to_meter.toString(), we[c] = d.to_meter);
                    c = new xe({code: b, units: c, axisOrientation: d.axis});
                    Me(c);
                    var e, f, g;
                    for (e in Be) f = proj4.defs(e), m(f) && (g = Ae(e), f === d ? De([g, c]) : (f = proj4(e, b), Pe(g, c, f.forward, f.inverse)))
                } else c = null
            }
        } else c = null;
        return c
    }

    function Re(b, c) {
        return b === c ? !0 : b.d != c.d ? !1 : ze(b, c) === Ge
    }

    function Se(b, c) {
        var d = Ae(b), e = Ae(c);
        return ze(d, e)
    }

    function ze(b, c) {
        var d = b.a, e = c.a, f;
        d in Ce && e in Ce[d] && (f = Ce[d][e]);
        m(f) || (f = Te);
        return f
    }

    function Te(b, c) {
        if (m(c) && b !== c) {
            for (var d = 0, e = b.length; d < e; ++d) c[d] = b[d];
            b = c
        }
        return b
    }

    function Ge(b, c) {
        var d;
        if (m(c)) {
            d = 0;
            for (var e = b.length; d < e; ++d) c[d] = b[d];
            d = c
        } else d = b.slice();
        return d
    }

    function Ue(b, c, d) {
        c = Se(c, d);
        return te(b, c)
    };

    function B(b) {
        qd.call(this);
        b = m(b) ? b : {};
        this.q = [0, 0];
        var c = {};
        c.center = m(b.center) ? b.center : null;
        this.p = Ne(b.projection);
        var d, e, f, g = m(b.minZoom) ? b.minZoom : 0;
        d = m(b.maxZoom) ? b.maxZoom : 28;
        var h = m(b.zoomFactor) ? b.zoomFactor : 2;
        if (m(b.resolutions)) d = b.resolutions, e = d[0], f = d[d.length - 1], d = bc(d); else {
            e = Ne(b.projection);
            f = e.J();
            var k = (null === f ? 360 * we.degrees / we[e.d] : Math.max(qe(f), ne(f))) / 256 / Math.pow(2, 0),
                n = k / Math.pow(2, 28);
            e = b.maxResolution;
            m(e) ? g = 0 : e = k / Math.pow(h, g);
            f = b.minResolution;
            m(f) || (f = m(b.maxZoom) ?
                m(b.maxResolution) ? e / Math.pow(h, d) : k / Math.pow(h, d) : n);
            d = g + Math.floor(Math.log(e / f) / Math.log(h));
            f = e / Math.pow(h, d - g);
            d = cc(h, e, d - g)
        }
        this.e = e;
        this.H = f;
        this.o = g;
        g = m(b.extent) ? Zb(b.extent) : $b;
        (m(b.enableRotation) ? b.enableRotation : 1) ? (e = b.constrainRotation, e = m(e) && !0 !== e ? !1 === e ? ec : ja(e) ? fc(e) : ec : gc()) : e = dc;
        this.D = new hc(g, d, e);
        m(b.resolution) ? c.resolution = b.resolution : m(b.zoom) && (c.resolution = this.constrainResolution(this.e, b.zoom - this.o));
        c.rotation = m(b.rotation) ? b.rotation : 0;
        this.C(c)
    }

    v(B, qd);
    B.prototype.i = function (b) {
        return this.D.center(b)
    };
    B.prototype.constrainResolution = function (b, c, d) {
        return this.D.resolution(b, c || 0, d || 0)
    };
    B.prototype.constrainRotation = function (b, c) {
        return this.D.rotation(b, c || 0)
    };
    B.prototype.b = function () {
        return this.get("center")
    };
    B.prototype.getCenter = B.prototype.b;
    B.prototype.g = function (b) {
        var c = this.b(), d = this.a();
        return [c[0] - d * b[0] / 2, c[1] - d * b[1] / 2, c[0] + d * b[0] / 2, c[1] + d * b[1] / 2]
    };
    B.prototype.N = function () {
        return this.p
    };
    B.prototype.a = function () {
        return this.get("resolution")
    };
    B.prototype.getResolution = B.prototype.a;
    B.prototype.j = function (b, c) {
        return Math.max(qe(b) / c[0], ne(b) / c[1])
    };

    function Ve(b) {
        var c = b.e, d = Math.log(c / b.H) / Math.log(2);
        return function (b) {
            return c / Math.pow(2, b * d)
        }
    }

    B.prototype.c = function () {
        return this.get("rotation")
    };
    B.prototype.getRotation = B.prototype.c;

    function We(b) {
        var c = b.e, d = Math.log(c / b.H) / Math.log(2);
        return function (b) {
            return Math.log(c / b) / Math.log(2) / d
        }
    }

    function Xe(b) {
        var c = b.b(), d = b.p, e = b.a();
        b = b.c();
        return {center: c.slice(), projection: m(d) ? d : null, resolution: e, rotation: b}
    }

    l = B.prototype;
    l.hi = function () {
        var b, c = this.a();
        if (m(c)) {
            var d, e = 0;
            do {
                d = this.constrainResolution(this.e, e);
                if (d == c) {
                    b = e;
                    break
                }
                ++e
            } while (d > this.H)
        }
        return m(b) ? this.o + b : b
    };
    l.ne = function (b, c) {
        if (!re(b)) {
            this.Ha(ke(b));
            var d = this.j(b, c), e = this.constrainResolution(d, 0, 0);
            e < d && (e = this.constrainResolution(e, -1, 0));
            this.f(e)
        }
    };
    l.uh = function (b, c, d) {
        var e = m(d) ? d : {};
        d = m(e.padding) ? e.padding : [0, 0, 0, 0];
        var f = m(e.constrainResolution) ? e.constrainResolution : !0, g = m(e.nearest) ? e.nearest : !1, h;
        m(e.minResolution) ? h = e.minResolution : m(e.maxZoom) ? h = this.constrainResolution(this.e, e.maxZoom - this.o, 0) : h = 0;
        var k = b.k, n = this.c(), e = Math.cos(-n), n = Math.sin(-n), p = Infinity, q = Infinity, r = -Infinity,
            s = -Infinity;
        b = b.B;
        for (var u = 0, z = k.length; u < z; u += b) var y = k[u] * e - k[u + 1] * n, A = k[u] * n + k[u + 1] * e, p = Math.min(p, y), q = Math.min(q, A), r = Math.max(r, y), s = Math.max(s,
            A);
        c = this.j([p, q, r, s], [c[0] - d[1] - d[3], c[1] - d[0] - d[2]]);
        c = isNaN(c) ? h : Math.max(c, h);
        f && (h = this.constrainResolution(c, 0, 0), !g && h < c && (h = this.constrainResolution(h, -1, 0)), c = h);
        this.f(c);
        n = -n;
        g = (p + r) / 2 + (d[1] - d[3]) / 2 * c;
        d = (q + s) / 2 + (d[0] - d[2]) / 2 * c;
        this.Ha([g * e - d * n, d * e + g * n])
    };
    l.mh = function (b, c, d) {
        var e = this.c(), f = Math.cos(-e), e = Math.sin(-e), g = b[0] * f - b[1] * e;
        b = b[1] * f + b[0] * e;
        var h = this.a(), g = g + (c[0] / 2 - d[0]) * h;
        b += (d[1] - c[1] / 2) * h;
        e = -e;
        this.Ha([g * f - b * e, b * f + g * e])
    };

    function Ye(b) {
        return null != b.b() && m(b.a())
    }

    l.rotate = function (b, c) {
        if (m(c)) {
            var d, e = this.b();
            m(e) && (d = [e[0] - c[0], e[1] - c[1]], Ad(d, b - this.c()), vd(d, c));
            this.Ha(d)
        }
        this.r(b)
    };
    l.Ha = function (b) {
        this.set("center", b)
    };
    B.prototype.setCenter = B.prototype.Ha;

    function Ze(b, c) {
        b.q[1] += c
    }

    B.prototype.f = function (b) {
        this.set("resolution", b)
    };
    B.prototype.setResolution = B.prototype.f;
    B.prototype.r = function (b) {
        this.set("rotation", b)
    };
    B.prototype.setRotation = B.prototype.r;
    B.prototype.S = function (b) {
        b = this.constrainResolution(this.e, b - this.o, 0);
        this.f(b)
    };

    function $e(b) {
        return 1 - Math.pow(1 - b, 3)
    };

    function af(b) {
        return 3 * b * b - 2 * b * b * b
    }

    function bf(b) {
        return b
    }

    function cf(b) {
        return .5 > b ? af(2 * b) : 1 - af(2 * (b - .5))
    };

    function df(b) {
        var c = b.source, d = m(b.start) ? b.start : ta(), e = c[0], f = c[1], g = m(b.duration) ? b.duration : 1E3,
            h = m(b.easing) ? b.easing : af;
        return function (b, c) {
            if (c.time < d) return c.animate = !0, c.viewHints[0] += 1, !0;
            if (c.time < d + g) {
                var p = 1 - h((c.time - d) / g), q = e - c.viewState.center[0], r = f - c.viewState.center[1];
                c.animate = !0;
                c.viewState.center[0] += p * q;
                c.viewState.center[1] += p * r;
                c.viewHints[0] += 1;
                return !0
            }
            return !1
        }
    }

    function ef(b) {
        var c = m(b.rotation) ? b.rotation : 0, d = m(b.start) ? b.start : ta(), e = m(b.duration) ? b.duration : 1E3,
            f = m(b.easing) ? b.easing : af, g = m(b.anchor) ? b.anchor : null;
        return function (b, k) {
            if (k.time < d) return k.animate = !0, k.viewHints[0] += 1, !0;
            if (k.time < d + e) {
                var n = 1 - f((k.time - d) / e), n = (c - k.viewState.rotation) * n;
                k.animate = !0;
                k.viewState.rotation += n;
                if (null !== g) {
                    var p = k.viewState.center;
                    p[0] -= g[0];
                    p[1] -= g[1];
                    Ad(p, n);
                    vd(p, g)
                }
                k.viewHints[0] += 1;
                return !0
            }
            return !1
        }
    }

    function ff(b) {
        var c = b.resolution, d = m(b.start) ? b.start : ta(), e = m(b.duration) ? b.duration : 1E3,
            f = m(b.easing) ? b.easing : af;
        return function (b, h) {
            if (h.time < d) return h.animate = !0, h.viewHints[0] += 1, !0;
            if (h.time < d + e) {
                var k = 1 - f((h.time - d) / e), n = c - h.viewState.resolution;
                h.animate = !0;
                h.viewState.resolution += k * n;
                h.viewHints[0] += 1;
                return !0
            }
            return !1
        }
    };

    function gf(b, c, d, e) {
        return m(e) ? (e[0] = b, e[1] = c, e[2] = d, e) : [b, c, d]
    }

    function hf(b, c, d) {
        return b + "/" + c + "/" + d
    }

    function jf(b) {
        var c = b[0], d = Array(c), e = 1 << c - 1, f, g;
        for (f = 0; f < c; ++f) g = 48, b[1] & e && (g += 1), b[2] & e && (g += 2), d[f] = String.fromCharCode(g), e >>= 1;
        return d.join("")
    }

    function kf(b) {
        return hf(b[0], b[1], b[2])
    };

    function lf(b, c, d, e) {
        this.a = b;
        this.c = c;
        this.b = d;
        this.d = e
    }

    function mf(b, c, d, e, f) {
        return m(f) ? (f.a = b, f.c = c, f.b = d, f.d = e, f) : new lf(b, c, d, e)
    }

    lf.prototype.contains = function (b) {
        return nf(this, b[1], b[2])
    };

    function nf(b, c, d) {
        return b.a <= c && c <= b.c && b.b <= d && d <= b.d
    }

    function of(b, c) {
        return b.a == c.a && b.b == c.b && b.c == c.c && b.d == c.d
    };

    function pf(b) {
        this.d = b.html;
        this.a = m(b.tileRanges) ? b.tileRanges : null
    }

    pf.prototype.b = function () {
        return this.d
    };
    var qf = !Fb || Fb && 9 <= Rb;
    !Gb && !Fb || Fb && Fb && 9 <= Rb || Gb && Ob("1.9.1");
    Fb && Ob("9");
    Db("area base br col command embed hr img input keygen link meta param source track wbr".split(" "));
    Db("action", "cite", "data", "formaction", "href", "manifest", "poster", "src");
    Db("embed", "iframe", "link", "object", "script", "style", "template");

    function rf(b, c) {
        this.x = m(b) ? b : 0;
        this.y = m(c) ? c : 0
    }

    l = rf.prototype;
    l.clone = function () {
        return new rf(this.x, this.y)
    };
    l.ceil = function () {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        return this
    };
    l.floor = function () {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        return this
    };
    l.round = function () {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        return this
    };
    l.scale = function (b, c) {
        var d = ja(c) ? c : b;
        this.x *= b;
        this.y *= d;
        return this
    };

    function sf(b, c) {
        this.width = b;
        this.height = c
    }

    l = sf.prototype;
    l.clone = function () {
        return new sf(this.width, this.height)
    };
    l.la = function () {
        return !(this.width * this.height)
    };
    l.ceil = function () {
        this.width = Math.ceil(this.width);
        this.height = Math.ceil(this.height);
        return this
    };
    l.floor = function () {
        this.width = Math.floor(this.width);
        this.height = Math.floor(this.height);
        return this
    };
    l.round = function () {
        this.width = Math.round(this.width);
        this.height = Math.round(this.height);
        return this
    };
    l.scale = function (b, c) {
        var d = ja(c) ? c : b;
        this.width *= b;
        this.height *= d;
        return this
    };

    function tf(b) {
        return b ? new uf(vf(b)) : xa || (xa = new uf)
    }

    function wf(b) {
        var c = document;
        return ia(b) ? c.getElementById(b) : b
    }

    function xf(b, c) {
        kb(c, function (c, e) {
            "style" == e ? b.style.cssText = c : "class" == e ? b.className = c : "for" == e ? b.htmlFor = c : e in yf ? b.setAttribute(yf[e], c) : 0 == e.lastIndexOf("aria-", 0) || 0 == e.lastIndexOf("data-", 0) ? b.setAttribute(e, c) : b[e] = c
        })
    }

    var yf = {
        cellpadding: "cellPadding",
        cellspacing: "cellSpacing",
        colspan: "colSpan",
        frameborder: "frameBorder",
        height: "height",
        maxlength: "maxLength",
        role: "role",
        rowspan: "rowSpan",
        type: "type",
        usemap: "useMap",
        valign: "vAlign",
        width: "width"
    };

    function zf(b) {
        b = b.document.documentElement;
        return new sf(b.clientWidth, b.clientHeight)
    }

    function Af(b, c, d) {
        var e = arguments, f = document, g = e[0], h = e[1];
        if (!qf && h && (h.name || h.type)) {
            g = ["<", g];
            h.name && g.push(' name="', Ba(h.name), '"');
            if (h.type) {
                g.push(' type="', Ba(h.type), '"');
                var k = {};
                Cb(k, h);
                delete k.type;
                h = k
            }
            g.push(">");
            g = g.join("")
        }
        g = f.createElement(g);
        h && (ia(h) ? g.className = h : ga(h) ? g.className = h.join(" ") : xf(g, h));
        2 < e.length && Bf(f, g, e, 2);
        return g
    }

    function Bf(b, c, d, e) {
        function f(d) {
            d && c.appendChild(ia(d) ? b.createTextNode(d) : d)
        }

        for (; e < d.length; e++) {
            var g = d[e];
            !ha(g) || la(g) && 0 < g.nodeType ? f(g) : Pa(Cf(g) ? Za(g) : g, f)
        }
    }

    function Df(b) {
        return document.createElement(b)
    }

    function Ef(b, c) {
        Bf(vf(b), b, arguments, 1)
    }

    function Ff(b) {
        for (var c; c = b.firstChild;) b.removeChild(c)
    }

    function Gf(b, c, d) {
        b.insertBefore(c, b.childNodes[d] || null)
    }

    function Hf(b) {
        b && b.parentNode && b.parentNode.removeChild(b)
    }

    function If(b, c) {
        var d = c.parentNode;
        d && d.replaceChild(b, c)
    }

    function Jf(b) {
        if (void 0 != b.firstElementChild) b = b.firstElementChild; else for (b = b.firstChild; b && 1 != b.nodeType;) b = b.nextSibling;
        return b
    }

    function Kf(b, c) {
        if (b.contains && 1 == c.nodeType) return b == c || b.contains(c);
        if ("undefined" != typeof b.compareDocumentPosition) return b == c || Boolean(b.compareDocumentPosition(c) & 16);
        for (; c && b != c;) c = c.parentNode;
        return c == b
    }

    function vf(b) {
        return 9 == b.nodeType ? b : b.ownerDocument || b.document
    }

    function Cf(b) {
        if (b && "number" == typeof b.length) {
            if (la(b)) return "function" == typeof b.item || "string" == typeof b.item;
            if (ka(b)) return "function" == typeof b.item
        }
        return !1
    }

    function uf(b) {
        this.a = b || ba.document || document
    }

    function Lf() {
        return !0
    }

    function Mf(b) {
        var c = b.a;
        b = Hb ? c.body || c.documentElement : c.documentElement;
        c = c.parentWindow || c.defaultView;
        return Fb && Ob("10") && c.pageYOffset != b.scrollTop ? new rf(b.scrollLeft, b.scrollTop) : new rf(c.pageXOffset || b.scrollLeft, c.pageYOffset || b.scrollTop)
    }

    uf.prototype.appendChild = function (b, c) {
        b.appendChild(c)
    };
    uf.prototype.contains = Kf;

    function Nf(b, c) {
        var d = Df("CANVAS");
        m(b) && (d.width = b);
        m(c) && (d.height = c);
        return d.getContext("2d")
    }

    var Of = function () {
        var b;
        return function () {
            if (!m(b)) if (ba.getComputedStyle) {
                var c = Df("P"), d, e = {
                    webkitTransform: "-webkit-transform",
                    OTransform: "-o-transform",
                    msTransform: "-ms-transform",
                    MozTransform: "-moz-transform",
                    transform: "transform"
                };
                document.body.appendChild(c);
                for (var f in e) f in c.style && (c.style[f] = "translate(1px,1px)", d = ba.getComputedStyle(c).getPropertyValue(e[f]));
                Hf(c);
                b = d && "none" !== d
            } else b = !1;
            return b
        }
    }(), Pf = function () {
        var b;
        return function () {
            if (!m(b)) if (ba.getComputedStyle) {
                var c = Df("P"),
                    d, e = {
                        webkitTransform: "-webkit-transform",
                        OTransform: "-o-transform",
                        msTransform: "-ms-transform",
                        MozTransform: "-moz-transform",
                        transform: "transform"
                    };
                document.body.appendChild(c);
                for (var f in e) f in c.style && (c.style[f] = "translate3d(1px,1px,1px)", d = ba.getComputedStyle(c).getPropertyValue(e[f]));
                Hf(c);
                b = d && "none" !== d
            } else b = !1;
            return b
        }
    }();

    function Qf(b, c) {
        var d = b.style;
        d.WebkitTransform = c;
        d.MozTransform = c;
        d.a = c;
        d.msTransform = c;
        d.transform = c;
        Fb && !Tb && (b.style.transformOrigin = "0 0")
    }

    function Sf(b, c) {
        var d;
        if (Pf()) {
            if (m(6)) {
                var e = Array(16);
                for (d = 0; 16 > d; ++d) e[d] = c[d].toFixed(6);
                d = e.join(",")
            } else d = c.join(",");
            Qf(b, "matrix3d(" + d + ")")
        } else if (Of()) {
            e = [c[0], c[1], c[4], c[5], c[12], c[13]];
            if (m(6)) {
                var f = Array(6);
                for (d = 0; 6 > d; ++d) f[d] = e[d].toFixed(6);
                d = f.join(",")
            } else d = e.join(",");
            Qf(b, "matrix(" + d + ")")
        } else b.style.left = Math.round(c[12]) + "px", b.style.top = Math.round(c[13]) + "px"
    };var Tf = ["experimental-webgl", "webgl", "webkit-3d", "moz-webgl"];

    function Uf(b, c) {
        var d, e, f = Tf.length;
        for (e = 0; e < f; ++e) try {
            if (d = b.getContext(Tf[e], c), null !== d) return d
        } catch (g) {
        }
        return null
    };var Vf, Wf = ba.devicePixelRatio || 1, Xf = "ArrayBuffer" in ba, Yf = !1, Zf = function () {
            if (!("HTMLCanvasElement" in ba)) return !1;
            try {
                var b = Nf();
                if (null === b) return !1;
                m(b.setLineDash) && (Yf = !0);
                return !0
            } catch (c) {
                return !1
            }
        }(), $f = "DeviceOrientationEvent" in ba, ag = "geolocation" in ba.navigator, bg = "ontouchstart" in ba,
        cg = "PointerEvent" in ba, dg = !!ba.navigator.msPointerEnabled, eg = !1, fg, gg = [];
    if ("WebGLRenderingContext" in ba) try {
        var hg = Df("CANVAS"), ig = Uf(hg, {th: !0});
        null !== ig && (eg = !0, fg = ig.getParameter(ig.MAX_TEXTURE_SIZE), gg = ig.getSupportedExtensions())
    } catch (jg) {
    }
    Vf = eg;
    va = gg;
    ua = fg;

    function kg(b, c, d) {
        qc.call(this, b, d);
        this.element = c
    }

    v(kg, qc);

    function lg(b) {
        qd.call(this);
        this.a = m(b) ? b : [];
        mg(this)
    }

    v(lg, qd);
    l = lg.prototype;
    l.clear = function () {
        for (; 0 < this.Ib();) this.pop()
    };
    l.xe = function (b) {
        var c, d;
        c = 0;
        for (d = b.length; c < d; ++c) this.push(b[c]);
        return this
    };
    l.forEach = function (b, c) {
        Pa(this.a, b, c)
    };
    l.hj = function () {
        return this.a
    };
    l.item = function (b) {
        return this.a[b]
    };
    l.Ib = function () {
        return this.get("length")
    };
    l.yd = function (b, c) {
        ab(this.a, b, 0, c);
        mg(this);
        this.dispatchEvent(new kg("add", c, this))
    };
    l.pop = function () {
        return this.Le(this.Ib() - 1)
    };
    l.push = function (b) {
        var c = this.a.length;
        this.yd(c, b);
        return c
    };
    l.remove = function (b) {
        var c = this.a, d, e;
        d = 0;
        for (e = c.length; d < e; ++d) if (c[d] === b) return this.Le(d)
    };
    l.Le = function (b) {
        var c = this.a[b];
        Oa.splice.call(this.a, b, 1);
        mg(this);
        this.dispatchEvent(new kg("remove", c, this));
        return c
    };
    l.Ul = function (b, c) {
        var d = this.Ib();
        if (b < d) d = this.a[b], this.a[b] = c, this.dispatchEvent(new kg("remove", d, this)), this.dispatchEvent(new kg("add", c, this)); else {
            for (; d < b; ++d) this.yd(d, void 0);
            this.yd(b, c)
        }
    };

    function mg(b) {
        b.set("length", b.a.length)
    };var ng = /^#(?:[0-9a-f]{3}){1,2}$/i,
        og = /^(?:rgb)?\((0|[1-9]\d{0,2}),\s?(0|[1-9]\d{0,2}),\s?(0|[1-9]\d{0,2})\)$/i,
        pg = /^(?:rgba)?\((0|[1-9]\d{0,2}),\s?(0|[1-9]\d{0,2}),\s?(0|[1-9]\d{0,2}),\s?(0|1|0\.\d{0,10})\)$/i;

    function qg(b) {
        return ga(b) ? b : rg(b)
    }

    function sg(b) {
        if (!ia(b)) {
            var c = b[0];
            c != (c | 0) && (c = c + .5 | 0);
            var d = b[1];
            d != (d | 0) && (d = d + .5 | 0);
            var e = b[2];
            e != (e | 0) && (e = e + .5 | 0);
            b = "rgba(" + c + "," + d + "," + e + "," + b[3] + ")"
        }
        return b
    }

    var rg = function () {
        var b = {}, c = 0;
        return function (d) {
            var e;
            if (b.hasOwnProperty(d)) e = b[d]; else {
                if (1024 <= c) {
                    e = 0;
                    for (var f in b) 0 === (e++ & 3) && (delete b[f], --c)
                }
                var g, h;
                ng.exec(d) ? (h = 3 == d.length - 1 ? 1 : 2, e = parseInt(d.substr(1 + 0 * h, h), 16), f = parseInt(d.substr(1 + 1 * h, h), 16), g = parseInt(d.substr(1 + 2 * h, h), 16), 1 == h && (e = (e << 4) + e, f = (f << 4) + f, g = (g << 4) + g), e = [e, f, g, 1]) : (h = pg.exec(d)) ? (e = Number(h[1]), f = Number(h[2]), g = Number(h[3]), h = Number(h[4]), e = [e, f, g, h], e = tg(e, e)) : (h = og.exec(d)) ? (e = Number(h[1]), f = Number(h[2]), g = Number(h[3]),
                    e = [e, f, g, 1], e = tg(e, e)) : e = void 0;
                b[d] = e;
                ++c
            }
            return e
        }
    }();

    function tg(b, c) {
        var d = m(c) ? c : [];
        d[0] = Vb(b[0] + .5 | 0, 0, 255);
        d[1] = Vb(b[1] + .5 | 0, 0, 255);
        d[2] = Vb(b[2] + .5 | 0, 0, 255);
        d[3] = Vb(b[3], 0, 1);
        return d
    };

    function ug() {
        this.g = Hd();
        this.d = void 0;
        this.a = Hd();
        this.c = void 0;
        this.b = Hd();
        this.e = void 0;
        this.f = Hd();
        this.i = void 0;
        this.n = Hd()
    }

    function vg(b, c, d, e, f) {
        var g = !1;
        m(c) && c !== b.d && (g = b.a, Ld(g), g[12] = c, g[13] = c, g[14] = c, g[15] = 1, b.d = c, g = !0);
        if (m(d) && d !== b.c) {
            g = b.b;
            Ld(g);
            g[0] = d;
            g[5] = d;
            g[10] = d;
            g[15] = 1;
            var h = -.5 * d + .5;
            g[12] = h;
            g[13] = h;
            g[14] = h;
            g[15] = 1;
            b.c = d;
            g = !0
        }
        m(e) && e !== b.e && (g = Math.cos(e), h = Math.sin(e), Id(b.f, .213 + .787 * g - .213 * h, .213 - .213 * g + .143 * h, .213 - .213 * g - .787 * h, 0, .715 - .715 * g - .715 * h, .715 + .285 * g + .14 * h, .715 - .715 * g + .715 * h, 0, .072 - .072 * g + .928 * h, .072 - .072 * g - .283 * h, .072 + .928 * g + .072 * h, 0, 0, 0, 0, 1), b.e = e, g = !0);
        m(f) && f !== b.i && (Id(b.n, .213 + .787 *
            f, .213 - .213 * f, .213 - .213 * f, 0, .715 - .715 * f, .715 + .285 * f, .715 - .715 * f, 0, .072 - .072 * f, .072 - .072 * f, .072 + .928 * f, 0, 0, 0, 0, 1), b.i = f, g = !0);
        g && (g = b.g, Ld(g), m(d) && Md(g, b.b, g), m(c) && Md(g, b.a, g), m(f) && Md(g, b.n, g), m(e) && Md(g, b.f, g));
        return b.g
    };

    function wg(b) {
        if (b.classList) return b.classList;
        b = b.className;
        return ia(b) && b.match(/\S+/g) || []
    }

    function xg(b, c) {
        return b.classList ? b.classList.contains(c) : Va(wg(b), c)
    }

    function yg(b, c) {
        b.classList ? b.classList.add(c) : xg(b, c) || (b.className += 0 < b.className.length ? " " + c : c)
    }

    function zg(b, c) {
        b.classList ? b.classList.remove(c) : xg(b, c) && (b.className = Qa(wg(b), function (b) {
            return b != c
        }).join(" "))
    }

    function Ag(b, c) {
        xg(b, c) ? zg(b, c) : yg(b, c)
    };

    function Bg(b, c, d, e) {
        this.top = b;
        this.right = c;
        this.bottom = d;
        this.left = e
    }

    l = Bg.prototype;
    l.clone = function () {
        return new Bg(this.top, this.right, this.bottom, this.left)
    };
    l.contains = function (b) {
        return this && b ? b instanceof Bg ? b.left >= this.left && b.right <= this.right && b.top >= this.top && b.bottom <= this.bottom : b.x >= this.left && b.x <= this.right && b.y >= this.top && b.y <= this.bottom : !1
    };
    l.ceil = function () {
        this.top = Math.ceil(this.top);
        this.right = Math.ceil(this.right);
        this.bottom = Math.ceil(this.bottom);
        this.left = Math.ceil(this.left);
        return this
    };
    l.floor = function () {
        this.top = Math.floor(this.top);
        this.right = Math.floor(this.right);
        this.bottom = Math.floor(this.bottom);
        this.left = Math.floor(this.left);
        return this
    };
    l.round = function () {
        this.top = Math.round(this.top);
        this.right = Math.round(this.right);
        this.bottom = Math.round(this.bottom);
        this.left = Math.round(this.left);
        return this
    };
    l.scale = function (b, c) {
        var d = ja(c) ? c : b;
        this.left *= b;
        this.right *= b;
        this.top *= d;
        this.bottom *= d;
        return this
    };

    function Cg(b, c, d, e) {
        this.left = b;
        this.top = c;
        this.width = d;
        this.height = e
    }

    l = Cg.prototype;
    l.clone = function () {
        return new Cg(this.left, this.top, this.width, this.height)
    };
    l.contains = function (b) {
        return b instanceof Cg ? this.left <= b.left && this.left + this.width >= b.left + b.width && this.top <= b.top && this.top + this.height >= b.top + b.height : b.x >= this.left && b.x <= this.left + this.width && b.y >= this.top && b.y <= this.top + this.height
    };

    function Dg(b, c) {
        var d = c.x < b.left ? b.left - c.x : Math.max(c.x - (b.left + b.width), 0),
            e = c.y < b.top ? b.top - c.y : Math.max(c.y - (b.top + b.height), 0);
        return d * d + e * e
    }

    l.distance = function (b) {
        return Math.sqrt(Dg(this, b))
    };
    l.ceil = function () {
        this.left = Math.ceil(this.left);
        this.top = Math.ceil(this.top);
        this.width = Math.ceil(this.width);
        this.height = Math.ceil(this.height);
        return this
    };
    l.floor = function () {
        this.left = Math.floor(this.left);
        this.top = Math.floor(this.top);
        this.width = Math.floor(this.width);
        this.height = Math.floor(this.height);
        return this
    };
    l.round = function () {
        this.left = Math.round(this.left);
        this.top = Math.round(this.top);
        this.width = Math.round(this.width);
        this.height = Math.round(this.height);
        return this
    };
    l.scale = function (b, c) {
        var d = ja(c) ? c : b;
        this.left *= b;
        this.width *= b;
        this.top *= d;
        this.height *= d;
        return this
    };

    function Eg(b, c) {
        var d = vf(b);
        return d.defaultView && d.defaultView.getComputedStyle && (d = d.defaultView.getComputedStyle(b, null)) ? d[c] || d.getPropertyValue(c) || "" : ""
    }

    function Fg(b, c) {
        return Eg(b, c) || (b.currentStyle ? b.currentStyle[c] : null) || b.style && b.style[c]
    }

    function Gg(b, c, d) {
        var e;
        c instanceof rf ? (e = c.x, c = c.y) : (e = c, c = d);
        b.style.left = Hg(e);
        b.style.top = Hg(c)
    }

    function Ig(b) {
        var c;
        try {
            c = b.getBoundingClientRect()
        } catch (d) {
            return {left: 0, top: 0, right: 0, bottom: 0}
        }
        Fb && b.ownerDocument.body && (b = b.ownerDocument, c.left -= b.documentElement.clientLeft + b.body.clientLeft, c.top -= b.documentElement.clientTop + b.body.clientTop);
        return c
    }

    function Jg(b) {
        if (1 == b.nodeType) return b = Ig(b), new rf(b.left, b.top);
        var c = ka(b.xh), d = b;
        b.targetTouches && b.targetTouches.length ? d = b.targetTouches[0] : c && b.a.targetTouches && b.a.targetTouches.length && (d = b.a.targetTouches[0]);
        return new rf(d.clientX, d.clientY)
    }

    function Hg(b) {
        "number" == typeof b && (b = b + "px");
        return b
    }

    function Kg(b) {
        var c = Lg;
        if ("none" != Fg(b, "display")) return c(b);
        var d = b.style, e = d.display, f = d.visibility, g = d.position;
        d.visibility = "hidden";
        d.position = "absolute";
        d.display = "inline";
        b = c(b);
        d.display = e;
        d.position = g;
        d.visibility = f;
        return b
    }

    function Lg(b) {
        var c = b.offsetWidth, d = b.offsetHeight, e = Hb && !c && !d;
        return m(c) && !e || !b.getBoundingClientRect ? new sf(c, d) : (b = Ig(b), new sf(b.right - b.left, b.bottom - b.top))
    }

    function Mg(b, c) {
        b.style.display = c ? "" : "none"
    }

    function Ng(b, c, d, e) {
        if (/^\d+px?$/.test(c)) return parseInt(c, 10);
        var f = b.style[d], g = b.runtimeStyle[d];
        b.runtimeStyle[d] = b.currentStyle[d];
        b.style[d] = c;
        c = b.style[e];
        b.style[d] = f;
        b.runtimeStyle[d] = g;
        return c
    }

    function Og(b, c) {
        var d = b.currentStyle ? b.currentStyle[c] : null;
        return d ? Ng(b, d, "left", "pixelLeft") : 0
    }

    function Pg(b, c) {
        if (Fb) {
            var d = Og(b, c + "Left"), e = Og(b, c + "Right"), f = Og(b, c + "Top"), g = Og(b, c + "Bottom");
            return new Bg(f, e, g, d)
        }
        d = Eg(b, c + "Left");
        e = Eg(b, c + "Right");
        f = Eg(b, c + "Top");
        g = Eg(b, c + "Bottom");
        return new Bg(parseFloat(f), parseFloat(e), parseFloat(g), parseFloat(d))
    }

    var Qg = {thin: 2, medium: 4, thick: 6};

    function Rg(b, c) {
        if ("none" == (b.currentStyle ? b.currentStyle[c + "Style"] : null)) return 0;
        var d = b.currentStyle ? b.currentStyle[c + "Width"] : null;
        return d in Qg ? Qg[d] : Ng(b, d, "left", "pixelLeft")
    }

    function Sg(b) {
        if (Fb && !(Fb && 9 <= Rb)) {
            var c = Rg(b, "borderLeft"), d = Rg(b, "borderRight"), e = Rg(b, "borderTop");
            b = Rg(b, "borderBottom");
            return new Bg(e, d, b, c)
        }
        c = Eg(b, "borderLeftWidth");
        d = Eg(b, "borderRightWidth");
        e = Eg(b, "borderTopWidth");
        b = Eg(b, "borderBottomWidth");
        return new Bg(parseFloat(e), parseFloat(d), parseFloat(b), parseFloat(c))
    };

    function Tg(b, c, d) {
        qc.call(this, b);
        this.map = c;
        this.frameState = m(d) ? d : null
    }

    v(Tg, qc);

    function Ug(b) {
        qd.call(this);
        this.element = m(b.element) ? b.element : null;
        this.a = this.i = null;
        this.q = [];
        this.render = m(b.render) ? b.render : ca;
        m(b.target) && this.b(b.target)
    }

    v(Ug, qd);
    Ug.prototype.P = function () {
        Hf(this.element);
        Ug.T.P.call(this)
    };
    Ug.prototype.f = function () {
        return this.a
    };
    Ug.prototype.setMap = function (b) {
        null === this.a || Hf(this.element);
        0 != this.q.length && (Pa(this.q, Wc), this.q.length = 0);
        this.a = b;
        null !== this.a && ((null === this.i ? b.H : this.i).appendChild(this.element), this.render !== ca && this.q.push(w(b, "postrender", this.render, !1, this)), b.render())
    };
    Ug.prototype.b = function (b) {
        this.i = wf(b)
    };

    function Vg(b) {
        b = m(b) ? b : {};
        this.r = Df("UL");
        this.o = Df("LI");
        this.r.appendChild(this.o);
        Mg(this.o, !1);
        this.c = m(b.collapsed) ? b.collapsed : !0;
        this.g = m(b.collapsible) ? b.collapsible : !0;
        this.g || (this.c = !1);
        var c = m(b.className) ? b.className : "ol-attribution", d = m(b.tipLabel) ? b.tipLabel : "Attributions",
            e = m(b.collapseLabel) ? b.collapseLabel : "\u00bb";
        this.D = ia(e) ? Af("SPAN", {}, e) : e;
        e = m(b.label) ? b.label : "i";
        this.H = ia(e) ? Af("SPAN", {}, e) : e;
        d = Af("BUTTON", {type: "button", title: d}, this.g && !this.c ? this.D : this.H);
        w(d, "click",
            this.Bj, !1, this);
        w(d, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        c = Af("DIV", c + " ol-unselectable ol-control" + (this.c && this.g ? " ol-collapsed" : "") + (this.g ? "" : " ol-uncollapsible"), this.r, d);
        Ug.call(this, {element: c, render: m(b.render) ? b.render : Wg, target: b.target});
        this.p = !0;
        this.j = {};
        this.e = {};
        this.N = {}
    }

    v(Vg, Ug);

    function Wg(b) {
        b = b.frameState;
        if (null === b) this.p && (Mg(this.element, !1), this.p = !1); else {
            var c, d, e, f, g, h, k, n, p, q = b.layerStatesArray, r = Ab(b.attributions), s = {};
            d = 0;
            for (c = q.length; d < c; d++) if (e = q[d].layer.a(), null !== e && (p = ma(e).toString(), n = e.f, null !== n)) for (e = 0, f = n.length; e < f; e++) if (h = n[e], k = ma(h).toString(), !(k in r)) {
                g = b.usedTiles[p];
                var u;
                if (u = m(g)) a:if (null === h.a) u = !0; else {
                    var z = u = void 0, y = void 0, A = void 0;
                    for (A in g) if (A in h.a) for (y = g[A], u = 0, z = h.a[A].length; u < z; ++u) {
                        var D = h.a[A][u];
                        if (D.a <= y.c && D.c >=
                            y.a && D.b <= y.d && D.d >= y.b) {
                            u = !0;
                            break a
                        }
                    }
                    u = !1
                }
                u ? (k in s && delete s[k], r[k] = h) : s[k] = h
            }
            c = [r, s];
            d = c[0];
            c = c[1];
            for (var x in this.j) x in d ? (this.e[x] || (Mg(this.j[x], !0), this.e[x] = !0), delete d[x]) : x in c ? (this.e[x] && (Mg(this.j[x], !1), delete this.e[x]), delete c[x]) : (Hf(this.j[x]), delete this.j[x], delete this.e[x]);
            for (x in d) p = Df("LI"), p.innerHTML = d[x].d, this.r.appendChild(p), this.j[x] = p, this.e[x] = !0;
            for (x in c) p = Df("LI"), p.innerHTML = c[x].d, Mg(p, !1), this.r.appendChild(p), this.j[x] = p;
            x = !vb(this.e) || !vb(b.logos);
            this.p != x && (Mg(this.element, x), this.p = x);
            x && vb(this.e) ? yg(this.element, "ol-logo-only") : zg(this.element, "ol-logo-only");
            var M;
            b = b.logos;
            x = this.N;
            for (M in x) M in b || (Hf(x[M]), delete x[M]);
            for (var Q in b) Q in x || (M = new Image, M.src = Q, d = b[Q], "" === d ? d = M : (d = Af("A", {href: d}), d.appendChild(M)), this.o.appendChild(d), x[Q] = d);
            Mg(this.o, !vb(b))
        }
    }

    l = Vg.prototype;
    l.Bj = function (b) {
        b.preventDefault();
        Xg(this)
    };

    function Xg(b) {
        Ag(b.element, "ol-collapsed");
        b.c ? If(b.D, b.H) : If(b.H, b.D);
        b.c = !b.c
    }

    l.Aj = function () {
        return this.g
    };
    l.Dj = function (b) {
        this.g !== b && (this.g = b, Ag(this.element, "ol-uncollapsible"), !b && this.c && Xg(this))
    };
    l.Cj = function (b) {
        this.g && this.c !== b && Xg(this)
    };
    l.zj = function () {
        return this.c
    };

    function Yg(b) {
        b = m(b) ? b : {};
        var c = m(b.className) ? b.className : "ol-rotate", d = m(b.label) ? b.label : "\u21e7";
        this.c = null;
        ia(d) ? this.c = Af("SPAN", "ol-compass", d) : (this.c = d, yg(this.c, "ol-compass"));
        d = Af("BUTTON", {
            "class": c + "-reset",
            type: "button",
            title: m(b.tipLabel) ? b.tipLabel : "Reset rotation"
        }, this.c);
        w(d, "click", Yg.prototype.o, !1, this);
        w(d, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        c = Af("DIV", c + " ol-unselectable ol-control", d);
        Ug.call(this, {element: c, render: m(b.render) ? b.render : Zg, target: b.target});
        this.g =
            m(b.duration) ? b.duration : 250;
        this.e = m(b.autoHide) ? b.autoHide : !0;
        this.j = void 0;
        this.e && yg(this.element, "ol-hidden")
    }

    v(Yg, Ug);
    Yg.prototype.o = function (b) {
        b.preventDefault();
        b = this.a;
        var c = b.a();
        if (null !== c) {
            for (var d = c.c(); d < -Math.PI;) d += 2 * Math.PI;
            for (; d > Math.PI;) d -= 2 * Math.PI;
            m(d) && (0 < this.g && b.La(ef({rotation: d, duration: this.g, easing: $e})), c.r(0))
        }
    };

    function Zg(b) {
        b = b.frameState;
        if (null !== b) {
            b = b.viewState.rotation;
            if (b != this.j) {
                var c = "rotate(" + 180 * b / Math.PI + "deg)";
                if (this.e) {
                    var d = this.element;
                    0 === b ? yg(d, "ol-hidden") : zg(d, "ol-hidden")
                }
                this.c.style.msTransform = c;
                this.c.style.webkitTransform = c;
                this.c.style.transform = c
            }
            this.j = b
        }
    };

    function $g(b) {
        b = m(b) ? b : {};
        var c = m(b.className) ? b.className : "ol-zoom", d = m(b.delta) ? b.delta : 1,
            e = m(b.zoomOutLabel) ? b.zoomOutLabel : "\u2212",
            f = m(b.zoomOutTipLabel) ? b.zoomOutTipLabel : "Zoom out", g = Af("BUTTON", {
                "class": c + "-in",
                type: "button",
                title: m(b.zoomInTipLabel) ? b.zoomInTipLabel : "Zoom in"
            }, m(b.zoomInLabel) ? b.zoomInLabel : "+");
        w(g, "click", sa($g.prototype.e, d), !1, this);
        w(g, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        e = Af("BUTTON", {"class": c + "-out", type: "button", title: f}, e);
        w(e, "click", sa($g.prototype.e,
            -d), !1, this);
        w(e, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        c = Af("DIV", c + " ol-unselectable ol-control", g, e);
        Ug.call(this, {element: c, target: b.target});
        this.c = m(b.duration) ? b.duration : 250
    }

    v($g, Ug);
    $g.prototype.e = function (b, c) {
        c.preventDefault();
        var d = this.a, e = d.a();
        if (null !== e) {
            var f = e.a();
            m(f) && (0 < this.c && d.La(ff({
                resolution: f,
                duration: this.c,
                easing: $e
            })), d = e.constrainResolution(f, b), e.f(d))
        }
    };

    function ah(b) {
        b = m(b) ? b : {};
        var c = new lg;
        (m(b.zoom) ? b.zoom : 1) && c.push(new $g(b.zoomOptions));
        (m(b.rotate) ? b.rotate : 1) && c.push(new Yg(b.rotateOptions));
        (m(b.attribution) ? b.attribution : 1) && c.push(new Vg(b.attributionOptions));
        return c
    };var bh = Hb ? "webkitfullscreenchange" : Gb ? "mozfullscreenchange" : Fb ? "MSFullscreenChange" : "fullscreenchange";

    function ch() {
        var b = tf().a, c = b.body;
        return !!(c.webkitRequestFullscreen || c.mozRequestFullScreen && b.mozFullScreenEnabled || c.msRequestFullscreen && b.msFullscreenEnabled || c.requestFullscreen && b.fullscreenEnabled)
    }

    function dh(b) {
        b.webkitRequestFullscreen ? b.webkitRequestFullscreen() : b.mozRequestFullScreen ? b.mozRequestFullScreen() : b.msRequestFullscreen ? b.msRequestFullscreen() : b.requestFullscreen && b.requestFullscreen()
    }

    function eh() {
        var b = tf().a;
        return !!(b.webkitIsFullScreen || b.mozFullScreen || b.msFullscreenElement || b.fullscreenElement)
    };

    function fh(b) {
        b = m(b) ? b : {};
        this.e = m(b.className) ? b.className : "ol-full-screen";
        var c = m(b.label) ? b.label : "\u2194";
        this.c = ia(c) ? document.createTextNode(String(c)) : c;
        c = m(b.labelActive) ? b.labelActive : "\u00d7";
        this.g = ia(c) ? document.createTextNode(String(c)) : c;
        c = m(b.tipLabel) ? b.tipLabel : "Toggle full-screen";
        c = Af("BUTTON", {"class": this.e + "-" + eh(), type: "button", title: c}, this.c);
        w(c, "click", this.p, !1, this);
        w(c, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        w(ba.document, bh, this.j, !1, this);
        var d = this.e + " ol-unselectable ol-control " +
            (ch() ? "" : "ol-unsupported"), c = Af("DIV", d, c);
        Ug.call(this, {element: c, target: b.target});
        this.o = m(b.keys) ? b.keys : !1
    }

    v(fh, Ug);
    fh.prototype.p = function (b) {
        b.preventDefault();
        ch() && (b = this.a, null !== b && (eh() ? (b = tf().a, b.webkitCancelFullScreen ? b.webkitCancelFullScreen() : b.mozCancelFullScreen ? b.mozCancelFullScreen() : b.msExitFullscreen ? b.msExitFullscreen() : b.exitFullscreen && b.exitFullscreen()) : (b = b.rc(), b = wf(b), this.o ? b.mozRequestFullScreenWithKeys ? b.mozRequestFullScreenWithKeys() : b.webkitRequestFullscreen ? b.webkitRequestFullscreen() : dh(b) : dh(b))))
    };
    fh.prototype.j = function () {
        var b = this.a;
        eh() ? If(this.g, this.c) : If(this.c, this.g);
        null === b || b.q()
    };

    function gh(b) {
        b = m(b) ? b : {};
        var c = Af("DIV", m(b.className) ? b.className : "ol-mouse-position");
        Ug.call(this, {element: c, render: m(b.render) ? b.render : hh, target: b.target});
        w(this, ud("projection"), this.S, !1, this);
        m(b.coordinateFormat) && this.D(b.coordinateFormat);
        m(b.projection) && this.r(Ae(b.projection));
        this.W = m(b.undefinedHTML) ? b.undefinedHTML : "";
        this.o = c.innerHTML;
        this.g = this.e = this.c = null
    }

    v(gh, Ug);

    function hh(b) {
        b = b.frameState;
        null === b ? this.c = null : this.c != b.viewState.projection && (this.c = b.viewState.projection, this.e = null);
        ih(this, this.g)
    }

    gh.prototype.S = function () {
        this.e = null
    };
    gh.prototype.j = function () {
        return this.get("coordinateFormat")
    };
    gh.prototype.getCoordinateFormat = gh.prototype.j;
    gh.prototype.p = function () {
        return this.get("projection")
    };
    gh.prototype.getProjection = gh.prototype.p;
    gh.prototype.H = function (b) {
        this.g = this.a.gd(b.a);
        ih(this, this.g)
    };
    gh.prototype.N = function () {
        ih(this, null);
        this.g = null
    };
    gh.prototype.setMap = function (b) {
        gh.T.setMap.call(this, b);
        null !== b && (b = b.b, this.q.push(w(b, "mousemove", this.H, !1, this), w(b, "mouseout", this.N, !1, this)))
    };
    gh.prototype.D = function (b) {
        this.set("coordinateFormat", b)
    };
    gh.prototype.setCoordinateFormat = gh.prototype.D;
    gh.prototype.r = function (b) {
        this.set("projection", b)
    };
    gh.prototype.setProjection = gh.prototype.r;

    function ih(b, c) {
        var d = b.W;
        if (null !== c && null !== b.c) {
            if (null === b.e) {
                var e = b.p();
                b.e = m(e) ? ze(b.c, e) : Te
            }
            e = b.a.ra(c);
            null !== e && (b.e(e, e), d = b.j(), d = m(d) ? d(e) : e.toString())
        }
        m(b.o) && d == b.o || (b.element.innerHTML = d, b.o = d)
    };

    function jh(b, c, d) {
        lc.call(this);
        this.c = b;
        this.b = d;
        this.a = c || window;
        this.d = ra(this.kf, this)
    }

    v(jh, lc);
    l = jh.prototype;
    l.aa = null;
    l.Qe = !1;
    l.start = function () {
        kh(this);
        this.Qe = !1;
        var b = lh(this), c = mh(this);
        b && !c && this.a.mozRequestAnimationFrame ? (this.aa = w(this.a, "MozBeforePaint", this.d), this.a.mozRequestAnimationFrame(null), this.Qe = !0) : this.aa = b && c ? b.call(this.a, this.d) : this.a.setTimeout(ed(this.d), 20)
    };

    function kh(b) {
        if (null != b.aa) {
            var c = lh(b), d = mh(b);
            c && !d && b.a.mozRequestAnimationFrame ? Wc(b.aa) : c && d ? d.call(b.a, b.aa) : b.a.clearTimeout(b.aa)
        }
        b.aa = null
    }

    l.kf = function () {
        this.Qe && this.aa && Wc(this.aa);
        this.aa = null;
        this.c.call(this.b, ta())
    };
    l.P = function () {
        kh(this);
        jh.T.P.call(this)
    };

    function lh(b) {
        b = b.a;
        return b.requestAnimationFrame || b.webkitRequestAnimationFrame || b.mozRequestAnimationFrame || b.oRequestAnimationFrame || b.msRequestAnimationFrame || null
    }

    function mh(b) {
        b = b.a;
        return b.cancelAnimationFrame || b.cancelRequestAnimationFrame || b.webkitCancelRequestAnimationFrame || b.mozCancelRequestAnimationFrame || b.oCancelRequestAnimationFrame || b.msCancelRequestAnimationFrame || null
    };

    function nh(b) {
        ba.setTimeout(function () {
            throw b;
        }, 0)
    }

    function oh(b, c) {
        var d = b;
        c && (d = ra(b, c));
        d = ph(d);
        !ka(ba.setImmediate) || ba.Window && ba.Window.prototype.setImmediate == ba.setImmediate ? (qh || (qh = rh()), qh(d)) : ba.setImmediate(d)
    }

    var qh;

    function rh() {
        var b = ba.MessageChannel;
        "undefined" === typeof b && "undefined" !== typeof window && window.postMessage && window.addEventListener && (b = function () {
            var b = document.createElement("iframe");
            b.style.display = "none";
            b.src = "";
            document.documentElement.appendChild(b);
            var c = b.contentWindow, b = c.document;
            b.open();
            b.write("");
            b.close();
            var d = "callImmediate" + Math.random(),
                e = "file:" == c.location.protocol ? "*" : c.location.protocol + "//" + c.location.host,
                b = ra(function (b) {
                        if (("*" == e || b.origin == e) && b.data == d) this.port1.onmessage()
                    },
                    this);
            c.addEventListener("message", b, !1);
            this.port1 = {};
            this.port2 = {
                postMessage: function () {
                    c.postMessage(d, e)
                }
            }
        });
        if ("undefined" !== typeof b && !jb("Trident") && !jb("MSIE")) {
            var c = new b, d = {}, e = d;
            c.port1.onmessage = function () {
                if (m(d.next)) {
                    d = d.next;
                    var b = d.ff;
                    d.ff = null;
                    b()
                }
            };
            return function (b) {
                e.next = {ff: b};
                e = e.next;
                c.port2.postMessage(0)
            }
        }
        return "undefined" !== typeof document && "onreadystatechange" in document.createElement("script") ? function (b) {
            var c = document.createElement("script");
            c.onreadystatechange = function () {
                c.onreadystatechange =
                    null;
                c.parentNode.removeChild(c);
                c = null;
                b();
                b = null
            };
            document.documentElement.appendChild(c)
        } : function (b) {
            ba.setTimeout(b, 0)
        }
    }

    var ph = dd;

    function sh(b) {
        if ("function" == typeof b.ob) return b.ob();
        if (ia(b)) return b.split("");
        if (ha(b)) {
            for (var c = [], d = b.length, e = 0; e < d; e++) c.push(b[e]);
            return c
        }
        return qb(b)
    }

    function th(b, c) {
        if ("function" == typeof b.forEach) b.forEach(c, void 0); else if (ha(b) || ia(b)) Pa(b, c, void 0); else {
            var d;
            if ("function" == typeof b.G) d = b.G(); else if ("function" != typeof b.ob) if (ha(b) || ia(b)) {
                d = [];
                for (var e = b.length, f = 0; f < e; f++) d.push(f)
            } else d = rb(b); else d = void 0;
            for (var e = sh(b), f = e.length, g = 0; g < f; g++) c.call(void 0, e[g], d && d[g], b)
        }
    };

    function uh(b, c) {
        this.d = {};
        this.a = [];
        this.b = 0;
        var d = arguments.length;
        if (1 < d) {
            if (d % 2) throw Error("Uneven number of arguments");
            for (var e = 0; e < d; e += 2) this.set(arguments[e], arguments[e + 1])
        } else if (b) {
            b instanceof uh ? (d = b.G(), e = b.ob()) : (d = rb(b), e = qb(b));
            for (var f = 0; f < d.length; f++) this.set(d[f], e[f])
        }
    }

    l = uh.prototype;
    l.Tb = function () {
        return this.b
    };
    l.ob = function () {
        vh(this);
        for (var b = [], c = 0; c < this.a.length; c++) b.push(this.d[this.a[c]]);
        return b
    };
    l.G = function () {
        vh(this);
        return this.a.concat()
    };
    l.la = function () {
        return 0 == this.b
    };
    l.clear = function () {
        this.d = {};
        this.b = this.a.length = 0
    };
    l.remove = function (b) {
        return wh(this.d, b) ? (delete this.d[b], this.b--, this.a.length > 2 * this.b && vh(this), !0) : !1
    };

    function vh(b) {
        if (b.b != b.a.length) {
            for (var c = 0, d = 0; c < b.a.length;) {
                var e = b.a[c];
                wh(b.d, e) && (b.a[d++] = e);
                c++
            }
            b.a.length = d
        }
        if (b.b != b.a.length) {
            for (var f = {}, d = c = 0; c < b.a.length;) e = b.a[c], wh(f, e) || (b.a[d++] = e, f[e] = 1), c++;
            b.a.length = d
        }
    }

    l.get = function (b, c) {
        return wh(this.d, b) ? this.d[b] : c
    };
    l.set = function (b, c) {
        wh(this.d, b) || (this.b++, this.a.push(b));
        this.d[b] = c
    };
    l.forEach = function (b, c) {
        for (var d = this.G(), e = 0; e < d.length; e++) {
            var f = d[e], g = this.get(f);
            b.call(c, g, f, this)
        }
    };
    l.clone = function () {
        return new uh(this)
    };

    function wh(b, c) {
        return Object.prototype.hasOwnProperty.call(b, c)
    };

    function xh() {
        this.a = ta()
    }

    new xh;
    xh.prototype.set = function (b) {
        this.a = b
    };
    xh.prototype.get = function () {
        return this.a
    };

    function yh(b) {
        hd.call(this);
        this.Vc = b || window;
        this.td = w(this.Vc, "resize", this.Ki, !1, this);
        this.ud = zf(this.Vc || window)
    }

    v(yh, hd);
    l = yh.prototype;
    l.td = null;
    l.Vc = null;
    l.ud = null;
    l.P = function () {
        yh.T.P.call(this);
        this.td && (Wc(this.td), this.td = null);
        this.ud = this.Vc = null
    };
    l.Ki = function () {
        var b = zf(this.Vc || window), c = this.ud;
        b == c || b && c && b.width == c.width && b.height == c.height || (this.ud = b, this.dispatchEvent("resize"))
    };

    function zh(b, c, d, e, f) {
        if (!(Fb || Hb && Ob("525"))) return !0;
        if (Ib && f) return Ah(b);
        if (f && !e) return !1;
        ja(c) && (c = Bh(c));
        if (!d && (17 == c || 18 == c || Ib && 91 == c)) return !1;
        if (Hb && e && d) switch (b) {
            case 220:
            case 219:
            case 221:
            case 192:
            case 186:
            case 189:
            case 187:
            case 188:
            case 190:
            case 191:
            case 192:
            case 222:
                return !1
        }
        if (Fb && e && c == b) return !1;
        switch (b) {
            case 13:
                return !0;
            case 27:
                return !Hb
        }
        return Ah(b)
    }

    function Ah(b) {
        if (48 <= b && 57 >= b || 96 <= b && 106 >= b || 65 <= b && 90 >= b || Hb && 0 == b) return !0;
        switch (b) {
            case 32:
            case 63:
            case 107:
            case 109:
            case 110:
            case 111:
            case 186:
            case 59:
            case 189:
            case 187:
            case 61:
            case 188:
            case 190:
            case 191:
            case 192:
            case 222:
            case 219:
            case 220:
            case 221:
                return !0;
            default:
                return !1
        }
    }

    function Bh(b) {
        if (Gb) b = Ch(b); else if (Ib && Hb) a:switch (b) {
            case 93:
                b = 91;
                break a
        }
        return b
    }

    function Ch(b) {
        switch (b) {
            case 61:
                return 187;
            case 59:
                return 186;
            case 173:
                return 189;
            case 224:
                return 91;
            case 0:
                return 224;
            default:
                return b
        }
    };

    function Dh(b, c) {
        hd.call(this);
        b && Eh(this, b, c)
    }

    v(Dh, hd);
    l = Dh.prototype;
    l.ba = null;
    l.zd = null;
    l.ue = null;
    l.Ad = null;
    l.Qa = -1;
    l.Gb = -1;
    l.he = !1;
    var Fh = {
        3: 13,
        12: 144,
        63232: 38,
        63233: 40,
        63234: 37,
        63235: 39,
        63236: 112,
        63237: 113,
        63238: 114,
        63239: 115,
        63240: 116,
        63241: 117,
        63242: 118,
        63243: 119,
        63244: 120,
        63245: 121,
        63246: 122,
        63247: 123,
        63248: 44,
        63272: 46,
        63273: 36,
        63275: 35,
        63276: 33,
        63277: 34,
        63289: 144,
        63302: 45
    }, Gh = {
        Up: 38,
        Down: 40,
        Left: 37,
        Right: 39,
        Enter: 13,
        F1: 112,
        F2: 113,
        F3: 114,
        F4: 115,
        F5: 116,
        F6: 117,
        F7: 118,
        F8: 119,
        F9: 120,
        F10: 121,
        F11: 122,
        F12: 123,
        "U+007F": 46,
        Home: 36,
        End: 35,
        PageUp: 33,
        PageDown: 34,
        Insert: 45
    }, Hh = Fb || Hb && Ob("525"), Ih = Ib && Gb;
    Dh.prototype.a = function (b) {
        Hb && (17 == this.Qa && !b.n || 18 == this.Qa && !b.d || Ib && 91 == this.Qa && !b.j) && (this.Gb = this.Qa = -1);
        -1 == this.Qa && (b.n && 17 != b.f ? this.Qa = 17 : b.d && 18 != b.f ? this.Qa = 18 : b.j && 91 != b.f && (this.Qa = 91));
        Hh && !zh(b.f, this.Qa, b.c, b.n, b.d) ? this.handleEvent(b) : (this.Gb = Bh(b.f), Ih && (this.he = b.d))
    };
    Dh.prototype.d = function (b) {
        this.Gb = this.Qa = -1;
        this.he = b.d
    };
    Dh.prototype.handleEvent = function (b) {
        var c = b.a, d, e, f = c.altKey;
        Fb && "keypress" == b.type ? (d = this.Gb, e = 13 != d && 27 != d ? c.keyCode : 0) : Hb && "keypress" == b.type ? (d = this.Gb, e = 0 <= c.charCode && 63232 > c.charCode && Ah(d) ? c.charCode : 0) : Eb ? (d = this.Gb, e = Ah(d) ? c.keyCode : 0) : (d = c.keyCode || this.Gb, e = c.charCode || 0, Ih && (f = this.he), Ib && 63 == e && 224 == d && (d = 191));
        var g = d = Bh(d), h = c.keyIdentifier;
        d ? 63232 <= d && d in Fh ? g = Fh[d] : 25 == d && b.c && (g = 9) : h && h in Gh && (g = Gh[h]);
        this.Qa = g;
        b = new Jh(g, e, 0, c);
        b.d = f;
        this.dispatchEvent(b)
    };

    function Eh(b, c, d) {
        b.Ad && Kh(b);
        b.ba = c;
        b.zd = w(b.ba, "keypress", b, d);
        b.ue = w(b.ba, "keydown", b.a, d, b);
        b.Ad = w(b.ba, "keyup", b.d, d, b)
    }

    function Kh(b) {
        b.zd && (Wc(b.zd), Wc(b.ue), Wc(b.Ad), b.zd = null, b.ue = null, b.Ad = null);
        b.ba = null;
        b.Qa = -1;
        b.Gb = -1
    }

    Dh.prototype.P = function () {
        Dh.T.P.call(this);
        Kh(this)
    };

    function Jh(b, c, d, e) {
        wc.call(this, e);
        this.type = "key";
        this.f = b;
        this.i = c
    }

    v(Jh, wc);

    function Lh(b, c) {
        hd.call(this);
        var d = this.ba = b;
        (d = la(d) && 1 == d.nodeType ? this.ba : this.ba ? this.ba.body : null) && Fg(d, "direction");
        this.a = w(this.ba, Gb ? "DOMMouseScroll" : "mousewheel", this, c)
    }

    v(Lh, hd);
    Lh.prototype.handleEvent = function (b) {
        var c = 0, d = 0, e = 0;
        b = b.a;
        if ("mousewheel" == b.type) {
            d = 1;
            if (Fb || Hb && (Jb || Ob("532.0"))) d = 40;
            e = Mh(-b.wheelDelta, d);
            m(b.wheelDeltaX) ? (c = Mh(-b.wheelDeltaX, d), d = Mh(-b.wheelDeltaY, d)) : d = e
        } else e = b.detail, 100 < e ? e = 3 : -100 > e && (e = -3), m(b.axis) && b.axis === b.HORIZONTAL_AXIS ? c = e : d = e;
        ja(this.d) && Vb(c, -this.d, this.d);
        ja(this.b) && (d = Vb(d, -this.b, this.b));
        c = new Nh(e, b, 0, d);
        this.dispatchEvent(c)
    };

    function Mh(b, c) {
        return Hb && (Ib || Kb) && 0 != b % c ? b : b / c
    }

    Lh.prototype.P = function () {
        Lh.T.P.call(this);
        Wc(this.a);
        this.a = null
    };

    function Nh(b, c, d, e) {
        wc.call(this, c);
        this.type = "mousewheel";
        this.detail = b;
        this.q = e
    }

    v(Nh, wc);

    function Oh(b, c, d) {
        qc.call(this, b);
        this.a = c;
        b = m(d) ? d : {};
        this.buttons = Ph(b);
        this.pressure = Qh(b, this.buttons);
        this.bubbles = yb(b, "bubbles", !1);
        this.cancelable = yb(b, "cancelable", !1);
        this.view = yb(b, "view", null);
        this.detail = yb(b, "detail", null);
        this.screenX = yb(b, "screenX", 0);
        this.screenY = yb(b, "screenY", 0);
        this.clientX = yb(b, "clientX", 0);
        this.clientY = yb(b, "clientY", 0);
        this.button = yb(b, "button", 0);
        this.relatedTarget = yb(b, "relatedTarget", null);
        this.pointerId = yb(b, "pointerId", 0);
        this.width = yb(b, "width", 0);
        this.height =
            yb(b, "height", 0);
        this.pointerType = yb(b, "pointerType", "");
        this.isPrimary = yb(b, "isPrimary", !1);
        c.preventDefault && (this.preventDefault = function () {
            c.preventDefault()
        })
    }

    v(Oh, qc);

    function Ph(b) {
        if (b.buttons || Rh) b = b.buttons; else switch (b.which) {
            case 1:
                b = 1;
                break;
            case 2:
                b = 4;
                break;
            case 3:
                b = 2;
                break;
            default:
                b = 0
        }
        return b
    }

    function Qh(b, c) {
        var d = 0;
        b.pressure ? d = b.pressure : d = c ? .5 : 0;
        return d
    }

    var Rh = !1;
    try {
        Rh = 1 === (new MouseEvent("click", {buttons: 1})).buttons
    } catch (Sh) {
    }
    ;

    function Th(b, c) {
        this.a = b;
        this.f = c
    };

    function Uh(b) {
        Th.call(this, b, {
            mousedown: this.Ui,
            mousemove: this.Vi,
            mouseup: this.Yi,
            mouseover: this.Xi,
            mouseout: this.Wi
        });
        this.d = b.d;
        this.b = []
    }

    v(Uh, Th);

    function Vh(b, c) {
        for (var d = b.b, e = c.clientX, f = c.clientY, g = 0, h = d.length, k; g < h && (k = d[g]); g++) {
            var n = Math.abs(f - k[1]);
            if (25 >= Math.abs(e - k[0]) && 25 >= n) return !0
        }
        return !1
    }

    function Xh(b) {
        var c = Yh(b, b.a), d = c.preventDefault;
        c.preventDefault = function () {
            b.preventDefault();
            d()
        };
        c.pointerId = 1;
        c.isPrimary = !0;
        c.pointerType = "mouse";
        return c
    }

    l = Uh.prototype;
    l.Ui = function (b) {
        if (!Vh(this, b)) {
            (1).toString() in this.d && this.cancel(b);
            var c = Xh(b);
            this.d[(1).toString()] = b;
            Zh(this.a, $h, c, b)
        }
    };
    l.Vi = function (b) {
        if (!Vh(this, b)) {
            var c = Xh(b);
            Zh(this.a, ai, c, b)
        }
    };
    l.Yi = function (b) {
        if (!Vh(this, b)) {
            var c = this.d[(1).toString()];
            c && c.button === b.button && (c = Xh(b), Zh(this.a, bi, c, b), xb(this.d, (1).toString()))
        }
    };
    l.Xi = function (b) {
        if (!Vh(this, b)) {
            var c = Xh(b);
            ci(this.a, c, b)
        }
    };
    l.Wi = function (b) {
        if (!Vh(this, b)) {
            var c = Xh(b);
            di(this.a, c, b)
        }
    };
    l.cancel = function (b) {
        var c = Xh(b);
        this.a.cancel(c, b);
        xb(this.d, (1).toString())
    };

    function ei(b) {
        Th.call(this, b, {
            MSPointerDown: this.cj,
            MSPointerMove: this.dj,
            MSPointerUp: this.gj,
            MSPointerOut: this.ej,
            MSPointerOver: this.fj,
            MSPointerCancel: this.bj,
            MSGotPointerCapture: this.$i,
            MSLostPointerCapture: this.aj
        });
        this.d = b.d;
        this.b = ["", "unavailable", "touch", "pen", "mouse"]
    }

    v(ei, Th);

    function fi(b, c) {
        var d = c;
        ja(c.a.pointerType) && (d = Yh(c, c.a), d.pointerType = b.b[c.a.pointerType]);
        return d
    }

    l = ei.prototype;
    l.cj = function (b) {
        this.d[b.a.pointerId] = b;
        var c = fi(this, b);
        Zh(this.a, $h, c, b)
    };
    l.dj = function (b) {
        var c = fi(this, b);
        Zh(this.a, ai, c, b)
    };
    l.gj = function (b) {
        var c = fi(this, b);
        Zh(this.a, bi, c, b);
        xb(this.d, b.a.pointerId)
    };
    l.ej = function (b) {
        var c = fi(this, b);
        di(this.a, c, b)
    };
    l.fj = function (b) {
        var c = fi(this, b);
        ci(this.a, c, b)
    };
    l.bj = function (b) {
        var c = fi(this, b);
        this.a.cancel(c, b);
        xb(this.d, b.a.pointerId)
    };
    l.aj = function (b) {
        this.a.dispatchEvent(new Oh("lostpointercapture", b, b.a))
    };
    l.$i = function (b) {
        this.a.dispatchEvent(new Oh("gotpointercapture", b, b.a))
    };

    function gi(b) {
        Th.call(this, b, {
            pointerdown: this.pl,
            pointermove: this.ql,
            pointerup: this.tl,
            pointerout: this.rl,
            pointerover: this.sl,
            pointercancel: this.ol,
            gotpointercapture: this.ii,
            lostpointercapture: this.Ti
        })
    }

    v(gi, Th);
    l = gi.prototype;
    l.pl = function (b) {
        hi(this.a, b)
    };
    l.ql = function (b) {
        hi(this.a, b)
    };
    l.tl = function (b) {
        hi(this.a, b)
    };
    l.rl = function (b) {
        hi(this.a, b)
    };
    l.sl = function (b) {
        hi(this.a, b)
    };
    l.ol = function (b) {
        hi(this.a, b)
    };
    l.Ti = function (b) {
        hi(this.a, b)
    };
    l.ii = function (b) {
        hi(this.a, b)
    };

    function ii(b, c) {
        Th.call(this, b, {touchstart: this.mm, touchmove: this.lm, touchend: this.km, touchcancel: this.jm});
        this.d = b.d;
        this.g = c;
        this.b = void 0;
        this.e = 0;
        this.c = void 0
    }

    v(ii, Th);
    l = ii.prototype;
    l.mg = function () {
        this.e = 0;
        this.c = void 0
    };

    function ji(b, c, d) {
        c = Yh(c, d);
        c.pointerId = d.identifier + 2;
        c.bubbles = !0;
        c.cancelable = !0;
        c.detail = b.e;
        c.button = 0;
        c.buttons = 1;
        c.width = d.webkitRadiusX || d.radiusX || 0;
        c.height = d.webkitRadiusY || d.radiusY || 0;
        c.pressure = d.webkitForce || d.force || .5;
        c.isPrimary = b.b === d.identifier;
        c.pointerType = "touch";
        c.clientX = d.clientX;
        c.clientY = d.clientY;
        c.screenX = d.screenX;
        c.screenY = d.screenY;
        return c
    }

    function ki(b, c, d) {
        function e() {
            c.preventDefault()
        }

        var f = Array.prototype.slice.call(c.a.changedTouches), g = f.length, h, k;
        for (h = 0; h < g; ++h) k = ji(b, c, f[h]), k.preventDefault = e, d.call(b, c, k)
    }

    l.mm = function (b) {
        var c = b.a.touches, d = rb(this.d), e = d.length;
        if (e >= c.length) {
            var f = [], g, h, k;
            for (g = 0; g < e; ++g) {
                h = d[g];
                k = this.d[h];
                var n;
                if (!(n = 1 == h)) a:{
                    n = c.length;
                    for (var p = void 0, q = 0; q < n; q++) if (p = c[q], p.identifier === h - 2) {
                        n = !0;
                        break a
                    }
                    n = !1
                }
                n || f.push(k.cc)
            }
            for (g = 0; g < f.length; ++g) this.ie(b, f[g])
        }
        c = pb(this.d);
        if (0 === c || 1 === c && (1).toString() in this.d) this.b = b.a.changedTouches[0].identifier, m(this.c) && ba.clearTimeout(this.c);
        li(this, b);
        this.e++;
        ki(this, b, this.kl)
    };
    l.kl = function (b, c) {
        this.d[c.pointerId] = {target: c.target, cc: c, Yf: c.target};
        var d = this.a;
        c.bubbles = !0;
        Zh(d, mi, c, b);
        d = this.a;
        c.bubbles = !1;
        Zh(d, ni, c, b);
        Zh(this.a, $h, c, b)
    };
    l.lm = function (b) {
        b.preventDefault();
        ki(this, b, this.Zi)
    };
    l.Zi = function (b, c) {
        var d = this.d[c.pointerId];
        if (d) {
            var e = d.cc, f = d.Yf;
            Zh(this.a, ai, c, b);
            e && f !== c.target && (e.relatedTarget = c.target, c.relatedTarget = f, e.target = f, c.target ? (di(this.a, e, b), ci(this.a, c, b)) : (c.target = f, c.relatedTarget = null, this.ie(b, c)));
            d.cc = c;
            d.Yf = c.target
        }
    };
    l.km = function (b) {
        li(this, b);
        ki(this, b, this.nm)
    };
    l.nm = function (b, c) {
        Zh(this.a, bi, c, b);
        this.a.cc(c, b);
        var d = this.a;
        c.bubbles = !1;
        Zh(d, oi, c, b);
        xb(this.d, c.pointerId);
        c.isPrimary && (this.b = void 0, this.c = ba.setTimeout(ra(this.mg, this), 200))
    };
    l.jm = function (b) {
        ki(this, b, this.ie)
    };
    l.ie = function (b, c) {
        this.a.cancel(c, b);
        this.a.cc(c, b);
        var d = this.a;
        c.bubbles = !1;
        Zh(d, oi, c, b);
        xb(this.d, c.pointerId);
        c.isPrimary && (this.b = void 0, this.c = ba.setTimeout(ra(this.mg, this), 200))
    };

    function li(b, c) {
        var d = b.g.b, e = c.a.changedTouches[0];
        if (b.b === e.identifier) {
            var f = [e.clientX, e.clientY];
            d.push(f);
            ba.setTimeout(function () {
                Wa(d, f)
            }, 2500)
        }
    };

    function pi(b) {
        hd.call(this);
        this.ba = b;
        this.d = {};
        this.b = {};
        this.a = [];
        cg ? qi(this, new gi(this)) : dg ? qi(this, new ei(this)) : (b = new Uh(this), qi(this, b), bg && qi(this, new ii(this, b)));
        b = this.a.length;
        for (var c, d = 0; d < b; d++) c = this.a[d], ri(this, rb(c.f))
    }

    v(pi, hd);

    function qi(b, c) {
        var d = rb(c.f);
        d && (Pa(d, function (b) {
            var d = c.f[b];
            d && (this.b[b] = ra(d, c))
        }, b), b.a.push(c))
    }

    pi.prototype.c = function (b) {
        var c = this.b[b.type];
        c && c(b)
    };

    function ri(b, c) {
        Pa(c, function (b) {
            w(this.ba, b, this.c, !1, this)
        }, b)
    }

    function si(b, c) {
        Pa(c, function (b) {
            Vc(this.ba, b, this.c, !1, this)
        }, b)
    }

    function Yh(b, c) {
        for (var d = {}, e, f = 0, g = ti.length; f < g; f++) e = ti[f][0], d[e] = b[e] || c[e] || ti[f][1];
        return d
    }

    pi.prototype.cc = function (b, c) {
        b.bubbles = !0;
        Zh(this, ui, b, c)
    };
    pi.prototype.cancel = function (b, c) {
        Zh(this, vi, b, c)
    };

    function di(b, c, d) {
        b.cc(c, d);
        c.target.contains(c.relatedTarget) || (c.bubbles = !1, Zh(b, oi, c, d))
    }

    function ci(b, c, d) {
        c.bubbles = !0;
        Zh(b, mi, c, d);
        c.target.contains(c.relatedTarget) || (c.bubbles = !1, Zh(b, ni, c, d))
    }

    function Zh(b, c, d, e) {
        b.dispatchEvent(new Oh(c, e, d))
    }

    function hi(b, c) {
        b.dispatchEvent(new Oh(c.type, c, c.a))
    }

    pi.prototype.P = function () {
        for (var b = this.a.length, c, d = 0; d < b; d++) c = this.a[d], si(this, rb(c.f));
        pi.T.P.call(this)
    };
    var ai = "pointermove", $h = "pointerdown", bi = "pointerup", mi = "pointerover", ui = "pointerout",
        ni = "pointerenter", oi = "pointerleave", vi = "pointercancel",
        ti = [["bubbles", !1], ["cancelable", !1], ["view", null], ["detail", null], ["screenX", 0], ["screenY", 0], ["clientX", 0], ["clientY", 0], ["ctrlKey", !1], ["altKey", !1], ["shiftKey", !1], ["metaKey", !1], ["button", 0], ["relatedTarget", null], ["buttons", 0], ["pointerId", 0], ["width", 0], ["height", 0], ["pressure", 0], ["tiltX", 0], ["tiltY", 0], ["pointerType", ""], ["hwTimestamp", 0], ["isPrimary",
            !1], ["type", ""], ["target", null], ["currentTarget", null], ["which", 0]];

    function wi(b, c, d, e, f) {
        Tg.call(this, b, c, f);
        this.a = d;
        this.originalEvent = d.a;
        this.pixel = c.gd(this.originalEvent);
        this.coordinate = c.ra(this.pixel);
        this.dragging = m(e) ? e : !1
    }

    v(wi, Tg);
    wi.prototype.preventDefault = function () {
        wi.T.preventDefault.call(this);
        this.a.preventDefault()
    };
    wi.prototype.pb = function () {
        wi.T.pb.call(this);
        this.a.pb()
    };

    function xi(b, c, d, e, f) {
        wi.call(this, b, c, d.a, e, f);
        this.d = d
    }

    v(xi, wi);

    function yi(b) {
        hd.call(this);
        this.b = b;
        this.e = 0;
        this.g = !1;
        this.d = this.n = this.c = null;
        b = this.b.b;
        this.q = 0;
        this.j = {};
        this.f = new pi(b);
        this.a = null;
        this.n = w(this.f, $h, this.Gi, !1, this);
        this.i = w(this.f, ai, this.Kl, !1, this)
    }

    v(yi, hd);

    function zi(b, c) {
        var d;
        d = new xi(Ai, b.b, c);
        b.dispatchEvent(d);
        0 !== b.e ? (ba.clearTimeout(b.e), b.e = 0, d = new xi(Bi, b.b, c), b.dispatchEvent(d)) : b.e = ba.setTimeout(ra(function () {
            this.e = 0;
            var b = new xi(Ci, this.b, c);
            this.dispatchEvent(b)
        }, b), 250)
    }

    function Di(b, c) {
        c.type == Ei || c.type == Fi ? delete b.j[c.pointerId] : c.type == Gi && (b.j[c.pointerId] = !0);
        b.q = pb(b.j)
    }

    l = yi.prototype;
    l.vf = function (b) {
        Di(this, b);
        var c = new xi(Ei, this.b, b);
        this.dispatchEvent(c);
        !this.g && 0 === b.button && zi(this, this.d);
        0 === this.q && (Pa(this.c, Wc), this.c = null, this.g = !1, this.d = null, pc(this.a), this.a = null)
    };
    l.Gi = function (b) {
        Di(this, b);
        var c = new xi(Gi, this.b, b);
        this.dispatchEvent(c);
        this.d = b;
        null === this.c && (this.a = new pi(document), this.c = [w(this.a, Hi, this.vj, !1, this), w(this.a, Ei, this.vf, !1, this), w(this.f, Fi, this.vf, !1, this)])
    };
    l.vj = function (b) {
        if (b.clientX != this.d.clientX || b.clientY != this.d.clientY) {
            this.g = !0;
            var c = new xi(Ii, this.b, b, this.g);
            this.dispatchEvent(c)
        }
        b.preventDefault()
    };
    l.Kl = function (b) {
        this.dispatchEvent(new xi(b.type, this.b, b, null !== this.d && (b.clientX != this.d.clientX || b.clientY != this.d.clientY)))
    };
    l.P = function () {
        null !== this.i && (Wc(this.i), this.i = null);
        null !== this.n && (Wc(this.n), this.n = null);
        null !== this.c && (Pa(this.c, Wc), this.c = null);
        null !== this.a && (pc(this.a), this.a = null);
        null !== this.f && (pc(this.f), this.f = null);
        yi.T.P.call(this)
    };
    var Ci = "singleclick", Ai = "click", Bi = "dblclick", Ii = "pointerdrag", Hi = "pointermove", Gi = "pointerdown",
        Ei = "pointerup", Fi = "pointercancel", Ji = {
            Lm: Ci,
            Am: Ai,
            Bm: Bi,
            Em: Ii,
            Hm: Hi,
            Dm: Gi,
            Km: Ei,
            Jm: "pointerover",
            Im: "pointerout",
            Fm: "pointerenter",
            Gm: "pointerleave",
            Cm: Fi
        };

    function Ki(b) {
        qd.call(this);
        this.g = Ae(b.projection);
        this.f = m(b.attributions) ? b.attributions : null;
        this.D = b.logo;
        this.q = m(b.state) ? b.state : "ready"
    }

    v(Ki, qd);
    l = Ki.prototype;
    l.Hd = ca;
    l.Y = function () {
        return this.f
    };
    l.X = function () {
        return this.D
    };
    l.Z = function () {
        return this.g
    };
    l.$ = function () {
        return this.q
    };

    function Li(b, c) {
        b.q = c;
        b.l()
    };

    function C(b) {
        qd.call(this);
        var c = Ab(b);
        c.brightness = m(b.brightness) ? b.brightness : 0;
        c.contrast = m(b.contrast) ? b.contrast : 1;
        c.hue = m(b.hue) ? b.hue : 0;
        c.opacity = m(b.opacity) ? b.opacity : 1;
        c.saturation = m(b.saturation) ? b.saturation : 1;
        c.visible = m(b.visible) ? b.visible : !0;
        c.maxResolution = m(b.maxResolution) ? b.maxResolution : Infinity;
        c.minResolution = m(b.minResolution) ? b.minResolution : 0;
        this.C(c)
    }

    v(C, qd);
    C.prototype.c = function () {
        return this.get("brightness")
    };
    C.prototype.getBrightness = C.prototype.c;
    C.prototype.f = function () {
        return this.get("contrast")
    };
    C.prototype.getContrast = C.prototype.f;
    C.prototype.e = function () {
        return this.get("hue")
    };
    C.prototype.getHue = C.prototype.e;

    function Mi(b) {
        var c = b.c(), d = b.f(), e = b.e(), f = b.q(), g = b.j(), h = b.kb(), k = b.b(), n = b.J(), p = b.g(),
            q = b.i();
        return {
            layer: b,
            brightness: Vb(c, -1, 1),
            contrast: Math.max(d, 0),
            hue: e,
            opacity: Vb(f, 0, 1),
            saturation: Math.max(g, 0),
            yc: h,
            visible: k,
            extent: n,
            maxResolution: p,
            minResolution: Math.max(q, 0)
        }
    }

    C.prototype.J = function () {
        return this.get("extent")
    };
    C.prototype.getExtent = C.prototype.J;
    C.prototype.g = function () {
        return this.get("maxResolution")
    };
    C.prototype.getMaxResolution = C.prototype.g;
    C.prototype.i = function () {
        return this.get("minResolution")
    };
    C.prototype.getMinResolution = C.prototype.i;
    C.prototype.q = function () {
        return this.get("opacity")
    };
    C.prototype.getOpacity = C.prototype.q;
    C.prototype.j = function () {
        return this.get("saturation")
    };
    C.prototype.getSaturation = C.prototype.j;
    C.prototype.b = function () {
        return this.get("visible")
    };
    C.prototype.getVisible = C.prototype.b;
    C.prototype.D = function (b) {
        this.set("brightness", b)
    };
    C.prototype.setBrightness = C.prototype.D;
    C.prototype.H = function (b) {
        this.set("contrast", b)
    };
    C.prototype.setContrast = C.prototype.H;
    C.prototype.N = function (b) {
        this.set("hue", b)
    };
    C.prototype.setHue = C.prototype.N;
    C.prototype.o = function (b) {
        this.set("extent", b)
    };
    C.prototype.setExtent = C.prototype.o;
    C.prototype.S = function (b) {
        this.set("maxResolution", b)
    };
    C.prototype.setMaxResolution = C.prototype.S;
    C.prototype.W = function (b) {
        this.set("minResolution", b)
    };
    C.prototype.setMinResolution = C.prototype.W;
    C.prototype.p = function (b) {
        this.set("opacity", b)
    };
    C.prototype.setOpacity = C.prototype.p;
    C.prototype.ca = function (b) {
        this.set("saturation", b)
    };
    C.prototype.setSaturation = C.prototype.ca;
    C.prototype.da = function (b) {
        this.set("visible", b)
    };
    C.prototype.setVisible = C.prototype.da;

    function E(b) {
        var c = Ab(b);
        delete c.source;
        C.call(this, c);
        this.va = null;
        w(this, ud("source"), this.wh, !1, this);
        this.fa(m(b.source) ? b.source : null)
    }

    v(E, C);

    function Ni(b, c) {
        return b.visible && c >= b.minResolution && c < b.maxResolution
    }

    E.prototype.Xa = function (b) {
        b = m(b) ? b : [];
        b.push(Mi(this));
        return b
    };
    E.prototype.a = function () {
        var b = this.get("source");
        return m(b) ? b : null
    };
    E.prototype.getSource = E.prototype.a;
    E.prototype.kb = function () {
        var b = this.a();
        return null === b ? "undefined" : b.q
    };
    E.prototype.ji = function () {
        this.l()
    };
    E.prototype.wh = function () {
        null !== this.va && (Wc(this.va), this.va = null);
        var b = this.a();
        null !== b && (this.va = w(b, "change", this.ji, !1, this));
        this.l()
    };
    E.prototype.fa = function (b) {
        this.set("source", b)
    };
    E.prototype.setSource = E.prototype.fa;

    function Oi(b, c, d, e, f) {
        hd.call(this);
        this.e = f;
        this.extent = b;
        this.f = d;
        this.resolution = c;
        this.state = e
    }

    v(Oi, hd);
    Oi.prototype.J = function () {
        return this.extent
    };

    function Pi(b, c) {
        hd.call(this);
        this.a = b;
        this.state = c
    }

    v(Pi, hd);

    function Qi(b) {
        b.dispatchEvent("change")
    }

    Pi.prototype.qb = function () {
        return ma(this).toString()
    };
    Pi.prototype.e = function () {
        return this.a
    };

    function Ri() {
        this.b = 0;
        this.c = {};
        this.d = this.a = null
    }

    l = Ri.prototype;
    l.clear = function () {
        this.b = 0;
        this.c = {};
        this.d = this.a = null
    };

    function Si(b, c) {
        return b.c.hasOwnProperty(c)
    }

    l.forEach = function (b, c) {
        for (var d = this.a; null !== d;) b.call(c, d.gc, d.Bd, this), d = d.eb
    };
    l.get = function (b) {
        b = this.c[b];
        if (b === this.d) return b.gc;
        b === this.a ? (this.a = this.a.eb, this.a.Mb = null) : (b.eb.Mb = b.Mb, b.Mb.eb = b.eb);
        b.eb = null;
        b.Mb = this.d;
        this.d = this.d.eb = b;
        return b.gc
    };
    l.Tb = function () {
        return this.b
    };
    l.G = function () {
        var b = Array(this.b), c = 0, d;
        for (d = this.d; null !== d; d = d.Mb) b[c++] = d.Bd;
        return b
    };
    l.ob = function () {
        var b = Array(this.b), c = 0, d;
        for (d = this.d; null !== d; d = d.Mb) b[c++] = d.gc;
        return b
    };
    l.pop = function () {
        var b = this.a;
        delete this.c[b.Bd];
        null !== b.eb && (b.eb.Mb = null);
        this.a = b.eb;
        null === this.a && (this.d = null);
        --this.b;
        return b.gc
    };
    l.set = function (b, c) {
        var d = {Bd: b, eb: null, Mb: this.d, gc: c};
        null === this.d ? this.a = d : this.d.eb = d;
        this.d = d;
        this.c[b] = d;
        ++this.b
    };

    function Ti(b) {
        Ri.call(this);
        this.f = m(b) ? b : 2048
    }

    v(Ti, Ri);

    function Ui(b) {
        return b.Tb() > b.f
    };

    function Vi(b) {
        this.minZoom = m(b.minZoom) ? b.minZoom : 0;
        this.a = b.resolutions;
        this.maxZoom = this.a.length - 1;
        this.c = m(b.origin) ? b.origin : null;
        this.e = null;
        m(b.origins) && (this.e = b.origins);
        this.d = null;
        m(b.tileSizes) && (this.d = b.tileSizes);
        this.f = m(b.tileSize) ? b.tileSize : null === this.d ? 256 : void 0
    }

    var Wi = [0, 0, 0];
    l = Vi.prototype;
    l.Db = function () {
        return dd
    };
    l.fd = function (b, c, d, e, f) {
        f = Xi(this, b, f);
        for (b = b[0] - 1; b >= this.minZoom;) {
            if (c.call(d, b, Yi(this, f, b, e))) return !0;
            --b
        }
        return !1
    };
    l.ld = function () {
        return this.maxZoom
    };
    l.od = function () {
        return this.minZoom
    };
    l.Lb = function (b) {
        return null === this.c ? this.e[b] : this.c
    };
    l.na = function (b) {
        return this.a[b]
    };
    l.Od = function () {
        return this.a
    };
    l.sd = function (b, c, d) {
        return b[0] < this.maxZoom ? (d = Xi(this, b, d), Yi(this, d, b[0] + 1, c)) : null
    };

    function Zi(b, c, d, e) {
        $i(b, c[0], c[1], d, !1, Wi);
        var f = Wi[1], g = Wi[2];
        $i(b, c[2], c[3], d, !0, Wi);
        return mf(f, Wi[1], g, Wi[2], e)
    }

    function Yi(b, c, d, e) {
        return Zi(b, c, b.na(d), e)
    }

    function aj(b, c) {
        var d = b.Lb(c[0]), e = b.na(c[0]), f = b.ua(c[0]);
        return [d[0] + (c[1] + .5) * f * e, d[1] + (c[2] + .5) * f * e]
    }

    function Xi(b, c, d) {
        var e = b.Lb(c[0]), f = b.na(c[0]);
        b = b.ua(c[0]);
        var g = e[0] + c[1] * b * f;
        c = e[1] + c[2] * b * f;
        return Vd(g, c, g + b * f, c + b * f, d)
    }

    l.Wb = function (b, c, d) {
        return $i(this, b[0], b[1], c, !1, d)
    };

    function $i(b, c, d, e, f, g) {
        var h = ac(b.a, e, 0), k = e / b.na(h), n = b.Lb(h);
        b = b.ua(h);
        c = k * (c - n[0]) / (e * b);
        d = k * (d - n[1]) / (e * b);
        f ? (c = Math.ceil(c) - 1, d = Math.ceil(d) - 1) : (c = Math.floor(c), d = Math.floor(d));
        return gf(h, c, d, g)
    }

    l.Mc = function (b, c, d) {
        return $i(this, b[0], b[1], this.na(c), !1, d)
    };
    l.ua = function (b) {
        return m(this.f) ? this.f : this.d[b]
    };

    function bj(b, c, d) {
        c = m(c) ? c : 42;
        d = m(d) ? d : 256;
        b = Math.max(qe(b) / d, ne(b) / d);
        c += 1;
        d = Array(c);
        for (var e = 0; e < c; ++e) d[e] = b / Math.pow(2, e);
        return d
    }

    function cj(b) {
        b = Ae(b);
        var c = b.J();
        null === c && (b = 180 * we.degrees / b.md(), c = Vd(-b, -b, b, b));
        return c
    };

    function dj(b) {
        Ki.call(this, {
            attributions: b.attributions,
            extent: b.extent,
            logo: b.logo,
            projection: b.projection,
            state: b.state
        });
        this.H = m(b.opaque) ? b.opaque : !1;
        this.N = m(b.tilePixelRatio) ? b.tilePixelRatio : 1;
        this.tileGrid = m(b.tileGrid) ? b.tileGrid : null;
        this.a = new Ti
    }

    v(dj, Ki);

    function ej(b, c, d, e) {
        for (var f = !0, g, h, k = d.a; k <= d.c; ++k) for (var n = d.b; n <= d.d; ++n) g = b.nb(c, k, n), h = !1, Si(b.a, g) && (g = b.a.get(g), (h = 2 === g.state) && (h = !1 !== e(g))), h || (f = !1);
        return f
    }

    l = dj.prototype;
    l.hd = function () {
        return 0
    };
    l.nb = hf;
    l.xa = function () {
        return this.tileGrid
    };

    function fj(b, c) {
        var d;
        if (null === b.tileGrid) {
            if (d = c.f, null === d) {
                d = cj(c);
                var e = m(void 0) ? void 0 : 256, f = m(void 0) ? void 0 : "bottom-left", g = bj(d, void 0, e);
                d = new Vi({origin: le(d, f), resolutions: g, tileSize: e});
                c.f = d
            }
        } else d = b.tileGrid;
        return d
    }

    l.Nc = function (b, c, d) {
        return fj(this, d).ua(b) * this.N
    };
    l.Pe = ca;

    function gj(b, c) {
        qc.call(this, b);
        this.tile = c
    }

    v(gj, qc);

    function hj(b, c, d, e, f, g, h, k) {
        Ld(b);
        0 === c && 0 === d || Od(b, c, d);
        1 == e && 1 == f || Pd(b, e, f);
        0 !== g && Qd(b, g);
        0 === h && 0 === k || Od(b, h, k);
        return b
    }

    function ij(b, c) {
        return b[0] == c[0] && b[1] == c[1] && b[4] == c[4] && b[5] == c[5] && b[12] == c[12] && b[13] == c[13]
    }

    function kj(b, c, d) {
        var e = b[1], f = b[5], g = b[13], h = c[0];
        c = c[1];
        d[0] = b[0] * h + b[4] * c + b[12];
        d[1] = e * h + f * c + g;
        return d
    };

    function lj(b) {
        kd.call(this);
        this.a = b
    }

    v(lj, kd);
    l = lj.prototype;
    l.Ua = ca;
    l.bc = function (b, c, d, e) {
        b = b.slice();
        kj(c.pixelToCoordinateMatrix, b, b);
        if (this.Ua(b, c, bd, this)) return d.call(e, this.a)
    };
    l.Fd = ad;
    l.dd = function (b, c) {
        return function (d, e) {
            return ej(b, d, e, function (b) {
                c[d] || (c[d] = {});
                c[d][b.a.toString()] = b
            })
        }
    };
    l.Tj = function (b) {
        2 === b.target.state && mj(this)
    };

    function nj(b, c) {
        var d = c.state;
        2 != d && 3 != d && Uc(c, "change", b.Tj, !1, b);
        0 == d && (c.load(), d = c.state);
        return 2 == d
    }

    function mj(b) {
        var c = b.a;
        c.b() && "ready" == c.kb() && b.l()
    }

    function oj(b, c) {
        Ui(c.a) && b.postRenderFunctions.push(sa(function (b, c, f) {
            c = ma(b).toString();
            b = b.a;
            f = f.usedTiles[c];
            for (var g; Ui(b) && !(c = b.a.gc, g = c.a[0].toString(), g in f && f[g].contains(c.a));) b.pop().Jc()
        }, c))
    }

    function pj(b, c) {
        if (null != c) {
            var d, e, f;
            e = 0;
            for (f = c.length; e < f; ++e) d = c[e], b[ma(d).toString()] = d
        }
    }

    function qj(b, c) {
        var d = c.D;
        m(d) && (ia(d) ? b.logos[d] = "" : la(d) && (b.logos[d.src] = d.href))
    }

    function rj(b, c, d, e) {
        c = ma(c).toString();
        d = d.toString();
        c in b ? d in b[c] ? (b = b[c][d], e.a < b.a && (b.a = e.a), e.c > b.c && (b.c = e.c), e.b < b.b && (b.b = e.b), e.d > b.d && (b.d = e.d)) : b[c][d] = e : (b[c] = {}, b[c][d] = e)
    }

    function sj(b, c, d) {
        return [c * (Math.round(b[0] / c) + d[0] % 2 / 2), c * (Math.round(b[1] / c) + d[1] % 2 / 2)]
    }

    function tj(b, c, d, e, f, g, h, k, n, p) {
        var q = ma(c).toString();
        q in b.wantedTiles || (b.wantedTiles[q] = {});
        var r = b.wantedTiles[q];
        b = b.tileQueue;
        var s = d.minZoom, u, z, y, A, D, x;
        for (x = h; x >= s; --x) for (z = Yi(d, g, x, z), y = d.na(x), A = z.a; A <= z.c; ++A) for (D = z.b; D <= z.d; ++D) h - x <= k ? (u = c.Vb(x, A, D, e, f), 0 == u.state && (r[kf(u.a)] = !0, u.qb() in b.b || uj(b, [u, q, aj(d, u.a), y])), m(n) && n.call(p, u)) : c.Pe(x, A, D)
    };

    function vj(b) {
        this.o = b.opacity;
        this.p = b.rotateWithView;
        this.i = b.rotation;
        this.j = b.scale;
        this.r = b.snapToPixel
    }

    l = vj.prototype;
    l.Jd = function () {
        return this.o
    };
    l.qd = function () {
        return this.p
    };
    l.Kd = function () {
        return this.i
    };
    l.Ld = function () {
        return this.j
    };
    l.rd = function () {
        return this.r
    };
    l.Md = function (b) {
        this.i = b
    };
    l.Nd = function (b) {
        this.j = b
    };

    function wj(b) {
        b = m(b) ? b : {};
        this.f = m(b.anchor) ? b.anchor : [.5, .5];
        this.c = null;
        this.d = m(b.anchorOrigin) ? b.anchorOrigin : "top-left";
        this.g = m(b.anchorXUnits) ? b.anchorXUnits : "fraction";
        this.n = m(b.anchorYUnits) ? b.anchorYUnits : "fraction";
        var c = m(b.crossOrigin) ? b.crossOrigin : null, d = m(b.img) ? b.img : null, e = b.src;
        m(e) && 0 !== e.length || null === d || (e = d.src);
        var f = m(b.src) ? 0 : 2, g = xj.Pa(), h = g.get(e, c);
        null === h && (h = new yj(d, e, c, f), g.set(e, c, h));
        this.a = h;
        this.D = m(b.offset) ? b.offset : [0, 0];
        this.b = m(b.offsetOrigin) ? b.offsetOrigin :
            "top-left";
        this.e = null;
        this.q = m(b.size) ? b.size : null;
        vj.call(this, {
            opacity: m(b.opacity) ? b.opacity : 1,
            rotation: m(b.rotation) ? b.rotation : 0,
            scale: m(b.scale) ? b.scale : 1,
            snapToPixel: m(b.snapToPixel) ? b.snapToPixel : !0,
            rotateWithView: m(b.rotateWithView) ? b.rotateWithView : !1
        })
    }

    v(wj, vj);
    l = wj.prototype;
    l.wb = function () {
        if (null !== this.c) return this.c;
        var b = this.f, c = this.gb();
        if ("fraction" == this.g || "fraction" == this.n) {
            if (null === c) return null;
            b = this.f.slice();
            "fraction" == this.g && (b[0] *= c[0]);
            "fraction" == this.n && (b[1] *= c[1])
        }
        if ("top-left" != this.d) {
            if (null === c) return null;
            b === this.f && (b = this.f.slice());
            if ("top-right" == this.d || "bottom-right" == this.d) b[0] = -b[0] + c[0];
            if ("bottom-left" == this.d || "bottom-right" == this.d) b[1] = -b[1] + c[1]
        }
        return this.c = b
    };
    l.Bb = function () {
        return this.a.a
    };
    l.jd = function () {
        return this.a.d
    };
    l.Oc = function () {
        return this.a.b
    };
    l.Id = function () {
        var b = this.a;
        if (null === b.f) if (b.n) {
            var c = b.d[0], d = b.d[1], e = Nf(c, d);
            e.fillRect(0, 0, c, d);
            b.f = e.canvas
        } else b.f = b.a;
        return b.f
    };
    l.Cb = function () {
        if (null !== this.e) return this.e;
        var b = this.D;
        if ("top-left" != this.b) {
            var c = this.gb(), d = this.a.d;
            if (null === c || null === d) return null;
            b = b.slice();
            if ("top-right" == this.b || "bottom-right" == this.b) b[0] = d[0] - c[0] - b[0];
            if ("bottom-left" == this.b || "bottom-right" == this.b) b[1] = d[1] - c[1] - b[1]
        }
        return this.e = b
    };
    l.Fk = function () {
        return this.a.e
    };
    l.gb = function () {
        return null === this.q ? this.a.d : this.q
    };
    l.we = function (b, c) {
        return w(this.a, "change", b, !1, c)
    };
    l.load = function () {
        this.a.load()
    };
    l.Oe = function (b, c) {
        Vc(this.a, "change", b, !1, c)
    };

    function yj(b, c, d, e) {
        hd.call(this);
        this.f = null;
        this.a = null === b ? new Image : b;
        null !== d && (this.a.crossOrigin = d);
        this.c = null;
        this.b = e;
        this.d = null;
        this.e = c;
        this.n = !1
    }

    v(yj, hd);
    yj.prototype.g = function () {
        this.b = 3;
        Pa(this.c, Wc);
        this.c = null;
        this.dispatchEvent("change")
    };
    yj.prototype.i = function () {
        this.b = 2;
        this.d = [this.a.width, this.a.height];
        Pa(this.c, Wc);
        this.c = null;
        var b = Nf(1, 1);
        b.drawImage(this.a, 0, 0);
        try {
            b.getImageData(0, 0, 1, 1)
        } catch (c) {
            this.n = !0
        }
        this.dispatchEvent("change")
    };
    yj.prototype.load = function () {
        if (0 == this.b) {
            this.b = 1;
            this.c = [Uc(this.a, "error", this.g, !1, this), Uc(this.a, "load", this.i, !1, this)];
            try {
                this.a.src = this.e
            } catch (b) {
                this.g()
            }
        }
    };

    function xj() {
        this.a = {};
        this.d = 0
    }

    da(xj);
    xj.prototype.clear = function () {
        this.a = {};
        this.d = 0
    };
    xj.prototype.get = function (b, c) {
        var d = c + ":" + b;
        return d in this.a ? this.a[d] : null
    };
    xj.prototype.set = function (b, c, d) {
        this.a[c + ":" + b] = d;
        ++this.d
    };

    function zj(b, c) {
        lc.call(this);
        this.g = c;
        this.b = null;
        this.e = {};
        this.q = {}
    }

    v(zj, lc);

    function Aj(b) {
        var c = b.viewState, d = b.coordinateToPixelMatrix;
        hj(d, b.size[0] / 2, b.size[1] / 2, 1 / c.resolution, -1 / c.resolution, -c.rotation, -c.center[0], -c.center[1]);
        Nd(d, b.pixelToCoordinateMatrix)
    }

    l = zj.prototype;
    l.P = function () {
        kb(this.e, pc);
        zj.T.P.call(this)
    };

    function Bj() {
        var b = xj.Pa();
        if (32 < b.d) {
            var c = 0, d, e;
            for (d in b.a) {
                e = b.a[d];
                var f;
                if (f = 0 === (c++ & 3)) Bc(e) ? e = jd(e, void 0, void 0) : (e = Qc(e), e = !!e && Jc(e, void 0, void 0)), f = !e;
                f && (delete b.a[d], --b.d)
            }
        }
    }

    l.ze = function (b, c, d, e, f, g) {
        var h, k = c.viewState, n = k.resolution, k = k.rotation;
        if (null !== this.b) {
            var p = {};
            if (h = this.b.b(b, n, k, {}, function (b) {
                var c = ma(b).toString();
                if (!(c in p)) return p[c] = !0, d.call(e, b, null)
            })) return h
        }
        var k = c.layerStatesArray, q;
        for (q = k.length - 1; 0 <= q; --q) {
            h = k[q];
            var r = h.layer;
            if (Ni(h, n) && f.call(g, r) && (h = Cj(this, r).Ua(b, c, d, e))) return h
        }
    };
    l.Jf = function (b, c, d, e, f, g) {
        var h, k = c.viewState, n = k.resolution, k = k.rotation;
        if (null !== this.b) {
            var p = this.g.ra(b);
            if (this.b.b(p, n, k, {}, bd) && (h = d.call(e, null))) return h
        }
        k = c.layerStatesArray;
        for (p = k.length - 1; 0 <= p; --p) {
            h = k[p];
            var q = h.layer;
            if (Ni(h, n) && f.call(g, q) && (h = Cj(this, q).bc(b, c, d, e))) return h
        }
    };
    l.Kf = function (b, c, d, e) {
        b = this.ze(b, c, bd, this, d, e);
        return m(b)
    };

    function Cj(b, c) {
        var d = ma(c).toString();
        if (d in b.e) return b.e[d];
        var e = b.le(c);
        b.e[d] = e;
        b.q[d] = w(e, "change", b.yi, !1, b);
        return e
    }

    l.yi = function () {
        this.g.render()
    };
    l.Wd = ca;
    l.Pl = function (b, c) {
        for (var d in this.e) if (!(null !== c && d in c.layerStates)) {
            var e = d, f = this.e[e];
            delete this.e[e];
            Wc(this.q[e]);
            delete this.q[e];
            pc(f)
        }
    };

    function Dj(b, c) {
        for (var d in b.e) if (!(d in c.layerStates)) {
            c.postRenderFunctions.push(ra(b.Pl, b));
            break
        }
    };

    function Ej(b, c) {
        this.e = b;
        this.f = c;
        this.a = [];
        this.d = [];
        this.b = {}
    }

    Ej.prototype.clear = function () {
        this.a.length = 0;
        this.d.length = 0;
        wb(this.b)
    };

    function Fj(b) {
        var c = b.a, d = b.d, e = c[0];
        1 == c.length ? (c.length = 0, d.length = 0) : (c[0] = c.pop(), d[0] = d.pop(), Gj(b, 0));
        c = b.f(e);
        delete b.b[c];
        return e
    }

    function uj(b, c) {
        var d = b.e(c);
        Infinity != d && (b.a.push(c), b.d.push(d), b.b[b.f(c)] = !0, Hj(b, 0, b.a.length - 1))
    }

    Ej.prototype.Tb = function () {
        return this.a.length
    };
    Ej.prototype.la = function () {
        return 0 === this.a.length
    };

    function Gj(b, c) {
        for (var d = b.a, e = b.d, f = d.length, g = d[c], h = e[c], k = c; c < f >> 1;) {
            var n = 2 * c + 1, p = 2 * c + 2, n = p < f && e[p] < e[n] ? p : n;
            d[c] = d[n];
            e[c] = e[n];
            c = n
        }
        d[c] = g;
        e[c] = h;
        Hj(b, k, c)
    }

    function Hj(b, c, d) {
        var e = b.a;
        b = b.d;
        for (var f = e[d], g = b[d]; d > c;) {
            var h = d - 1 >> 1;
            if (b[h] > g) e[d] = e[h], b[d] = b[h], d = h; else break
        }
        e[d] = f;
        b[d] = g
    }

    function Ij(b) {
        var c = b.e, d = b.a, e = b.d, f = 0, g = d.length, h, k, n;
        for (k = 0; k < g; ++k) h = d[k], n = c(h), Infinity == n ? delete b.b[b.f(h)] : (e[f] = n, d[f++] = h);
        d.length = f;
        e.length = f;
        for (c = (b.a.length >> 1) - 1; 0 <= c; c--) Gj(b, c)
    };

    function Jj(b, c) {
        Ej.call(this, function (c) {
            return b.apply(null, c)
        }, function (b) {
            return b[0].qb()
        });
        this.n = c;
        this.c = 0
    }

    v(Jj, Ej);
    Jj.prototype.g = function (b) {
        b = b.target.state;
        if (2 === b || 3 === b || 4 === b) --this.c, this.n()
    };

    function Kj(b, c, d) {
        this.c = b;
        this.b = c;
        this.e = d;
        this.a = [];
        this.d = this.f = 0
    }

    Kj.prototype.update = function (b, c) {
        this.a.push(b, c, ta())
    };

    function Lj(b, c) {
        var d = b.c, e = b.d, f = b.b - e, g = Mj(b);
        return df({
            source: c, duration: g, easing: function (b) {
                return e * (Math.exp(d * b * g) - 1) / f
            }
        })
    }

    function Mj(b) {
        return Math.log(b.b / b.d) / b.c
    };

    function Nj(b) {
        qd.call(this);
        this.j = null;
        this.b(!0);
        this.handleEvent = b.handleEvent
    }

    v(Nj, qd);
    Nj.prototype.a = function () {
        return this.get("active")
    };
    Nj.prototype.getActive = Nj.prototype.a;
    Nj.prototype.b = function (b) {
        this.set("active", b)
    };
    Nj.prototype.setActive = Nj.prototype.b;
    Nj.prototype.setMap = function (b) {
        this.j = b
    };

    function Oj(b, c, d, e, f) {
        if (null != d) {
            var g = c.c(), h = c.b();
            m(g) && m(h) && m(f) && 0 < f && (b.La(ef({
                rotation: g,
                duration: f,
                easing: $e
            })), m(e) && b.La(df({source: h, duration: f, easing: $e})));
            c.rotate(d, e)
        }
    }

    function Pj(b, c, d, e, f) {
        var g = c.a();
        d = c.constrainResolution(g, d, 0);
        Qj(b, c, d, e, f)
    }

    function Qj(b, c, d, e, f) {
        if (null != d) {
            var g = c.a(), h = c.b();
            m(g) && m(h) && m(f) && 0 < f && (b.La(ff({
                resolution: g,
                duration: f,
                easing: $e
            })), m(e) && b.La(df({source: h, duration: f, easing: $e})));
            if (null != e) {
                var k;
                b = c.b();
                f = c.a();
                m(b) && m(f) && (k = [e[0] - d * (e[0] - b[0]) / f, e[1] - d * (e[1] - b[1]) / f]);
                c.Ha(k)
            }
            c.f(d)
        }
    };

    function Rj(b) {
        b = m(b) ? b : {};
        this.c = m(b.delta) ? b.delta : 1;
        Nj.call(this, {handleEvent: Sj});
        this.f = m(b.duration) ? b.duration : 250
    }

    v(Rj, Nj);

    function Sj(b) {
        var c = !1, d = b.a;
        if (b.type == Bi) {
            var c = b.map, e = b.coordinate, d = d.c ? -this.c : this.c, f = c.a();
            Pj(c, f, d, e, this.f);
            b.preventDefault();
            c = !0
        }
        return !c
    };

    function Tj(b) {
        b = b.a;
        return b.d && !b.g && b.c
    }

    function Uj(b) {
        return "pointermove" == b.type
    }

    function Vj(b) {
        return b.type == Ci
    }

    function Wj(b) {
        b = b.a;
        return !b.d && !b.g && !b.c
    }

    function Xj(b) {
        b = b.a;
        return !b.d && !b.g && b.c
    }

    function Yj(b) {
        b = b.a.target.tagName;
        return "INPUT" !== b && "SELECT" !== b && "TEXTAREA" !== b
    }

    function Zj(b) {
        return 1 == b.d.pointerId
    };

    function ak(b) {
        b = m(b) ? b : {};
        Nj.call(this, {handleEvent: m(b.handleEvent) ? b.handleEvent : bk});
        this.ia = m(b.handleDownEvent) ? b.handleDownEvent : ad;
        this.ka = m(b.handleDragEvent) ? b.handleDragEvent : ca;
        this.va = m(b.handleMoveEvent) ? b.handleMoveEvent : ca;
        this.Ea = m(b.handleUpEvent) ? b.handleUpEvent : ad;
        this.o = !1;
        this.D = {};
        this.f = []
    }

    v(ak, Nj);

    function ck(b) {
        for (var c = b.length, d = 0, e = 0, f = 0; f < c; f++) d += b[f].clientX, e += b[f].clientY;
        return [d / c, e / c]
    }

    function bk(b) {
        if (!(b instanceof xi)) return !0;
        var c = !1, d = b.type;
        if (d === Gi || d === Ii || d === Ei) d = b.d, b.type == Ei ? delete this.D[d.pointerId] : b.type == Gi ? this.D[d.pointerId] = d : d.pointerId in this.D && (this.D[d.pointerId] = d), this.f = qb(this.D);
        this.o && (b.type == Ii ? this.ka(b) : b.type == Ei && (this.o = this.Ea(b)));
        b.type == Gi ? (this.o = b = this.ia(b), c = this.p(b)) : b.type == Hi && this.va(b);
        return !c
    }

    ak.prototype.p = dd;

    function dk(b) {
        ak.call(this, {handleDownEvent: ek, handleDragEvent: fk, handleUpEvent: gk});
        b = m(b) ? b : {};
        this.c = b.kinetic;
        this.e = this.g = null;
        this.q = m(b.condition) ? b.condition : Wj;
        this.i = !1
    }

    v(dk, ak);

    function fk(b) {
        var c = ck(this.f);
        this.c && this.c.update(c[0], c[1]);
        if (null !== this.e) {
            var d = this.e[0] - c[0], e = c[1] - this.e[1];
            b = b.map;
            var f = b.a(), g = Xe(f), e = d = [d, e], h = g.resolution;
            e[0] *= h;
            e[1] *= h;
            Ad(d, g.rotation);
            vd(d, g.center);
            d = f.i(d);
            b.render();
            f.Ha(d)
        }
        this.e = c
    }

    function gk(b) {
        b = b.map;
        var c = b.a();
        if (0 === this.f.length) {
            var d;
            if (d = !this.i && this.c) if (d = this.c, 6 > d.a.length) d = !1; else {
                var e = ta() - d.e, f = d.a.length - 3;
                if (d.a[f + 2] < e) d = !1; else {
                    for (var g = f - 3; 0 < g && d.a[g + 2] > e;) g -= 3;
                    var e = d.a[f + 2] - d.a[g + 2], h = d.a[f] - d.a[g], f = d.a[f + 1] - d.a[g + 1];
                    d.f = Math.atan2(f, h);
                    d.d = Math.sqrt(h * h + f * f) / e;
                    d = d.d > d.b
                }
            }
            d && (d = this.c, d = (d.b - d.d) / d.c, f = this.c.f, g = c.b(), this.g = Lj(this.c, g), b.La(this.g), g = b.e(g), d = b.ra([g[0] - d * Math.cos(f), g[1] - d * Math.sin(f)]), d = c.i(d), c.Ha(d));
            Ze(c, -1);
            b.render();
            return !1
        }
        this.e = null;
        return !0
    }

    function ek(b) {
        if (0 < this.f.length && this.q(b)) {
            var c = b.map, d = c.a();
            this.e = null;
            this.o || Ze(d, 1);
            c.render();
            null !== this.g && Wa(c.N, this.g) && (d.Ha(b.frameState.viewState.center), this.g = null);
            this.c && (b = this.c, b.a.length = 0, b.f = 0, b.d = 0);
            this.i = 1 < this.f.length;
            return !0
        }
        return !1
    }

    dk.prototype.p = ad;

    function hk(b) {
        b = m(b) ? b : {};
        ak.call(this, {handleDownEvent: ik, handleDragEvent: jk, handleUpEvent: kk});
        this.e = m(b.condition) ? b.condition : Tj;
        this.c = void 0
    }

    v(hk, ak);

    function jk(b) {
        if (Zj(b)) {
            var c = b.map, d = c.f();
            b = b.pixel;
            d = Math.atan2(d[1] / 2 - b[1], b[0] - d[0] / 2);
            if (m(this.c)) {
                b = d - this.c;
                var e = c.a(), f = e.c();
                c.render();
                Oj(c, e, f - b)
            }
            this.c = d
        }
    }

    function kk(b) {
        if (!Zj(b)) return !0;
        b = b.map;
        var c = b.a();
        Ze(c, -1);
        var d = c.c(), d = c.constrainRotation(d, 0);
        Oj(b, c, d, void 0, 250);
        return !1
    }

    function ik(b) {
        return Zj(b) && zc(b.a) && this.e(b) ? (b = b.map, Ze(b.a(), 1), b.render(), this.c = void 0, !0) : !1
    }

    hk.prototype.p = ad;

    function lk() {
        kd.call(this);
        this.j = Sd();
        this.q = -1;
        this.e = {};
        this.i = this.g = 0
    }

    v(lk, kd);
    lk.prototype.f = function (b, c) {
        var d = m(c) ? c : [NaN, NaN];
        this.Ya(b[0], b[1], d, Infinity);
        return d
    };
    lk.prototype.Jb = ad;
    lk.prototype.J = function (b) {
        this.q != this.d && (this.j = this.cd(this.j), this.q = this.d);
        var c = this.j;
        m(b) ? (b[0] = c[0], b[1] = c[1], b[2] = c[2], b[3] = c[3]) : b = c;
        return b
    };
    lk.prototype.transform = function (b, c) {
        this.qa(Se(b, c));
        return this
    };

    function mk(b, c, d, e, f, g) {
        var h = f[0], k = f[1], n = f[4], p = f[5], q = f[12];
        f = f[13];
        for (var r = m(g) ? g : [], s = 0; c < d; c += e) {
            var u = b[c], z = b[c + 1];
            r[s++] = h * u + n * z + q;
            r[s++] = k * u + p * z + f
        }
        m(g) && r.length != s && (r.length = s);
        return r
    };

    function nk() {
        lk.call(this);
        this.a = "XY";
        this.B = 2;
        this.k = null
    }

    v(nk, lk);

    function ok(b) {
        if ("XY" == b) return 2;
        if ("XYZ" == b || "XYM" == b) return 3;
        if ("XYZM" == b) return 4
    }

    l = nk.prototype;
    l.Jb = ad;
    l.cd = function (b) {
        var c = this.k, d = this.k.length, e = this.B;
        b = Vd(Infinity, Infinity, -Infinity, -Infinity, b);
        return ee(b, c, 0, d, e)
    };
    l.yb = function () {
        return this.k.slice(0, this.B)
    };
    l.zb = function () {
        return this.k.slice(this.k.length - this.B)
    };
    l.Ab = function () {
        return this.a
    };
    l.se = function (b) {
        this.i != this.d && (wb(this.e), this.g = 0, this.i = this.d);
        if (0 > b || 0 !== this.g && b <= this.g) return this;
        var c = b.toString();
        if (this.e.hasOwnProperty(c)) return this.e[c];
        var d = this.nc(b);
        if (d.k.length < this.k.length) return this.e[c] = d;
        this.g = b;
        return this
    };
    l.nc = function () {
        return this
    };

    function pk(b, c, d) {
        b.B = ok(c);
        b.a = c;
        b.k = d
    }

    function qk(b, c, d, e) {
        if (m(c)) d = ok(c); else {
            for (c = 0; c < e; ++c) {
                if (0 === d.length) {
                    b.a = "XY";
                    b.B = 2;
                    return
                }
                d = d[0]
            }
            d = d.length;
            c = 2 == d ? "XY" : 3 == d ? "XYZ" : 4 == d ? "XYZM" : void 0
        }
        b.a = c;
        b.B = d
    }

    l.qa = function (b) {
        null !== this.k && (b(this.k, this.k, this.B), this.l())
    };
    l.Ia = function (b, c) {
        var d = this.k;
        if (null !== d) {
            var e = d.length, f = this.B, g = m(d) ? d : [], h = 0, k, n;
            for (k = 0; k < e; k += f) for (g[h++] = d[k] + b, g[h++] = d[k + 1] + c, n = k + 2; n < k + f; ++n) g[h++] = d[n];
            m(d) && g.length != h && (g.length = h);
            this.l()
        }
    };

    function rk(b, c, d, e) {
        for (var f = 0, g = b[d - e], h = b[d - e + 1]; c < d; c += e) var k = b[c], n = b[c + 1], f = f + (h * k - g * n), g = k, h = n;
        return f / 2
    }

    function sk(b, c, d, e) {
        var f = 0, g, h;
        g = 0;
        for (h = d.length; g < h; ++g) {
            var k = d[g], f = f + rk(b, c, k, e);
            c = k
        }
        return f
    };

    function tk(b, c, d, e, f, g) {
        var h = f - d, k = g - e;
        if (0 !== h || 0 !== k) {
            var n = ((b - d) * h + (c - e) * k) / (h * h + k * k);
            1 < n ? (d = f, e = g) : 0 < n && (d += h * n, e += k * n)
        }
        return uk(b, c, d, e)
    }

    function uk(b, c, d, e) {
        b = d - b;
        c = e - c;
        return b * b + c * c
    };

    function vk(b, c, d, e, f, g, h) {
        var k = b[c], n = b[c + 1], p = b[d] - k, q = b[d + 1] - n;
        if (0 !== p || 0 !== q) if (g = ((f - k) * p + (g - n) * q) / (p * p + q * q), 1 < g) c = d; else if (0 < g) {
            for (f = 0; f < e; ++f) h[f] = Xb(b[c + f], b[d + f], g);
            h.length = e;
            return
        }
        for (f = 0; f < e; ++f) h[f] = b[c + f];
        h.length = e
    }

    function wk(b, c, d, e, f) {
        var g = b[c], h = b[c + 1];
        for (c += e; c < d; c += e) {
            var k = b[c], n = b[c + 1], g = uk(g, h, k, n);
            g > f && (f = g);
            g = k;
            h = n
        }
        return f
    }

    function xk(b, c, d, e, f) {
        var g, h;
        g = 0;
        for (h = d.length; g < h; ++g) {
            var k = d[g];
            f = wk(b, c, k, e, f);
            c = k
        }
        return f
    }

    function yk(b, c, d, e, f, g, h, k, n, p, q) {
        if (c == d) return p;
        var r;
        if (0 === f) {
            r = uk(h, k, b[c], b[c + 1]);
            if (r < p) {
                for (q = 0; q < e; ++q) n[q] = b[c + q];
                n.length = e;
                return r
            }
            return p
        }
        for (var s = m(q) ? q : [NaN, NaN], u = c + e; u < d;) if (vk(b, u - e, u, e, h, k, s), r = uk(h, k, s[0], s[1]), r < p) {
            p = r;
            for (q = 0; q < e; ++q) n[q] = s[q];
            n.length = e;
            u += e
        } else u += e * Math.max((Math.sqrt(r) - Math.sqrt(p)) / f | 0, 1);
        if (g && (vk(b, d - e, c, e, h, k, s), r = uk(h, k, s[0], s[1]), r < p)) {
            p = r;
            for (q = 0; q < e; ++q) n[q] = s[q];
            n.length = e
        }
        return p
    }

    function zk(b, c, d, e, f, g, h, k, n, p, q) {
        q = m(q) ? q : [NaN, NaN];
        var r, s;
        r = 0;
        for (s = d.length; r < s; ++r) {
            var u = d[r];
            p = yk(b, c, u, e, f, g, h, k, n, p, q);
            c = u
        }
        return p
    };

    function Ak(b, c) {
        var d = 0, e, f;
        e = 0;
        for (f = c.length; e < f; ++e) b[d++] = c[e];
        return d
    }

    function Bk(b, c, d, e) {
        var f, g;
        f = 0;
        for (g = d.length; f < g; ++f) {
            var h = d[f], k;
            for (k = 0; k < e; ++k) b[c++] = h[k]
        }
        return c
    }

    function Ck(b, c, d, e, f) {
        f = m(f) ? f : [];
        var g = 0, h, k;
        h = 0;
        for (k = d.length; h < k; ++h) c = Bk(b, c, d[h], e), f[g++] = c;
        f.length = g;
        return f
    };

    function Dk(b, c, d, e, f) {
        f = m(f) ? f : [];
        for (var g = 0; c < d; c += e) f[g++] = b.slice(c, c + e);
        f.length = g;
        return f
    }

    function Ek(b, c, d, e, f) {
        f = m(f) ? f : [];
        var g = 0, h, k;
        h = 0;
        for (k = d.length; h < k; ++h) {
            var n = d[h];
            f[g++] = Dk(b, c, n, e, f[g]);
            c = n
        }
        f.length = g;
        return f
    };

    function Fk(b, c, d, e, f, g, h) {
        var k = (d - c) / e;
        if (3 > k) {
            for (; c < d; c += e) g[h++] = b[c], g[h++] = b[c + 1];
            return h
        }
        var n = Array(k);
        n[0] = 1;
        n[k - 1] = 1;
        d = [c, d - e];
        for (var p = 0, q; 0 < d.length;) {
            var r = d.pop(), s = d.pop(), u = 0, z = b[s], y = b[s + 1], A = b[r], D = b[r + 1];
            for (q = s + e; q < r; q += e) {
                var x = tk(b[q], b[q + 1], z, y, A, D);
                x > u && (p = q, u = x)
            }
            u > f && (n[(p - c) / e] = 1, s + e < p && d.push(s, p), p + e < r && d.push(p, r))
        }
        for (q = 0; q < k; ++q) n[q] && (g[h++] = b[c + q * e], g[h++] = b[c + q * e + 1]);
        return h
    }

    function Gk(b, c, d, e, f, g, h, k) {
        var n, p;
        n = 0;
        for (p = d.length; n < p; ++n) {
            var q = d[n];
            a:{
                var r = b, s = q, u = e, z = f, y = g;
                if (c != s) {
                    var A = z * Math.round(r[c] / z), D = z * Math.round(r[c + 1] / z);
                    c += u;
                    y[h++] = A;
                    y[h++] = D;
                    var x = void 0, M = void 0;
                    do if (x = z * Math.round(r[c] / z), M = z * Math.round(r[c + 1] / z), c += u, c == s) {
                        y[h++] = x;
                        y[h++] = M;
                        break a
                    } while (x == A && M == D);
                    for (; c < s;) {
                        var Q, O;
                        Q = z * Math.round(r[c] / z);
                        O = z * Math.round(r[c + 1] / z);
                        c += u;
                        if (Q != x || O != M) {
                            var W = x - A, Ja = M - D, lb = Q - A, Ka = O - D;
                            W * Ka == Ja * lb && (0 > W && lb < W || W == lb || 0 < W && lb > W) && (0 > Ja && Ka < Ja || Ja == Ka ||
                                0 < Ja && Ka > Ja) || (y[h++] = x, y[h++] = M, A = x, D = M);
                            x = Q;
                            M = O
                        }
                    }
                    y[h++] = x;
                    y[h++] = M
                }
            }
            k.push(h);
            c = q
        }
        return h
    };

    function Hk(b, c) {
        nk.call(this);
        this.b = this.n = -1;
        this.V(b, c)
    }

    v(Hk, nk);
    l = Hk.prototype;
    l.clone = function () {
        var b = new Hk(null);
        Ik(b, this.a, this.k.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        this.b != this.d && (this.n = Math.sqrt(wk(this.k, 0, this.k.length, this.B, 0)), this.b = this.d);
        return yk(this.k, 0, this.k.length, this.B, this.n, !0, b, c, d, e)
    };
    l.Nj = function () {
        return rk(this.k, 0, this.k.length, this.B)
    };
    l.Q = function () {
        return Dk(this.k, 0, this.k.length, this.B)
    };
    l.nc = function (b) {
        var c = [];
        c.length = Fk(this.k, 0, this.k.length, this.B, b, c, 0);
        b = new Hk(null);
        Ik(b, "XY", c);
        return b
    };
    l.O = function () {
        return "LinearRing"
    };
    l.V = function (b, c) {
        null === b ? Ik(this, "XY", null) : (qk(this, c, b, 1), null === this.k && (this.k = []), this.k.length = Bk(this.k, 0, b, this.B), this.l())
    };

    function Ik(b, c, d) {
        pk(b, c, d);
        b.l()
    };

    function Jk(b, c) {
        nk.call(this);
        this.V(b, c)
    }

    v(Jk, nk);
    l = Jk.prototype;
    l.clone = function () {
        var b = new Jk(null);
        Kk(b, this.a, this.k.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        var f = this.k;
        b = uk(b, c, f[0], f[1]);
        if (b < e) {
            e = this.B;
            for (c = 0; c < e; ++c) d[c] = f[c];
            d.length = e;
            return b
        }
        return e
    };
    l.Q = function () {
        return null === this.k ? [] : this.k.slice()
    };
    l.cd = function (b) {
        return be(this.k, b)
    };
    l.O = function () {
        return "Point"
    };
    l.ja = function (b) {
        return $d(b, this.k[0], this.k[1])
    };
    l.V = function (b, c) {
        null === b ? Kk(this, "XY", null) : (qk(this, c, b, 0), null === this.k && (this.k = []), this.k.length = Ak(this.k, b), this.l())
    };

    function Kk(b, c, d) {
        pk(b, c, d);
        b.l()
    };

    function Lk(b, c, d, e, f) {
        return !fe(f, function (f) {
            return !Mk(b, c, d, e, f[0], f[1])
        })
    }

    function Mk(b, c, d, e, f, g) {
        for (var h = !1, k = b[d - e], n = b[d - e + 1]; c < d; c += e) {
            var p = b[c], q = b[c + 1];
            n > g != q > g && f < (p - k) * (g - n) / (q - n) + k && (h = !h);
            k = p;
            n = q
        }
        return h
    }

    function Nk(b, c, d, e, f, g) {
        if (0 === d.length || !Mk(b, c, d[0], e, f, g)) return !1;
        var h;
        c = 1;
        for (h = d.length; c < h; ++c) if (Mk(b, d[c - 1], d[c], e, f, g)) return !1;
        return !0
    };

    function Ok(b, c, d, e, f, g, h) {
        var k, n, p, q, r, s = f[g + 1], u = [], z = d[0];
        p = b[z - e];
        r = b[z - e + 1];
        for (k = c; k < z; k += e) {
            q = b[k];
            n = b[k + 1];
            if (s <= r && n <= s || r <= s && s <= n) p = (s - r) / (n - r) * (q - p) + p, u.push(p);
            p = q;
            r = n
        }
        z = NaN;
        r = -Infinity;
        u.sort();
        p = u[0];
        k = 1;
        for (n = u.length; k < n; ++k) {
            q = u[k];
            var y = Math.abs(q - p);
            y > r && (p = (p + q) / 2, Nk(b, c, d, e, p, s) && (z = p, r = y));
            p = q
        }
        isNaN(z) && (z = f[g]);
        return m(h) ? (h.push(z, s), h) : [z, s]
    };

    function Pk(b, c, d, e, f) {
        for (var g = [b[c], b[c + 1]], h = [], k; c + e < d; c += e) {
            h[0] = b[c + e];
            h[1] = b[c + e + 1];
            if (k = f(g, h)) return k;
            g[0] = h[0];
            g[1] = h[1]
        }
        return !1
    };

    function Qk(b, c, d, e, f) {
        var g = ee(Sd(), b, c, d, e);
        return pe(f, g) ? Zd(f, g) || g[0] >= f[0] && g[2] <= f[2] || g[1] >= f[1] && g[3] <= f[3] ? !0 : Pk(b, c, d, e, function (b, c) {
            var d = !1, e = ae(f, b), g = ae(f, c);
            if (1 === e || 1 === g) d = !0; else {
                var r = f[0], s = f[1], u = f[2], z = f[3], y = c[0], A = c[1], D = (A - b[1]) / (y - b[0]);
                g & 2 && !(e & 2) ? (s = y - (A - z) / D, d = s >= r && s <= u) : g & 4 && !(e & 4) ? (r = A - (y - u) * D, d = r >= s && r <= z) : g & 8 && !(e & 8) ? (s = y - (A - s) / D, d = s >= r && s <= u) : g & 16 && !(e & 16) && (r = A - (y - r) * D, d = r >= s && r <= z)
            }
            return d
        }) : !1
    }

    function Rk(b, c, d, e, f) {
        var g = d[0];
        if (!(Qk(b, c, g, e, f) || Mk(b, c, g, e, f[0], f[1]) || Mk(b, c, g, e, f[0], f[3]) || Mk(b, c, g, e, f[2], f[1]) || Mk(b, c, g, e, f[2], f[3]))) return !1;
        if (1 === d.length) return !0;
        c = 1;
        for (g = d.length; c < g; ++c) if (Lk(b, d[c - 1], d[c], e, f)) return !1;
        return !0
    };

    function Sk(b, c, d, e) {
        for (var f = 0, g = b[d - e], h = b[d - e + 1]; c < d; c += e) var k = b[c], n = b[c + 1], f = f + (k - g) * (n + h), g = k, h = n;
        return 0 < f
    }

    function Tk(b, c, d) {
        var e = 0, f, g;
        f = 0;
        for (g = c.length; f < g; ++f) {
            var h = c[f], e = Sk(b, e, h, d);
            if (0 === f ? !e : e) return !1;
            e = h
        }
        return !0
    }

    function Uk(b, c, d, e) {
        var f, g;
        f = 0;
        for (g = d.length; f < g; ++f) {
            var h = d[f], k = Sk(b, c, h, e);
            if (0 === f ? !k : k) for (var k = b, n = h, p = e; c < n - p;) {
                var q;
                for (q = 0; q < p; ++q) {
                    var r = k[c + q];
                    k[c + q] = k[n - p + q];
                    k[n - p + q] = r
                }
                c += p;
                n -= p
            }
            c = h
        }
        return c
    };

    function F(b, c) {
        nk.call(this);
        this.b = [];
        this.o = -1;
        this.p = null;
        this.H = this.r = this.D = -1;
        this.n = null;
        this.V(b, c)
    }

    v(F, nk);
    l = F.prototype;
    l.ih = function (b) {
        null === this.k ? this.k = b.k.slice() : $a(this.k, b.k);
        this.b.push(this.k.length);
        this.l()
    };
    l.clone = function () {
        var b = new F(null);
        Vk(b, this.a, this.k.slice(), this.b.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        this.r != this.d && (this.D = Math.sqrt(xk(this.k, 0, this.b, this.B, 0)), this.r = this.d);
        return zk(this.k, 0, this.b, this.B, this.D, !0, b, c, d, e)
    };
    l.Jb = function (b, c) {
        return Nk(Wk(this), 0, this.b, this.B, b, c)
    };
    l.Qj = function () {
        return sk(Wk(this), 0, this.b, this.B)
    };
    l.Q = function () {
        return Ek(this.k, 0, this.b, this.B)
    };

    function Xk(b) {
        if (b.o != b.d) {
            var c = ke(b.J());
            b.p = Ok(Wk(b), 0, b.b, b.B, c, 0);
            b.o = b.d
        }
        return b.p
    }

    l.Jh = function () {
        return new Jk(Xk(this))
    };
    l.Ph = function () {
        return this.b.length
    };
    l.Oh = function (b) {
        if (0 > b || this.b.length <= b) return null;
        var c = new Hk(null);
        Ik(c, this.a, this.k.slice(0 === b ? 0 : this.b[b - 1], this.b[b]));
        return c
    };
    l.kd = function () {
        var b = this.a, c = this.k, d = this.b, e = [], f = 0, g, h;
        g = 0;
        for (h = d.length; g < h; ++g) {
            var k = d[g], n = new Hk(null);
            Ik(n, b, c.slice(f, k));
            e.push(n);
            f = k
        }
        return e
    };

    function Wk(b) {
        if (b.H != b.d) {
            var c = b.k;
            Tk(c, b.b, b.B) ? b.n = c : (b.n = c.slice(), b.n.length = Uk(b.n, 0, b.b, b.B));
            b.H = b.d
        }
        return b.n
    }

    l.nc = function (b) {
        var c = [], d = [];
        c.length = Gk(this.k, 0, this.b, this.B, Math.sqrt(b), c, 0, d);
        b = new F(null);
        Vk(b, "XY", c, d);
        return b
    };
    l.O = function () {
        return "Polygon"
    };
    l.ja = function (b) {
        return Rk(Wk(this), 0, this.b, this.B, b)
    };
    l.V = function (b, c) {
        if (null === b) Vk(this, "XY", null, this.b); else {
            qk(this, c, b, 2);
            null === this.k && (this.k = []);
            var d = Ck(this.k, 0, b, this.B, this.b);
            this.k.length = 0 === d.length ? 0 : d[d.length - 1];
            this.l()
        }
    };

    function Vk(b, c, d, e) {
        pk(b, c, d);
        b.b = e;
        b.l()
    }

    function Yk(b, c, d, e) {
        var f = m(e) ? e : 32;
        e = [];
        var g;
        for (g = 0; g < f; ++g) $a(e, b.offset(c, d, 2 * Math.PI * g / f));
        e.push(e[0], e[1]);
        b = new F(null);
        Vk(b, "XY", e, [e.length]);
        return b
    };

    function Zk(b, c, d, e, f, g, h) {
        qc.call(this, b, c);
        this.vectorContext = d;
        this.a = e;
        this.frameState = f;
        this.context = g;
        this.glContext = h
    }

    v(Zk, qc);

    function $k(b) {
        this.b = this.d = this.f = this.c = this.a = null;
        this.e = b
    }

    v($k, lc);

    function al(b) {
        var c = b.f, d = b.d;
        b = Ra([c, [c[0], d[1]], d, [d[0], c[1]]], b.a.ra, b.a);
        b[4] = b[0].slice();
        return new F([b])
    }

    $k.prototype.P = function () {
        this.setMap(null)
    };
    $k.prototype.g = function (b) {
        var c = this.b, d = this.e;
        b.vectorContext.jc(Infinity, function (b) {
            b.Ba(d.f, d.b);
            b.Ca(d.d);
            b.Rb(c, null)
        })
    };
    $k.prototype.R = function () {
        return this.b
    };

    function bl(b) {
        null === b.a || null === b.f || null === b.d || b.a.render()
    }

    $k.prototype.setMap = function (b) {
        null !== this.c && (Wc(this.c), this.c = null, this.a.render(), this.a = null);
        this.a = b;
        null !== this.a && (this.c = w(b, "postcompose", this.g, !1, this), bl(this))
    };

    function cl(b, c) {
        qc.call(this, b);
        this.coordinate = c
    }

    v(cl, qc);

    function dl(b) {
        ak.call(this, {handleDownEvent: el, handleDragEvent: fl, handleUpEvent: gl});
        b = m(b) ? b : {};
        this.e = new $k(m(b.style) ? b.style : null);
        this.c = null;
        this.i = m(b.condition) ? b.condition : bd
    }

    v(dl, ak);

    function fl(b) {
        if (Zj(b)) {
            var c = this.e;
            b = b.pixel;
            c.f = this.c;
            c.d = b;
            c.b = al(c);
            bl(c)
        }
    }

    dl.prototype.R = function () {
        return this.e.R()
    };
    dl.prototype.g = ca;

    function gl(b) {
        if (!Zj(b)) return !0;
        this.e.setMap(null);
        var c = b.pixel[0] - this.c[0], d = b.pixel[1] - this.c[1];
        64 <= c * c + d * d && (this.g(b), this.dispatchEvent(new cl("boxend", b.coordinate)));
        return !1
    }

    function el(b) {
        if (Zj(b) && zc(b.a) && this.i(b)) {
            this.c = b.pixel;
            this.e.setMap(b.map);
            var c = this.e, d = this.c;
            c.f = this.c;
            c.d = d;
            c.b = al(c);
            bl(c);
            this.dispatchEvent(new cl("boxstart", b.coordinate));
            return !0
        }
        return !1
    };

    function hl() {
        this.d = -1
    };

    function il() {
        this.d = -1;
        this.d = 64;
        this.a = Array(4);
        this.f = Array(this.d);
        this.c = this.b = 0;
        this.a[0] = 1732584193;
        this.a[1] = 4023233417;
        this.a[2] = 2562383102;
        this.a[3] = 271733878;
        this.c = this.b = 0
    }

    v(il, hl);

    function jl(b, c, d) {
        d || (d = 0);
        var e = Array(16);
        if (ia(c)) for (var f = 0; 16 > f; ++f) e[f] = c.charCodeAt(d++) | c.charCodeAt(d++) << 8 | c.charCodeAt(d++) << 16 | c.charCodeAt(d++) << 24; else for (f = 0; 16 > f; ++f) e[f] = c[d++] | c[d++] << 8 | c[d++] << 16 | c[d++] << 24;
        c = b.a[0];
        d = b.a[1];
        var f = b.a[2], g = b.a[3], h = 0, h = c + (g ^ d & (f ^ g)) + e[0] + 3614090360 & 4294967295;
        c = d + (h << 7 & 4294967295 | h >>> 25);
        h = g + (f ^ c & (d ^ f)) + e[1] + 3905402710 & 4294967295;
        g = c + (h << 12 & 4294967295 | h >>> 20);
        h = f + (d ^ g & (c ^ d)) + e[2] + 606105819 & 4294967295;
        f = g + (h << 17 & 4294967295 | h >>> 15);
        h = d + (c ^ f & (g ^
            c)) + e[3] + 3250441966 & 4294967295;
        d = f + (h << 22 & 4294967295 | h >>> 10);
        h = c + (g ^ d & (f ^ g)) + e[4] + 4118548399 & 4294967295;
        c = d + (h << 7 & 4294967295 | h >>> 25);
        h = g + (f ^ c & (d ^ f)) + e[5] + 1200080426 & 4294967295;
        g = c + (h << 12 & 4294967295 | h >>> 20);
        h = f + (d ^ g & (c ^ d)) + e[6] + 2821735955 & 4294967295;
        f = g + (h << 17 & 4294967295 | h >>> 15);
        h = d + (c ^ f & (g ^ c)) + e[7] + 4249261313 & 4294967295;
        d = f + (h << 22 & 4294967295 | h >>> 10);
        h = c + (g ^ d & (f ^ g)) + e[8] + 1770035416 & 4294967295;
        c = d + (h << 7 & 4294967295 | h >>> 25);
        h = g + (f ^ c & (d ^ f)) + e[9] + 2336552879 & 4294967295;
        g = c + (h << 12 & 4294967295 | h >>> 20);
        h = f +
            (d ^ g & (c ^ d)) + e[10] + 4294925233 & 4294967295;
        f = g + (h << 17 & 4294967295 | h >>> 15);
        h = d + (c ^ f & (g ^ c)) + e[11] + 2304563134 & 4294967295;
        d = f + (h << 22 & 4294967295 | h >>> 10);
        h = c + (g ^ d & (f ^ g)) + e[12] + 1804603682 & 4294967295;
        c = d + (h << 7 & 4294967295 | h >>> 25);
        h = g + (f ^ c & (d ^ f)) + e[13] + 4254626195 & 4294967295;
        g = c + (h << 12 & 4294967295 | h >>> 20);
        h = f + (d ^ g & (c ^ d)) + e[14] + 2792965006 & 4294967295;
        f = g + (h << 17 & 4294967295 | h >>> 15);
        h = d + (c ^ f & (g ^ c)) + e[15] + 1236535329 & 4294967295;
        d = f + (h << 22 & 4294967295 | h >>> 10);
        h = c + (f ^ g & (d ^ f)) + e[1] + 4129170786 & 4294967295;
        c = d + (h << 5 & 4294967295 |
            h >>> 27);
        h = g + (d ^ f & (c ^ d)) + e[6] + 3225465664 & 4294967295;
        g = c + (h << 9 & 4294967295 | h >>> 23);
        h = f + (c ^ d & (g ^ c)) + e[11] + 643717713 & 4294967295;
        f = g + (h << 14 & 4294967295 | h >>> 18);
        h = d + (g ^ c & (f ^ g)) + e[0] + 3921069994 & 4294967295;
        d = f + (h << 20 & 4294967295 | h >>> 12);
        h = c + (f ^ g & (d ^ f)) + e[5] + 3593408605 & 4294967295;
        c = d + (h << 5 & 4294967295 | h >>> 27);
        h = g + (d ^ f & (c ^ d)) + e[10] + 38016083 & 4294967295;
        g = c + (h << 9 & 4294967295 | h >>> 23);
        h = f + (c ^ d & (g ^ c)) + e[15] + 3634488961 & 4294967295;
        f = g + (h << 14 & 4294967295 | h >>> 18);
        h = d + (g ^ c & (f ^ g)) + e[4] + 3889429448 & 4294967295;
        d = f + (h << 20 & 4294967295 |
            h >>> 12);
        h = c + (f ^ g & (d ^ f)) + e[9] + 568446438 & 4294967295;
        c = d + (h << 5 & 4294967295 | h >>> 27);
        h = g + (d ^ f & (c ^ d)) + e[14] + 3275163606 & 4294967295;
        g = c + (h << 9 & 4294967295 | h >>> 23);
        h = f + (c ^ d & (g ^ c)) + e[3] + 4107603335 & 4294967295;
        f = g + (h << 14 & 4294967295 | h >>> 18);
        h = d + (g ^ c & (f ^ g)) + e[8] + 1163531501 & 4294967295;
        d = f + (h << 20 & 4294967295 | h >>> 12);
        h = c + (f ^ g & (d ^ f)) + e[13] + 2850285829 & 4294967295;
        c = d + (h << 5 & 4294967295 | h >>> 27);
        h = g + (d ^ f & (c ^ d)) + e[2] + 4243563512 & 4294967295;
        g = c + (h << 9 & 4294967295 | h >>> 23);
        h = f + (c ^ d & (g ^ c)) + e[7] + 1735328473 & 4294967295;
        f = g + (h << 14 & 4294967295 |
            h >>> 18);
        h = d + (g ^ c & (f ^ g)) + e[12] + 2368359562 & 4294967295;
        d = f + (h << 20 & 4294967295 | h >>> 12);
        h = c + (d ^ f ^ g) + e[5] + 4294588738 & 4294967295;
        c = d + (h << 4 & 4294967295 | h >>> 28);
        h = g + (c ^ d ^ f) + e[8] + 2272392833 & 4294967295;
        g = c + (h << 11 & 4294967295 | h >>> 21);
        h = f + (g ^ c ^ d) + e[11] + 1839030562 & 4294967295;
        f = g + (h << 16 & 4294967295 | h >>> 16);
        h = d + (f ^ g ^ c) + e[14] + 4259657740 & 4294967295;
        d = f + (h << 23 & 4294967295 | h >>> 9);
        h = c + (d ^ f ^ g) + e[1] + 2763975236 & 4294967295;
        c = d + (h << 4 & 4294967295 | h >>> 28);
        h = g + (c ^ d ^ f) + e[4] + 1272893353 & 4294967295;
        g = c + (h << 11 & 4294967295 | h >>> 21);
        h = f + (g ^
            c ^ d) + e[7] + 4139469664 & 4294967295;
        f = g + (h << 16 & 4294967295 | h >>> 16);
        h = d + (f ^ g ^ c) + e[10] + 3200236656 & 4294967295;
        d = f + (h << 23 & 4294967295 | h >>> 9);
        h = c + (d ^ f ^ g) + e[13] + 681279174 & 4294967295;
        c = d + (h << 4 & 4294967295 | h >>> 28);
        h = g + (c ^ d ^ f) + e[0] + 3936430074 & 4294967295;
        g = c + (h << 11 & 4294967295 | h >>> 21);
        h = f + (g ^ c ^ d) + e[3] + 3572445317 & 4294967295;
        f = g + (h << 16 & 4294967295 | h >>> 16);
        h = d + (f ^ g ^ c) + e[6] + 76029189 & 4294967295;
        d = f + (h << 23 & 4294967295 | h >>> 9);
        h = c + (d ^ f ^ g) + e[9] + 3654602809 & 4294967295;
        c = d + (h << 4 & 4294967295 | h >>> 28);
        h = g + (c ^ d ^ f) + e[12] + 3873151461 & 4294967295;
        g = c + (h << 11 & 4294967295 | h >>> 21);
        h = f + (g ^ c ^ d) + e[15] + 530742520 & 4294967295;
        f = g + (h << 16 & 4294967295 | h >>> 16);
        h = d + (f ^ g ^ c) + e[2] + 3299628645 & 4294967295;
        d = f + (h << 23 & 4294967295 | h >>> 9);
        h = c + (f ^ (d | ~g)) + e[0] + 4096336452 & 4294967295;
        c = d + (h << 6 & 4294967295 | h >>> 26);
        h = g + (d ^ (c | ~f)) + e[7] + 1126891415 & 4294967295;
        g = c + (h << 10 & 4294967295 | h >>> 22);
        h = f + (c ^ (g | ~d)) + e[14] + 2878612391 & 4294967295;
        f = g + (h << 15 & 4294967295 | h >>> 17);
        h = d + (g ^ (f | ~c)) + e[5] + 4237533241 & 4294967295;
        d = f + (h << 21 & 4294967295 | h >>> 11);
        h = c + (f ^ (d | ~g)) + e[12] + 1700485571 & 4294967295;
        c = d +
            (h << 6 & 4294967295 | h >>> 26);
        h = g + (d ^ (c | ~f)) + e[3] + 2399980690 & 4294967295;
        g = c + (h << 10 & 4294967295 | h >>> 22);
        h = f + (c ^ (g | ~d)) + e[10] + 4293915773 & 4294967295;
        f = g + (h << 15 & 4294967295 | h >>> 17);
        h = d + (g ^ (f | ~c)) + e[1] + 2240044497 & 4294967295;
        d = f + (h << 21 & 4294967295 | h >>> 11);
        h = c + (f ^ (d | ~g)) + e[8] + 1873313359 & 4294967295;
        c = d + (h << 6 & 4294967295 | h >>> 26);
        h = g + (d ^ (c | ~f)) + e[15] + 4264355552 & 4294967295;
        g = c + (h << 10 & 4294967295 | h >>> 22);
        h = f + (c ^ (g | ~d)) + e[6] + 2734768916 & 4294967295;
        f = g + (h << 15 & 4294967295 | h >>> 17);
        h = d + (g ^ (f | ~c)) + e[13] + 1309151649 & 4294967295;
        d = f + (h << 21 & 4294967295 | h >>> 11);
        h = c + (f ^ (d | ~g)) + e[4] + 4149444226 & 4294967295;
        c = d + (h << 6 & 4294967295 | h >>> 26);
        h = g + (d ^ (c | ~f)) + e[11] + 3174756917 & 4294967295;
        g = c + (h << 10 & 4294967295 | h >>> 22);
        h = f + (c ^ (g | ~d)) + e[2] + 718787259 & 4294967295;
        f = g + (h << 15 & 4294967295 | h >>> 17);
        h = d + (g ^ (f | ~c)) + e[9] + 3951481745 & 4294967295;
        b.a[0] = b.a[0] + c & 4294967295;
        b.a[1] = b.a[1] + (f + (h << 21 & 4294967295 | h >>> 11)) & 4294967295;
        b.a[2] = b.a[2] + f & 4294967295;
        b.a[3] = b.a[3] + g & 4294967295
    }

    il.prototype.update = function (b, c) {
        m(c) || (c = b.length);
        for (var d = c - this.d, e = this.f, f = this.b, g = 0; g < c;) {
            if (0 == f) for (; g <= d;) jl(this, b, g), g += this.d;
            if (ia(b)) for (; g < c;) {
                if (e[f++] = b.charCodeAt(g++), f == this.d) {
                    jl(this, e);
                    f = 0;
                    break
                }
            } else for (; g < c;) if (e[f++] = b[g++], f == this.d) {
                jl(this, e);
                f = 0;
                break
            }
        }
        this.b = f;
        this.c += c
    };

    function kl(b) {
        b = m(b) ? b : {};
        this.a = m(b.color) ? b.color : null;
        this.c = b.lineCap;
        this.b = m(b.lineDash) ? b.lineDash : null;
        this.f = b.lineJoin;
        this.e = b.miterLimit;
        this.d = b.width;
        this.g = void 0
    }

    l = kl.prototype;
    l.Lk = function () {
        return this.a
    };
    l.Lh = function () {
        return this.c
    };
    l.Mk = function () {
        return this.b
    };
    l.Mh = function () {
        return this.f
    };
    l.Sh = function () {
        return this.e
    };
    l.Nk = function () {
        return this.d
    };
    l.Ok = function (b) {
        this.a = b;
        this.g = void 0
    };
    l.Xl = function (b) {
        this.c = b;
        this.g = void 0
    };
    l.Pk = function (b) {
        this.b = b;
        this.g = void 0
    };
    l.Yl = function (b) {
        this.f = b;
        this.g = void 0
    };
    l.Zl = function (b) {
        this.e = b;
        this.g = void 0
    };
    l.fm = function (b) {
        this.d = b;
        this.g = void 0
    };
    l.xb = function () {
        if (!m(this.g)) {
            var b = "s" + (null === this.a ? "-" : sg(this.a)) + "," + (m(this.c) ? this.c.toString() : "-") + "," + (null === this.b ? "-" : this.b.toString()) + "," + (m(this.f) ? this.f : "-") + "," + (m(this.e) ? this.e.toString() : "-") + "," + (m(this.d) ? this.d.toString() : "-"),
                c = new il;
            c.update(b);
            var d = Array((56 > c.b ? c.d : 2 * c.d) - c.b);
            d[0] = 128;
            for (b = 1; b < d.length - 8; ++b) d[b] = 0;
            for (var e = 8 * c.c, b = d.length - 8; b < d.length; ++b) d[b] = e & 255, e /= 256;
            c.update(d);
            d = Array(16);
            for (b = e = 0; 4 > b; ++b) for (var f = 0; 32 > f; f += 8) d[e++] = c.a[b] >>> f & 255;
            if (8192 > d.length) c = String.fromCharCode.apply(null, d); else for (c = "", b = 0; b < d.length; b += 8192) c += String.fromCharCode.apply(null, bb(d, b, b + 8192));
            this.g = c
        }
        return this.g
    };
    var ll = [0, 0, 0, 1], ml = [], nl = [0, 0, 0, 1];

    function pl(b) {
        b = m(b) ? b : {};
        this.a = m(b.color) ? b.color : null;
        this.d = void 0
    }

    pl.prototype.b = function () {
        return this.a
    };
    pl.prototype.c = function (b) {
        this.a = b;
        this.d = void 0
    };
    pl.prototype.xb = function () {
        m(this.d) || (this.d = "f" + (null === this.a ? "-" : sg(this.a)));
        return this.d
    };

    function ql(b) {
        b = m(b) ? b : {};
        this.e = this.a = this.f = null;
        this.c = m(b.fill) ? b.fill : null;
        this.d = m(b.stroke) ? b.stroke : null;
        this.b = b.radius;
        this.q = [0, 0];
        this.n = this.D = this.g = null;
        var c = b.atlasManager, d, e = null, f, g = 0;
        null !== this.d && (f = sg(this.d.a), g = this.d.d, m(g) || (g = 1), e = this.d.b, Yf || (e = null));
        var h = 2 * (this.b + g) + 1;
        f = {strokeStyle: f, Tc: g, size: h, lineDash: e};
        m(c) ? (h = Math.round(h), (e = null === this.c) && (d = ra(this.Qf, this, f)), g = this.xb(), f = c.add(g, h, h, ra(this.Rf, this, f), d), this.a = f.image, this.q = [f.offsetX, f.offsetY],
            d = f.image.width, this.e = e ? f.xf : this.a) : (this.a = Df("CANVAS"), this.a.height = h, this.a.width = h, d = h = this.a.width, c = this.a.getContext("2d"), this.Rf(f, c, 0, 0), null === this.c ? (c = this.e = Df("CANVAS"), c.height = f.size, c.width = f.size, c = c.getContext("2d"), this.Qf(f, c, 0, 0)) : this.e = this.a);
        this.g = [h / 2, h / 2];
        this.D = [h, h];
        this.n = [d, d];
        vj.call(this, {
            opacity: 1,
            rotateWithView: !1,
            rotation: 0,
            scale: 1,
            snapToPixel: m(b.snapToPixel) ? b.snapToPixel : !0
        })
    }

    v(ql, vj);
    l = ql.prototype;
    l.wb = function () {
        return this.g
    };
    l.Ck = function () {
        return this.c
    };
    l.Id = function () {
        return this.e
    };
    l.Bb = function () {
        return this.a
    };
    l.Oc = function () {
        return 2
    };
    l.jd = function () {
        return this.n
    };
    l.Cb = function () {
        return this.q
    };
    l.Dk = function () {
        return this.b
    };
    l.gb = function () {
        return this.D
    };
    l.Ek = function () {
        return this.d
    };
    l.we = ca;
    l.load = ca;
    l.Oe = ca;
    l.Rf = function (b, c, d, e) {
        c.setTransform(1, 0, 0, 1, 0, 0);
        c.translate(d, e);
        c.beginPath();
        c.arc(b.size / 2, b.size / 2, this.b, 0, 2 * Math.PI, !0);
        null !== this.c && (c.fillStyle = sg(this.c.a), c.fill());
        null !== this.d && (c.strokeStyle = b.strokeStyle, c.lineWidth = b.Tc, null === b.lineDash || c.setLineDash(b.lineDash), c.stroke());
        c.closePath()
    };
    l.Qf = function (b, c, d, e) {
        c.setTransform(1, 0, 0, 1, 0, 0);
        c.translate(d, e);
        c.beginPath();
        c.arc(b.size / 2, b.size / 2, this.b, 0, 2 * Math.PI, !0);
        c.fillStyle = ll;
        c.fill();
        null !== this.d && (c.strokeStyle = b.strokeStyle, c.lineWidth = b.Tc, null === b.lineDash || c.setLineDash(b.lineDash), c.stroke());
        c.closePath()
    };
    l.xb = function () {
        var b = null === this.d ? "-" : this.d.xb(), c = null === this.c ? "-" : this.c.xb();
        if (null === this.f || b != this.f[1] || c != this.f[2] || this.b != this.f[3]) this.f = ["c" + b + c + (m(this.b) ? this.b.toString() : "-"), b, c, this.b];
        return this.f[0]
    };

    function rl(b) {
        b = m(b) ? b : {};
        this.g = null;
        this.c = sl;
        m(b.geometry) && this.Uf(b.geometry);
        this.f = m(b.fill) ? b.fill : null;
        this.e = m(b.image) ? b.image : null;
        this.b = m(b.stroke) ? b.stroke : null;
        this.d = m(b.text) ? b.text : null;
        this.a = b.zIndex
    }

    l = rl.prototype;
    l.R = function () {
        return this.g
    };
    l.Fh = function () {
        return this.c
    };
    l.Qk = function () {
        return this.f
    };
    l.Rk = function () {
        return this.e
    };
    l.Sk = function () {
        return this.b
    };
    l.Tk = function () {
        return this.d
    };
    l.gi = function () {
        return this.a
    };
    l.Uf = function (b) {
        ka(b) ? this.c = b : ia(b) ? this.c = function (c) {
            return c.get(b)
        } : null === b ? this.c = sl : m(b) && (this.c = function () {
            return b
        });
        this.g = b
    };
    l.hm = function (b) {
        this.a = b
    };

    function tl(b) {
        ka(b) || (b = ga(b) ? b : [b], b = $c(b));
        return b
    }

    function ul() {
        var b = new pl({color: "rgba(255,255,255,0.4)"}), c = new kl({color: "#3399CC", width: 1.25}),
            d = [new rl({image: new ql({fill: b, stroke: c, radius: 5}), fill: b, stroke: c})];
        ul = function () {
            return d
        };
        return d
    }

    function vl() {
        var b = {}, c = [255, 255, 255, 1], d = [0, 153, 255, 1];
        b.Polygon = [new rl({fill: new pl({color: [255, 255, 255, .5]})})];
        b.MultiPolygon = b.Polygon;
        b.LineString = [new rl({stroke: new kl({color: c, width: 5})}), new rl({stroke: new kl({color: d, width: 3})})];
        b.MultiLineString = b.LineString;
        b.Point = [new rl({
            image: new ql({radius: 6, fill: new pl({color: d}), stroke: new kl({color: c, width: 1.5})}),
            zIndex: Infinity
        })];
        b.MultiPoint = b.Point;
        b.GeometryCollection = b.Polygon.concat(b.Point);
        return b
    }

    function sl(b) {
        return b.R()
    };

    function wl(b) {
        var c = m(b) ? b : {};
        b = m(c.condition) ? c.condition : Xj;
        c = m(c.style) ? c.style : new rl({stroke: new kl({color: [0, 0, 255, 1]})});
        dl.call(this, {condition: b, style: c})
    }

    v(wl, dl);
    wl.prototype.g = function () {
        var b = this.j, c = b.a(), d = this.R().J(), e = ke(d), f = b.f(), d = c.j(d, f),
            d = c.constrainResolution(d, 0, void 0);
        Qj(b, c, d, e, 200)
    };

    function xl(b) {
        Nj.call(this, {handleEvent: yl});
        b = m(b) ? b : {};
        this.c = m(b.condition) ? b.condition : gd(Wj, Yj);
        this.f = m(b.pixelDelta) ? b.pixelDelta : 128
    }

    v(xl, Nj);

    function yl(b) {
        var c = !1;
        if ("key" == b.type) {
            var d = b.a.f;
            if (this.c(b) && (40 == d || 37 == d || 39 == d || 38 == d)) {
                var e = b.map, c = e.a(), f = Xe(c), g = f.resolution * this.f, h = 0, k = 0;
                40 == d ? k = -g : 37 == d ? h = -g : 39 == d ? h = g : k = g;
                d = [h, k];
                Ad(d, f.rotation);
                f = c.b();
                m(f) && (m(100) && e.La(df({
                    source: f,
                    duration: 100,
                    easing: bf
                })), e = c.i([f[0] + d[0], f[1] + d[1]]), c.Ha(e));
                b.preventDefault();
                c = !0
            }
        }
        return !c
    };

    function zl(b) {
        Nj.call(this, {handleEvent: Al});
        b = m(b) ? b : {};
        this.f = m(b.condition) ? b.condition : Yj;
        this.c = m(b.delta) ? b.delta : 1;
        this.e = m(b.duration) ? b.duration : 100
    }

    v(zl, Nj);

    function Al(b) {
        var c = !1;
        if ("key" == b.type) {
            var d = b.a.i;
            if (this.f(b) && (43 == d || 45 == d)) {
                c = b.map;
                d = 43 == d ? this.c : -this.c;
                c.render();
                var e = c.a();
                Pj(c, e, d, void 0, this.e);
                b.preventDefault();
                c = !0
            }
        }
        return !c
    };

    function Bl(b) {
        Nj.call(this, {handleEvent: Cl});
        b = m(b) ? b : {};
        this.c = 0;
        this.q = m(b.duration) ? b.duration : 250;
        this.e = null;
        this.g = this.f = void 0
    }

    v(Bl, Nj);

    function Cl(b) {
        var c = !1;
        if ("mousewheel" == b.type) {
            var c = b.map, d = b.a;
            this.e = b.coordinate;
            this.c += d.q;
            m(this.f) || (this.f = ta());
            d = Math.max(80 - (ta() - this.f), 0);
            ba.clearTimeout(this.g);
            this.g = ba.setTimeout(ra(this.i, this, c), d);
            b.preventDefault();
            c = !0
        }
        return !c
    }

    Bl.prototype.i = function (b) {
        var c = Vb(this.c, -1, 1), d = b.a();
        b.render();
        Pj(b, d, -c, this.e, this.q);
        this.c = 0;
        this.e = null;
        this.g = this.f = void 0
    };

    function Dl(b) {
        ak.call(this, {handleDownEvent: El, handleDragEvent: Fl, handleUpEvent: Gl});
        b = m(b) ? b : {};
        this.e = null;
        this.g = void 0;
        this.c = !1;
        this.i = 0;
        this.q = m(b.threshold) ? b.threshold : .3
    }

    v(Dl, ak);

    function Fl(b) {
        var c = 0, d = this.f[0], e = this.f[1], d = Math.atan2(e.clientY - d.clientY, e.clientX - d.clientX);
        m(this.g) && (c = d - this.g, this.i += c, !this.c && Math.abs(this.i) > this.q && (this.c = !0));
        this.g = d;
        b = b.map;
        d = Jg(b.b);
        e = ck(this.f);
        e[0] -= d.x;
        e[1] -= d.y;
        this.e = b.ra(e);
        this.c && (d = b.a(), e = d.c(), b.render(), Oj(b, d, e + c, this.e))
    }

    function Gl(b) {
        if (2 > this.f.length) {
            b = b.map;
            var c = b.a();
            Ze(c, -1);
            if (this.c) {
                var d = c.c(), e = this.e, d = c.constrainRotation(d, 0);
                Oj(b, c, d, e, 250)
            }
            return !1
        }
        return !0
    }

    function El(b) {
        return 2 <= this.f.length ? (b = b.map, this.e = null, this.g = void 0, this.c = !1, this.i = 0, this.o || Ze(b.a(), 1), b.render(), !0) : !1
    }

    Dl.prototype.p = ad;

    function Hl(b) {
        ak.call(this, {handleDownEvent: Il, handleDragEvent: Jl, handleUpEvent: Kl});
        b = m(b) ? b : {};
        this.e = null;
        this.i = m(b.duration) ? b.duration : 400;
        this.c = void 0;
        this.g = 1
    }

    v(Hl, ak);

    function Jl(b) {
        var c = 1, d = this.f[0], e = this.f[1], f = d.clientX - e.clientX, d = d.clientY - e.clientY,
            f = Math.sqrt(f * f + d * d);
        m(this.c) && (c = this.c / f);
        this.c = f;
        1 != c && (this.g = c);
        b = b.map;
        var f = b.a(), d = f.a(), e = Jg(b.b), g = ck(this.f);
        g[0] -= e.x;
        g[1] -= e.y;
        this.e = b.ra(g);
        b.render();
        Qj(b, f, d * c, this.e)
    }

    function Kl(b) {
        if (2 > this.f.length) {
            b = b.map;
            var c = b.a();
            Ze(c, -1);
            var d = c.a(), e = this.e, f = this.i, d = c.constrainResolution(d, 0, this.g - 1);
            Qj(b, c, d, e, f);
            return !1
        }
        return !0
    }

    function Il(b) {
        return 2 <= this.f.length ? (b = b.map, this.e = null, this.c = void 0, this.g = 1, this.o || Ze(b.a(), 1), b.render(), !0) : !1
    }

    Hl.prototype.p = ad;

    function Ll(b) {
        b = m(b) ? b : {};
        var c = new lg, d = new Kj(-.005, .05, 100);
        (m(b.altShiftDragRotate) ? b.altShiftDragRotate : 1) && c.push(new hk);
        (m(b.doubleClickZoom) ? b.doubleClickZoom : 1) && c.push(new Rj({
            delta: b.zoomDelta,
            duration: b.zoomDuration
        }));
        (m(b.dragPan) ? b.dragPan : 1) && c.push(new dk({kinetic: d}));
        (m(b.pinchRotate) ? b.pinchRotate : 1) && c.push(new Dl);
        (m(b.pinchZoom) ? b.pinchZoom : 1) && c.push(new Hl({duration: b.zoomDuration}));
        if (m(b.keyboard) ? b.keyboard : 1) c.push(new xl), c.push(new zl({
            delta: b.zoomDelta,
            duration: b.zoomDuration
        }));
        (m(b.mouseWheelZoom) ? b.mouseWheelZoom : 1) && c.push(new Bl({duration: b.zoomDuration}));
        (m(b.shiftDragZoom) ? b.shiftDragZoom : 1) && c.push(new wl);
        return c
    };

    function G(b) {
        var c = m(b) ? b : {};
        b = Ab(c);
        delete b.layers;
        c = c.layers;
        C.call(this, b);
        this.a = null;
        w(this, ud("layers"), this.Ai, !1, this);
        null != c ? ga(c) && (c = new lg(c.slice())) : c = new lg;
        this.r(c)
    }

    v(G, C);
    l = G.prototype;
    l.tf = function () {
        this.b() && this.l()
    };
    l.Ai = function () {
        null !== this.a && (Pa(qb(this.a), Wc), this.a = null);
        var b = this.$b();
        if (null != b) {
            this.a = {add: w(b, "add", this.zi, !1, this), remove: w(b, "remove", this.Bi, !1, this)};
            var b = b.a, c, d, e;
            c = 0;
            for (d = b.length; c < d; c++) e = b[c], this.a[ma(e).toString()] = w(e, ["propertychange", "change"], this.tf, !1, this)
        }
        this.l()
    };
    l.zi = function (b) {
        b = b.element;
        this.a[ma(b).toString()] = w(b, ["propertychange", "change"], this.tf, !1, this);
        this.l()
    };
    l.Bi = function (b) {
        b = ma(b.element).toString();
        Wc(this.a[b]);
        delete this.a[b];
        this.l()
    };
    l.$b = function () {
        return this.get("layers")
    };
    G.prototype.getLayers = G.prototype.$b;
    G.prototype.r = function (b) {
        this.set("layers", b)
    };
    G.prototype.setLayers = G.prototype.r;
    G.prototype.Xa = function (b) {
        var c = m(b) ? b : [], d = c.length;
        this.$b().forEach(function (b) {
            b.Xa(c)
        });
        b = Mi(this);
        var e, f;
        for (e = c.length; d < e; d++) f = c[d], f.brightness = Vb(f.brightness + b.brightness, -1, 1), f.contrast *= b.contrast, f.hue += b.hue, f.opacity *= b.opacity, f.saturation *= b.saturation, f.visible = f.visible && b.visible, f.maxResolution = Math.min(f.maxResolution, b.maxResolution), f.minResolution = Math.max(f.minResolution, b.minResolution), m(b.extent) && (f.extent = m(f.extent) ? oe(f.extent, b.extent) : b.extent);
        return c
    };
    G.prototype.kb = function () {
        return "ready"
    };

    function Ml(b) {
        xe.call(this, {code: b, units: "m", extent: Nl, global: !0, worldExtent: Ol})
    }

    v(Ml, xe);
    Ml.prototype.re = function (b, c) {
        var d = c[1] / 6378137;
        return b / ((Math.exp(d) + Math.exp(-d)) / 2)
    };
    var Pl = 6378137 * Math.PI, Nl = [-Pl, -Pl, Pl, Pl], Ol = [-180, -85, 180, 85],
        Ie = Ra("EPSG:3857 EPSG:102100 EPSG:102113 EPSG:900913 urn:ogc:def:crs:EPSG:6.18:3:3857 urn:ogc:def:crs:EPSG::3857 http://www.opengis.net/gml/srs/epsg.xml#3857".split(" "), function (b) {
            return new Ml(b)
        });

    function Je(b, c, d) {
        var e = b.length;
        d = 1 < d ? d : 2;
        m(c) || (2 < d ? c = b.slice() : c = Array(e));
        for (var f = 0; f < e; f += d) c[f] = 6378137 * Math.PI * b[f] / 180, c[f + 1] = 6378137 * Math.log(Math.tan(Math.PI * (b[f + 1] + 90) / 360));
        return c
    }

    function Ke(b, c, d) {
        var e = b.length;
        d = 1 < d ? d : 2;
        m(c) || (2 < d ? c = b.slice() : c = Array(e));
        for (var f = 0; f < e; f += d) c[f] = 180 * b[f] / (6378137 * Math.PI), c[f + 1] = 360 * Math.atan(Math.exp(b[f + 1] / 6378137)) / Math.PI - 90;
        return c
    };

    function Ql(b, c) {
        xe.call(this, {code: b, units: "degrees", extent: Rl, axisOrientation: c, global: !0, worldExtent: Rl})
    }

    v(Ql, xe);
    Ql.prototype.re = function (b) {
        return b
    };
    var Rl = [-180, -90, 180, 90],
        Le = [new Ql("CRS:84"), new Ql("EPSG:4326", "neu"), new Ql("urn:ogc:def:crs:EPSG::4326", "neu"), new Ql("urn:ogc:def:crs:EPSG:6.6:4326", "neu"), new Ql("urn:ogc:def:crs:OGC:1.3:CRS84"), new Ql("urn:ogc:def:crs:OGC:2:84"), new Ql("http://www.opengis.net/gml/srs/epsg.xml#4326", "neu"), new Ql("urn:x-ogc:def:crs:EPSG:4326", "neu")];

    function Sl() {
        De(Ie);
        De(Le);
        He()
    };

    function H(b) {
        E.call(this, m(b) ? b : {})
    }

    v(H, E);

    function I(b) {
        b = m(b) ? b : {};
        var c = Ab(b);
        delete c.preload;
        delete c.useInterimTilesOnError;
        E.call(this, c);
        this.ia(m(b.preload) ? b.preload : 0);
        this.ka(m(b.useInterimTilesOnError) ? b.useInterimTilesOnError : !0)
    }

    v(I, E);
    I.prototype.r = function () {
        return this.get("preload")
    };
    I.prototype.getPreload = I.prototype.r;
    I.prototype.ia = function (b) {
        this.set("preload", b)
    };
    I.prototype.setPreload = I.prototype.ia;
    I.prototype.ea = function () {
        return this.get("useInterimTilesOnError")
    };
    I.prototype.getUseInterimTilesOnError = I.prototype.ea;
    I.prototype.ka = function (b) {
        this.set("useInterimTilesOnError", b)
    };
    I.prototype.setUseInterimTilesOnError = I.prototype.ka;

    function J(b) {
        b = m(b) ? b : {};
        var c = Ab(b);
        delete c.style;
        delete c.renderBuffer;
        delete c.updateWhileAnimating;
        E.call(this, c);
        this.ea = m(b.renderBuffer) ? b.renderBuffer : 100;
        this.vc = null;
        this.r = void 0;
        this.ka(b.style);
        this.Ac = m(b.updateWhileAnimating) ? b.updateWhileAnimating : !1
    }

    v(J, E);
    J.prototype.af = function () {
        return this.vc
    };
    J.prototype.df = function () {
        return this.r
    };
    J.prototype.ka = function (b) {
        this.vc = m(b) ? b : ul;
        this.r = null === b ? void 0 : tl(this.vc);
        this.l()
    };

    function Tl(b, c, d, e, f) {
        this.o = {};
        this.b = b;
        this.r = c;
        this.f = d;
        this.H = e;
        this.kb = f;
        this.e = this.a = this.d = this.fa = this.pa = this.oa = null;
        this.ea = this.Da = this.q = this.W = this.S = this.N = 0;
        this.ia = !1;
        this.g = this.ka = 0;
        this.va = !1;
        this.ca = 0;
        this.c = "";
        this.i = this.D = this.Fa = this.Ea = 0;
        this.da = this.j = this.n = null;
        this.p = [];
        this.Xa = Hd()
    }

    function Ul(b, c, d) {
        if (null !== b.e) {
            c = mk(c, 0, d, 2, b.H, b.p);
            d = b.b;
            var e = b.Xa, f = d.globalAlpha;
            1 != b.q && (d.globalAlpha = f * b.q);
            var g = b.ka;
            b.ia && (g += b.kb);
            var h, k;
            h = 0;
            for (k = c.length; h < k; h += 2) {
                var n = c[h] - b.N, p = c[h + 1] - b.S;
                b.va && (n = n + .5 | 0, p = p + .5 | 0);
                if (0 !== g || 1 != b.g) {
                    var q = n + b.N, r = p + b.S;
                    hj(e, q, r, b.g, b.g, g, -q, -r);
                    d.setTransform(e[0], e[1], e[4], e[5], e[12], e[13])
                }
                d.drawImage(b.e, b.Da, b.ea, b.ca, b.W, n, p, b.ca, b.W)
            }
            0 === g && 1 == b.g || d.setTransform(1, 0, 0, 1, 0, 0);
            1 != b.q && (d.globalAlpha = f)
        }
    }

    function Vl(b, c, d, e) {
        var f = 0;
        if (null !== b.da && "" !== b.c) {
            null === b.n || Wl(b, b.n);
            null === b.j || Yl(b, b.j);
            var g = b.da, h = b.b, k = b.fa;
            null === k ? (h.font = g.font, h.textAlign = g.textAlign, h.textBaseline = g.textBaseline, b.fa = {
                font: g.font,
                textAlign: g.textAlign,
                textBaseline: g.textBaseline
            }) : (k.font != g.font && (k.font = h.font = g.font), k.textAlign != g.textAlign && (k.textAlign = h.textAlign = g.textAlign), k.textBaseline != g.textBaseline && (k.textBaseline = h.textBaseline = g.textBaseline));
            c = mk(c, f, d, e, b.H, b.p);
            for (g = b.b; f < d; f += e) {
                h = c[f] +
                    b.Ea;
                k = c[f + 1] + b.Fa;
                if (0 !== b.D || 1 != b.i) {
                    var n = hj(b.Xa, h, k, b.i, b.i, b.D, -h, -k);
                    g.setTransform(n[0], n[1], n[4], n[5], n[12], n[13])
                }
                null === b.j || g.strokeText(b.c, h, k);
                null === b.n || g.fillText(b.c, h, k)
            }
            0 === b.D && 1 == b.i || g.setTransform(1, 0, 0, 1, 0, 0)
        }
    }

    function Zl(b, c, d, e, f, g) {
        var h = b.b;
        b = mk(c, d, e, f, b.H, b.p);
        h.moveTo(b[0], b[1]);
        for (c = 2; c < b.length; c += 2) h.lineTo(b[c], b[c + 1]);
        g && h.lineTo(b[0], b[1]);
        return e
    }

    function $l(b, c, d, e, f) {
        var g = b.b, h, k;
        h = 0;
        for (k = e.length; h < k; ++h) d = Zl(b, c, d, e[h], f, !0), g.closePath();
        return d
    }

    l = Tl.prototype;
    l.jc = function (b, c) {
        var d = b.toString(), e = this.o[d];
        m(e) ? e.push(c) : this.o[d] = [c]
    };
    l.kc = function (b) {
        if (pe(this.f, b.J())) {
            if (null !== this.d || null !== this.a) {
                null === this.d || Wl(this, this.d);
                null === this.a || Yl(this, this.a);
                var c;
                c = b.k;
                c = null === c ? null : mk(c, 0, c.length, b.B, this.H, this.p);
                var d = c[2] - c[0], e = c[3] - c[1], d = Math.sqrt(d * d + e * e), e = this.b;
                e.beginPath();
                e.arc(c[0], c[1], d, 0, 2 * Math.PI);
                null === this.d || e.fill();
                null === this.a || e.stroke()
            }
            "" !== this.c && Vl(this, b.ye(), 2, 2)
        }
    };
    l.me = function (b, c) {
        var d = (0, c.c)(b);
        if (null != d && pe(this.f, d.J())) {
            var e = c.a;
            m(e) || (e = 0);
            this.jc(e, function (b) {
                b.Ba(c.f, c.b);
                b.ib(c.e);
                b.Ca(c.d);
                am[d.O()].call(b, d, null)
            })
        }
    };
    l.ed = function (b, c) {
        var d = b.c, e, f;
        e = 0;
        for (f = d.length; e < f; ++e) {
            var g = d[e];
            am[g.O()].call(this, g, c)
        }
    };
    l.ub = function (b) {
        var c = b.k;
        b = b.B;
        null === this.e || Ul(this, c, c.length);
        "" !== this.c && Vl(this, c, c.length, b)
    };
    l.tb = function (b) {
        var c = b.k;
        b = b.B;
        null === this.e || Ul(this, c, c.length);
        "" !== this.c && Vl(this, c, c.length, b)
    };
    l.Eb = function (b) {
        if (pe(this.f, b.J())) {
            if (null !== this.a) {
                Yl(this, this.a);
                var c = this.b, d = b.k;
                c.beginPath();
                Zl(this, d, 0, d.length, b.B, !1);
                c.stroke()
            }
            "" !== this.c && (b = bm(b), Vl(this, b, 2, 2))
        }
    };
    l.lc = function (b) {
        var c = b.J();
        if (pe(this.f, c)) {
            if (null !== this.a) {
                Yl(this, this.a);
                var c = this.b, d = b.k, e = 0, f = b.b, g = b.B;
                c.beginPath();
                var h, k;
                h = 0;
                for (k = f.length; h < k; ++h) e = Zl(this, d, e, f[h], g, !1);
                c.stroke()
            }
            "" !== this.c && (b = cm(b), Vl(this, b, b.length, 2))
        }
    };
    l.Rb = function (b) {
        if (pe(this.f, b.J())) {
            if (null !== this.a || null !== this.d) {
                null === this.d || Wl(this, this.d);
                null === this.a || Yl(this, this.a);
                var c = this.b;
                c.beginPath();
                $l(this, Wk(b), 0, b.b, b.B);
                null === this.d || c.fill();
                null === this.a || c.stroke()
            }
            "" !== this.c && (b = Xk(b), Vl(this, b, 2, 2))
        }
    };
    l.mc = function (b) {
        if (pe(this.f, b.J())) {
            if (null !== this.a || null !== this.d) {
                null === this.d || Wl(this, this.d);
                null === this.a || Yl(this, this.a);
                var c = this.b, d = dm(b), e = 0, f = b.b, g = b.B, h, k;
                h = 0;
                for (k = f.length; h < k; ++h) {
                    var n = f[h];
                    c.beginPath();
                    e = $l(this, d, e, n, g);
                    null === this.d || c.fill();
                    null === this.a || c.stroke()
                }
            }
            "" !== this.c && (b = em(b), Vl(this, b, b.length, 2))
        }
    };

    function fm(b) {
        var c = Ra(rb(b.o), Number);
        cb(c);
        var d, e, f, g, h;
        d = 0;
        for (e = c.length; d < e; ++d) for (f = b.o[c[d].toString()], g = 0, h = f.length; g < h; ++g) f[g](b)
    }

    function Wl(b, c) {
        var d = b.b, e = b.oa;
        null === e ? (d.fillStyle = c.fillStyle, b.oa = {fillStyle: c.fillStyle}) : e.fillStyle != c.fillStyle && (e.fillStyle = d.fillStyle = c.fillStyle)
    }

    function Yl(b, c) {
        var d = b.b, e = b.pa;
        null === e ? (d.lineCap = c.lineCap, Yf && d.setLineDash(c.lineDash), d.lineJoin = c.lineJoin, d.lineWidth = c.lineWidth, d.miterLimit = c.miterLimit, d.strokeStyle = c.strokeStyle, b.pa = {
            lineCap: c.lineCap,
            lineDash: c.lineDash,
            lineJoin: c.lineJoin,
            lineWidth: c.lineWidth,
            miterLimit: c.miterLimit,
            strokeStyle: c.strokeStyle
        }) : (e.lineCap != c.lineCap && (e.lineCap = d.lineCap = c.lineCap), Yf && !eb(e.lineDash, c.lineDash) && d.setLineDash(e.lineDash = c.lineDash), e.lineJoin != c.lineJoin && (e.lineJoin = d.lineJoin =
            c.lineJoin), e.lineWidth != c.lineWidth && (e.lineWidth = d.lineWidth = c.lineWidth), e.miterLimit != c.miterLimit && (e.miterLimit = d.miterLimit = c.miterLimit), e.strokeStyle != c.strokeStyle && (e.strokeStyle = d.strokeStyle = c.strokeStyle))
    }

    l.Ba = function (b, c) {
        if (null === b) this.d = null; else {
            var d = b.a;
            this.d = {fillStyle: sg(null === d ? ll : d)}
        }
        if (null === c) this.a = null; else {
            var d = c.a, e = c.c, f = c.b, g = c.f, h = c.d, k = c.e;
            this.a = {
                lineCap: m(e) ? e : "round",
                lineDash: null != f ? f : ml,
                lineJoin: m(g) ? g : "round",
                lineWidth: this.r * (m(h) ? h : 1),
                miterLimit: m(k) ? k : 10,
                strokeStyle: sg(null === d ? nl : d)
            }
        }
    };
    l.ib = function (b) {
        if (null === b) this.e = null; else {
            var c = b.wb(), d = b.Bb(1), e = b.Cb(), f = b.gb();
            this.N = c[0];
            this.S = c[1];
            this.W = f[1];
            this.e = d;
            this.q = b.o;
            this.Da = e[0];
            this.ea = e[1];
            this.ia = b.p;
            this.ka = b.i;
            this.g = b.j;
            this.va = b.r;
            this.ca = f[0]
        }
    };
    l.Ca = function (b) {
        if (null === b) this.c = ""; else {
            var c = b.a;
            null === c ? this.n = null : (c = c.a, this.n = {fillStyle: sg(null === c ? ll : c)});
            var d = b.e;
            if (null === d) this.j = null; else {
                var c = d.a, e = d.c, f = d.b, g = d.f, h = d.d, d = d.e;
                this.j = {
                    lineCap: m(e) ? e : "round",
                    lineDash: null != f ? f : ml,
                    lineJoin: m(g) ? g : "round",
                    lineWidth: m(h) ? h : 1,
                    miterLimit: m(d) ? d : 10,
                    strokeStyle: sg(null === c ? nl : c)
                }
            }
            var c = b.c, e = b.i, f = b.j, g = b.f, h = b.d, d = b.b, k = b.g;
            b = b.n;
            this.da = {
                font: m(c) ? c : "10px sans-serif",
                textAlign: m(k) ? k : "center",
                textBaseline: m(b) ? b : "middle"
            };
            this.c =
                m(d) ? d : "";
            this.Ea = m(e) ? this.r * e : 0;
            this.Fa = m(f) ? this.r * f : 0;
            this.D = m(g) ? g : 0;
            this.i = this.r * (m(h) ? h : 1)
        }
    };
    var am = {
        Point: Tl.prototype.ub,
        LineString: Tl.prototype.Eb,
        Polygon: Tl.prototype.Rb,
        MultiPoint: Tl.prototype.tb,
        MultiLineString: Tl.prototype.lc,
        MultiPolygon: Tl.prototype.mc,
        GeometryCollection: Tl.prototype.ed,
        Circle: Tl.prototype.kc
    };
    var gm = ["Polygon", "LineString", "Image", "Text"];

    function hm(b, c, d) {
        this.fa = b;
        this.ca = c;
        this.c = null;
        this.f = 0;
        this.resolution = d;
        this.S = this.N = null;
        this.d = [];
        this.coordinates = [];
        this.oa = Hd();
        this.a = [];
        this.da = [];
        this.pa = Hd()
    }

    function im(b, c, d, e, f, g) {
        var h = b.coordinates.length, k = b.pe(), n = [c[d], c[d + 1]], p = [NaN, NaN], q = !0, r, s, u;
        for (r = d + f; r < e; r += f) p[0] = c[r], p[1] = c[r + 1], u = ae(k, p), u !== s ? (q && (b.coordinates[h++] = n[0], b.coordinates[h++] = n[1]), b.coordinates[h++] = p[0], b.coordinates[h++] = p[1], q = !1) : 1 === u ? (b.coordinates[h++] = p[0], b.coordinates[h++] = p[1], q = !1) : q = !0, n[0] = p[0], n[1] = p[1], s = u;
        r === d + f && (b.coordinates[h++] = n[0], b.coordinates[h++] = n[1]);
        g && (b.coordinates[h++] = c[d], b.coordinates[h++] = c[d + 1]);
        return h
    }

    function jm(b, c) {
        b.N = [0, c, 0];
        b.d.push(b.N);
        b.S = [0, c, 0];
        b.a.push(b.S)
    }

    function km(b, c, d, e, f, g, h, k, n) {
        var p;
        ij(e, b.oa) ? p = b.da : (p = mk(b.coordinates, 0, b.coordinates.length, 2, e, b.da), Kd(b.oa, e));
        e = 0;
        var q = h.length, r = 0, s;
        for (b = b.pa; e < q;) {
            var u = h[e], z, y, A, D;
            switch (u[0]) {
                case 0:
                    r = u[1];
                    s = ma(r).toString();
                    m(g[s]) ? e = u[2] : m(n) && !pe(n, r.R().J()) ? e = u[2] : ++e;
                    break;
                case 1:
                    c.beginPath();
                    ++e;
                    break;
                case 2:
                    r = u[1];
                    s = p[r];
                    var x = p[r + 1], M = p[r + 2] - s, r = p[r + 3] - x;
                    c.arc(s, x, Math.sqrt(M * M + r * r), 0, 2 * Math.PI, !0);
                    ++e;
                    break;
                case 3:
                    c.closePath();
                    ++e;
                    break;
                case 4:
                    r = u[1];
                    s = u[2];
                    z = u[3];
                    A = u[4] * d;
                    var Q = u[5] *
                        d, O = u[6];
                    y = u[7];
                    var W = u[8], Ja = u[9], x = u[11], M = u[12], lb = u[13], Ka = u[14];
                    for (u[10] && (x += f); r < s; r += 2) {
                        u = p[r] - A;
                        D = p[r + 1] - Q;
                        lb && (u = u + .5 | 0, D = D + .5 | 0);
                        if (1 != M || 0 !== x) {
                            var mb = u + A, Pb = D + Q;
                            hj(b, mb, Pb, M, M, x, -mb, -Pb);
                            c.setTransform(b[0], b[1], b[4], b[5], b[12], b[13])
                        }
                        mb = c.globalAlpha;
                        1 != y && (c.globalAlpha = mb * y);
                        c.drawImage(z, W, Ja, Ka, O, u, D, Ka * d, O * d);
                        1 != y && (c.globalAlpha = mb);
                        1 == M && 0 === x || c.setTransform(1, 0, 0, 1, 0, 0)
                    }
                    ++e;
                    break;
                case 5:
                    r = u[1];
                    s = u[2];
                    A = u[3];
                    Q = u[4] * d;
                    O = u[5] * d;
                    x = u[6];
                    M = u[7] * d;
                    z = u[8];
                    for (y = u[9]; r < s; r += 2) {
                        u = p[r] +
                            Q;
                        D = p[r + 1] + O;
                        if (1 != M || 0 !== x) hj(b, u, D, M, M, x, -u, -D), c.setTransform(b[0], b[1], b[4], b[5], b[12], b[13]);
                        y && c.strokeText(A, u, D);
                        z && c.fillText(A, u, D);
                        1 == M && 0 === x || c.setTransform(1, 0, 0, 1, 0, 0)
                    }
                    ++e;
                    break;
                case 6:
                    if (m(k) && (r = u[1], r = k(r))) return r;
                    ++e;
                    break;
                case 7:
                    c.fill();
                    ++e;
                    break;
                case 8:
                    r = u[1];
                    s = u[2];
                    c.moveTo(p[r], p[r + 1]);
                    for (r += 2; r < s; r += 2) c.lineTo(p[r], p[r + 1]);
                    ++e;
                    break;
                case 9:
                    c.fillStyle = u[1];
                    ++e;
                    break;
                case 10:
                    r = m(u[7]) ? u[7] : !0;
                    s = u[2];
                    c.strokeStyle = u[1];
                    c.lineWidth = r ? s * d : s;
                    c.lineCap = u[3];
                    c.lineJoin = u[4];
                    c.miterLimit =
                        u[5];
                    Yf && c.setLineDash(u[6]);
                    ++e;
                    break;
                case 11:
                    c.font = u[1];
                    c.textAlign = u[2];
                    c.textBaseline = u[3];
                    ++e;
                    break;
                case 12:
                    c.stroke();
                    ++e;
                    break;
                default:
                    ++e
            }
        }
    }

    hm.prototype.ac = function (b, c, d, e, f) {
        km(this, b, c, d, e, f, this.d, void 0)
    };

    function lm(b) {
        var c = b.a;
        c.reverse();
        var d, e = c.length, f, g, h = -1;
        for (d = 0; d < e; ++d) if (f = c[d], g = f[0], 6 == g) h = d; else if (0 == g) {
            f[2] = d;
            f = b.a;
            for (g = d; h < g;) {
                var k = f[h];
                f[h] = f[g];
                f[g] = k;
                ++h;
                --g
            }
            h = -1
        }
    }

    function mm(b, c) {
        b.N[2] = b.d.length;
        b.N = null;
        b.S[2] = b.a.length;
        b.S = null;
        var d = [6, c];
        b.d.push(d);
        b.a.push(d)
    }

    hm.prototype.Kb = ca;
    hm.prototype.pe = function () {
        return this.ca
    };

    function nm(b, c, d) {
        hm.call(this, b, c, d);
        this.n = this.W = null;
        this.H = this.D = this.r = this.p = this.o = this.q = this.j = this.i = this.g = this.e = this.b = void 0
    }

    v(nm, hm);
    nm.prototype.ub = function (b, c) {
        if (null !== this.n) {
            jm(this, c);
            var d = b.k, e = this.coordinates.length, d = im(this, d, 0, d.length, b.B, !1);
            this.d.push([4, e, d, this.n, this.b, this.e, this.g, this.i, this.j, this.q, this.o, this.p, this.r, this.D, this.H]);
            this.a.push([4, e, d, this.W, this.b, this.e, this.g, this.i, this.j, this.q, this.o, this.p, this.r, this.D, this.H]);
            mm(this, c)
        }
    };
    nm.prototype.tb = function (b, c) {
        if (null !== this.n) {
            jm(this, c);
            var d = b.k, e = this.coordinates.length, d = im(this, d, 0, d.length, b.B, !1);
            this.d.push([4, e, d, this.n, this.b, this.e, this.g, this.i, this.j, this.q, this.o, this.p, this.r, this.D, this.H]);
            this.a.push([4, e, d, this.W, this.b, this.e, this.g, this.i, this.j, this.q, this.o, this.p, this.r, this.D, this.H]);
            mm(this, c)
        }
    };
    nm.prototype.Kb = function () {
        lm(this);
        this.e = this.b = void 0;
        this.n = this.W = null;
        this.H = this.D = this.p = this.o = this.q = this.j = this.i = this.r = this.g = void 0
    };
    nm.prototype.ib = function (b) {
        var c = b.wb(), d = b.gb(), e = b.Id(1), f = b.Bb(1), g = b.Cb();
        this.b = c[0];
        this.e = c[1];
        this.W = e;
        this.n = f;
        this.g = d[1];
        this.i = b.o;
        this.j = g[0];
        this.q = g[1];
        this.o = b.p;
        this.p = b.i;
        this.r = b.j;
        this.D = b.r;
        this.H = d[0]
    };

    function om(b, c, d) {
        hm.call(this, b, c, d);
        this.b = {
            Ic: void 0,
            Dc: void 0,
            Ec: null,
            Fc: void 0,
            Gc: void 0,
            Hc: void 0,
            ve: 0,
            strokeStyle: void 0,
            lineCap: void 0,
            lineDash: null,
            lineJoin: void 0,
            lineWidth: void 0,
            miterLimit: void 0
        }
    }

    v(om, hm);

    function pm(b, c, d, e, f) {
        var g = b.coordinates.length;
        c = im(b, c, d, e, f, !1);
        g = [8, g, c];
        b.d.push(g);
        b.a.push(g);
        return e
    }

    l = om.prototype;
    l.pe = function () {
        null === this.c && (this.c = Xd(this.ca), 0 < this.f && Wd(this.c, this.resolution * (this.f + 1) / 2, this.c));
        return this.c
    };

    function qm(b) {
        var c = b.b, d = c.strokeStyle, e = c.lineCap, f = c.lineDash, g = c.lineJoin, h = c.lineWidth,
            k = c.miterLimit;
        c.Ic == d && c.Dc == e && eb(c.Ec, f) && c.Fc == g && c.Gc == h && c.Hc == k || (c.ve != b.coordinates.length && (b.d.push([12]), c.ve = b.coordinates.length), b.d.push([10, d, h, e, g, k, f], [1]), c.Ic = d, c.Dc = e, c.Ec = f, c.Fc = g, c.Gc = h, c.Hc = k)
    }

    l.Eb = function (b, c) {
        var d = this.b, e = d.lineWidth;
        m(d.strokeStyle) && m(e) && (qm(this), jm(this, c), this.a.push([10, d.strokeStyle, d.lineWidth, d.lineCap, d.lineJoin, d.miterLimit, d.lineDash], [1]), d = b.k, pm(this, d, 0, d.length, b.B), this.a.push([12]), mm(this, c))
    };
    l.lc = function (b, c) {
        var d = this.b, e = d.lineWidth;
        if (m(d.strokeStyle) && m(e)) {
            qm(this);
            jm(this, c);
            this.a.push([10, d.strokeStyle, d.lineWidth, d.lineCap, d.lineJoin, d.miterLimit, d.lineDash], [1]);
            var d = b.b, e = b.k, f = b.B, g = 0, h, k;
            h = 0;
            for (k = d.length; h < k; ++h) g = pm(this, e, g, d[h], f);
            this.a.push([12]);
            mm(this, c)
        }
    };
    l.Kb = function () {
        this.b.ve != this.coordinates.length && this.d.push([12]);
        lm(this);
        this.b = null
    };
    l.Ba = function (b, c) {
        var d = c.a;
        this.b.strokeStyle = sg(null === d ? nl : d);
        d = c.c;
        this.b.lineCap = m(d) ? d : "round";
        d = c.b;
        this.b.lineDash = null === d ? ml : d;
        d = c.f;
        this.b.lineJoin = m(d) ? d : "round";
        d = c.d;
        this.b.lineWidth = m(d) ? d : 1;
        d = c.e;
        this.b.miterLimit = m(d) ? d : 10;
        this.b.lineWidth > this.f && (this.f = this.b.lineWidth, this.c = null)
    };

    function rm(b, c, d) {
        hm.call(this, b, c, d);
        this.b = {
            gf: void 0,
            Ic: void 0,
            Dc: void 0,
            Ec: null,
            Fc: void 0,
            Gc: void 0,
            Hc: void 0,
            fillStyle: void 0,
            strokeStyle: void 0,
            lineCap: void 0,
            lineDash: null,
            lineJoin: void 0,
            lineWidth: void 0,
            miterLimit: void 0
        }
    }

    v(rm, hm);

    function sm(b, c, d, e, f) {
        var g = b.b, h = [1];
        b.d.push(h);
        b.a.push(h);
        var k, h = 0;
        for (k = e.length; h < k; ++h) {
            var n = e[h], p = b.coordinates.length;
            d = im(b, c, d, n, f, !0);
            d = [8, p, d];
            p = [3];
            b.d.push(d, p);
            b.a.push(d, p);
            d = n
        }
        c = [7];
        b.a.push(c);
        m(g.fillStyle) && b.d.push(c);
        m(g.strokeStyle) && (g = [12], b.d.push(g), b.a.push(g));
        return d
    }

    l = rm.prototype;
    l.kc = function (b, c) {
        var d = this.b, e = d.strokeStyle;
        if (m(d.fillStyle) || m(e)) {
            tm(this);
            jm(this, c);
            this.a.push([9, sg(ll)]);
            m(d.strokeStyle) && this.a.push([10, d.strokeStyle, d.lineWidth, d.lineCap, d.lineJoin, d.miterLimit, d.lineDash]);
            var f = b.k, e = this.coordinates.length;
            im(this, f, 0, f.length, b.B, !1);
            f = [1];
            e = [2, e];
            this.d.push(f, e);
            this.a.push(f, e);
            e = [7];
            this.a.push(e);
            m(d.fillStyle) && this.d.push(e);
            m(d.strokeStyle) && (d = [12], this.d.push(d), this.a.push(d));
            mm(this, c)
        }
    };
    l.Rb = function (b, c) {
        var d = this.b, e = d.strokeStyle;
        if (m(d.fillStyle) || m(e)) tm(this), jm(this, c), this.a.push([9, sg(ll)]), m(d.strokeStyle) && this.a.push([10, d.strokeStyle, d.lineWidth, d.lineCap, d.lineJoin, d.miterLimit, d.lineDash]), d = b.b, e = Wk(b), sm(this, e, 0, d, b.B), mm(this, c)
    };
    l.mc = function (b, c) {
        var d = this.b, e = d.strokeStyle;
        if (m(d.fillStyle) || m(e)) {
            tm(this);
            jm(this, c);
            this.a.push([9, sg(ll)]);
            m(d.strokeStyle) && this.a.push([10, d.strokeStyle, d.lineWidth, d.lineCap, d.lineJoin, d.miterLimit, d.lineDash]);
            var d = b.b, e = dm(b), f = b.B, g = 0, h, k;
            h = 0;
            for (k = d.length; h < k; ++h) g = sm(this, e, g, d[h], f);
            mm(this, c)
        }
    };
    l.Kb = function () {
        lm(this);
        this.b = null;
        var b = this.fa;
        if (0 !== b) {
            var c = this.coordinates, d, e;
            d = 0;
            for (e = c.length; d < e; ++d) c[d] = b * Math.round(c[d] / b)
        }
    };
    l.pe = function () {
        null === this.c && (this.c = Xd(this.ca), 0 < this.f && Wd(this.c, this.resolution * (this.f + 1) / 2, this.c));
        return this.c
    };
    l.Ba = function (b, c) {
        var d = this.b;
        if (null === b) d.fillStyle = void 0; else {
            var e = b.a;
            d.fillStyle = sg(null === e ? ll : e)
        }
        null === c ? (d.strokeStyle = void 0, d.lineCap = void 0, d.lineDash = null, d.lineJoin = void 0, d.lineWidth = void 0, d.miterLimit = void 0) : (e = c.a, d.strokeStyle = sg(null === e ? nl : e), e = c.c, d.lineCap = m(e) ? e : "round", e = c.b, d.lineDash = null === e ? ml : e.slice(), e = c.f, d.lineJoin = m(e) ? e : "round", e = c.d, d.lineWidth = m(e) ? e : 1, e = c.e, d.miterLimit = m(e) ? e : 10, d.lineWidth > this.f && (this.f = d.lineWidth, this.c = null))
    };

    function tm(b) {
        var c = b.b, d = c.fillStyle, e = c.strokeStyle, f = c.lineCap, g = c.lineDash, h = c.lineJoin, k = c.lineWidth,
            n = c.miterLimit;
        m(d) && c.gf != d && (b.d.push([9, d]), c.gf = c.fillStyle);
        !m(e) || c.Ic == e && c.Dc == f && c.Ec == g && c.Fc == h && c.Gc == k && c.Hc == n || (b.d.push([10, e, k, f, h, n, g]), c.Ic = e, c.Dc = f, c.Ec = g, c.Fc = h, c.Gc = k, c.Hc = n)
    }

    function um(b, c, d) {
        hm.call(this, b, c, d);
        this.D = this.r = this.p = null;
        this.n = "";
        this.o = this.q = this.j = this.i = 0;
        this.g = this.e = this.b = null
    }

    v(um, hm);
    um.prototype.vb = function (b, c, d, e, f, g) {
        if ("" !== this.n && null !== this.g && (null !== this.b || null !== this.e)) {
            if (null !== this.b) {
                f = this.b;
                var h = this.p;
                if (null === h || h.fillStyle != f.fillStyle) {
                    var k = [9, f.fillStyle];
                    this.d.push(k);
                    this.a.push(k);
                    null === h ? this.p = {fillStyle: f.fillStyle} : h.fillStyle = f.fillStyle
                }
            }
            null !== this.e && (f = this.e, h = this.r, null === h || h.lineCap != f.lineCap || h.lineDash != f.lineDash || h.lineJoin != f.lineJoin || h.lineWidth != f.lineWidth || h.miterLimit != f.miterLimit || h.strokeStyle != f.strokeStyle) && (k = [10,
                f.strokeStyle, f.lineWidth, f.lineCap, f.lineJoin, f.miterLimit, f.lineDash, !1], this.d.push(k), this.a.push(k), null === h ? this.r = {
                lineCap: f.lineCap,
                lineDash: f.lineDash,
                lineJoin: f.lineJoin,
                lineWidth: f.lineWidth,
                miterLimit: f.miterLimit,
                strokeStyle: f.strokeStyle
            } : (h.lineCap = f.lineCap, h.lineDash = f.lineDash, h.lineJoin = f.lineJoin, h.lineWidth = f.lineWidth, h.miterLimit = f.miterLimit, h.strokeStyle = f.strokeStyle));
            f = this.g;
            h = this.D;
            if (null === h || h.font != f.font || h.textAlign != f.textAlign || h.textBaseline != f.textBaseline) k =
                [11, f.font, f.textAlign, f.textBaseline], this.d.push(k), this.a.push(k), null === h ? this.D = {
                font: f.font,
                textAlign: f.textAlign,
                textBaseline: f.textBaseline
            } : (h.font = f.font, h.textAlign = f.textAlign, h.textBaseline = f.textBaseline);
            jm(this, g);
            f = this.coordinates.length;
            b = im(this, b, c, d, e, !1);
            b = [5, f, b, this.n, this.i, this.j, this.q, this.o, null !== this.b, null !== this.e];
            this.d.push(b);
            this.a.push(b);
            mm(this, g)
        }
    };
    um.prototype.Ca = function (b) {
        if (null === b) this.n = ""; else {
            var c = b.a;
            null === c ? this.b = null : (c = c.a, c = sg(null === c ? ll : c), null === this.b ? this.b = {fillStyle: c} : this.b.fillStyle = c);
            var d = b.e;
            if (null === d) this.e = null; else {
                var c = d.a, e = d.c, f = d.b, g = d.f, h = d.d, d = d.e, e = m(e) ? e : "round",
                    f = null != f ? f.slice() : ml, g = m(g) ? g : "round", h = m(h) ? h : 1, d = m(d) ? d : 10,
                    c = sg(null === c ? nl : c);
                if (null === this.e) this.e = {
                    lineCap: e,
                    lineDash: f,
                    lineJoin: g,
                    lineWidth: h,
                    miterLimit: d,
                    strokeStyle: c
                }; else {
                    var k = this.e;
                    k.lineCap = e;
                    k.lineDash = f;
                    k.lineJoin = g;
                    k.lineWidth =
                        h;
                    k.miterLimit = d;
                    k.strokeStyle = c
                }
            }
            var n = b.c, c = b.i, e = b.j, f = b.f, h = b.d, d = b.b, g = b.g, k = b.n;
            b = m(n) ? n : "10px sans-serif";
            g = m(g) ? g : "center";
            k = m(k) ? k : "middle";
            null === this.g ? this.g = {
                font: b,
                textAlign: g,
                textBaseline: k
            } : (n = this.g, n.font = b, n.textAlign = g, n.textBaseline = k);
            this.n = m(d) ? d : "";
            this.i = m(c) ? c : 0;
            this.j = m(e) ? e : 0;
            this.q = m(f) ? f : 0;
            this.o = m(h) ? h : 1
        }
    };

    function vm(b, c, d, e) {
        this.i = b;
        this.c = c;
        this.n = d;
        this.f = e;
        this.d = {};
        this.e = Nf(1, 1);
        this.g = Hd()
    }

    function wm(b) {
        for (var c in b.d) {
            var d = b.d[c], e;
            for (e in d) d[e].Kb()
        }
    }

    vm.prototype.b = function (b, c, d, e, f) {
        var g = this.g;
        hj(g, .5, .5, 1 / c, -1 / c, -d, -b[0], -b[1]);
        var h = this.e;
        h.clearRect(0, 0, 1, 1);
        var k;
        m(this.f) && (k = Sd(), Td(k, b), Wd(k, c * this.f, k));
        return xm(this, h, g, d, e, function (b) {
            if (0 < h.getImageData(0, 0, 1, 1).data[3]) {
                if (b = f(b)) return b;
                h.clearRect(0, 0, 1, 1)
            }
        }, k)
    };
    vm.prototype.a = function (b, c) {
        var d = m(b) ? b.toString() : "0", e = this.d[d];
        m(e) || (e = {}, this.d[d] = e);
        d = e[c];
        m(d) || (d = new ym[c](this.i, this.c, this.n), e[c] = d);
        return d
    };
    vm.prototype.la = function () {
        return vb(this.d)
    };

    function zm(b, c, d, e, f, g) {
        var h = Ra(rb(b.d), Number);
        cb(h);
        var k = b.c, n = k[0], p = k[1], q = k[2], k = k[3], n = [n, p, n, k, q, k, q, p];
        mk(n, 0, 8, 2, e, n);
        c.save();
        c.beginPath();
        c.moveTo(n[0], n[1]);
        c.lineTo(n[2], n[3]);
        c.lineTo(n[4], n[5]);
        c.lineTo(n[6], n[7]);
        c.closePath();
        c.clip();
        for (var r, s, n = 0, p = h.length; n < p; ++n) for (r = b.d[h[n].toString()], q = 0, k = gm.length; q < k; ++q) s = r[gm[q]], m(s) && s.ac(c, d, e, f, g);
        c.restore()
    }

    function xm(b, c, d, e, f, g, h) {
        var k = Ra(rb(b.d), Number);
        cb(k, function (b, c) {
            return c - b
        });
        var n, p, q, r, s;
        n = 0;
        for (p = k.length; n < p; ++n) for (r = b.d[k[n].toString()], q = gm.length - 1; 0 <= q; --q) if (s = r[gm[q]], m(s) && (s = km(s, c, 1, d, e, f, s.a, g, h))) return s
    }

    var ym = {Image: nm, LineString: om, Polygon: rm, Text: um};

    function Am(b) {
        lj.call(this, b);
        this.r = Hd()
    }

    v(Am, lj);
    Am.prototype.q = function (b, c, d) {
        Bm(this, "precompose", d, b, void 0);
        var e = this.Gd();
        if (null !== e) {
            var f = c.extent, g = m(f);
            if (g) {
                var h = b.pixelRatio, k = je(f), n = ie(f), p = he(f), f = ge(f);
                kj(b.coordinateToPixelMatrix, k, k);
                kj(b.coordinateToPixelMatrix, n, n);
                kj(b.coordinateToPixelMatrix, p, p);
                kj(b.coordinateToPixelMatrix, f, f);
                d.save();
                d.beginPath();
                d.moveTo(k[0] * h, k[1] * h);
                d.lineTo(n[0] * h, n[1] * h);
                d.lineTo(p[0] * h, p[1] * h);
                d.lineTo(f[0] * h, f[1] * h);
                d.clip()
            }
            h = this.of();
            k = d.globalAlpha;
            d.globalAlpha = c.opacity;
            0 === b.viewState.rotation ?
                (c = h[13], n = e.width * h[0], p = e.height * h[5], d.drawImage(e, 0, 0, +e.width, +e.height, Math.round(h[12]), Math.round(c), Math.round(n), Math.round(p))) : (d.setTransform(h[0], h[1], h[4], h[5], h[12], h[13]), d.drawImage(e, 0, 0), d.setTransform(1, 0, 0, 1, 0, 0));
            d.globalAlpha = k;
            g && d.restore()
        }
        Bm(this, "postcompose", d, b, void 0)
    };

    function Bm(b, c, d, e, f) {
        var g = b.a;
        jd(g, c) && (b = m(f) ? f : Cm(b, e), b = new Tl(d, e.pixelRatio, e.extent, b, e.viewState.rotation), g.dispatchEvent(new Zk(c, g, b, null, e, d, null)), fm(b))
    }

    function Cm(b, c) {
        var d = c.viewState, e = c.pixelRatio;
        return hj(b.r, e * c.size[0] / 2, e * c.size[1] / 2, e / d.resolution, -e / d.resolution, -d.rotation, -d.center[0], -d.center[1])
    }

    function Dm(b, c) {
        var d = [0, 0];
        kj(c, b, d);
        return d
    }

    var Em = function () {
        var b = null, c = null;
        return function (d) {
            if (null === b) {
                b = Nf(1, 1);
                c = b.createImageData(1, 1);
                var e = c.data;
                e[0] = 42;
                e[1] = 84;
                e[2] = 126;
                e[3] = 255
            }
            var e = b.canvas, f = d[0] <= e.width && d[1] <= e.height;
            f || (e.width = d[0], e.height = d[1], e = d[0] - 1, d = d[1] - 1, b.putImageData(c, e, d), d = b.getImageData(e, d, 1, 1), f = eb(c.data, d.data));
            return f
        }
    }();

    function Fm(b, c, d) {
        nk.call(this);
        this.pg(b, m(c) ? c : 0, d)
    }

    v(Fm, nk);
    l = Fm.prototype;
    l.clone = function () {
        var b = new Fm(null);
        pk(b, this.a, this.k.slice());
        b.l();
        return b
    };
    l.Ya = function (b, c, d, e) {
        var f = this.k;
        b -= f[0];
        var g = c - f[1];
        c = b * b + g * g;
        if (c < e) {
            if (0 === c) for (e = 0; e < this.B; ++e) d[e] = f[e]; else for (e = this.Hf() / Math.sqrt(c), d[0] = f[0] + e * b, d[1] = f[1] + e * g, e = 2; e < this.B; ++e) d[e] = f[e];
            d.length = this.B;
            return c
        }
        return e
    };
    l.Jb = function (b, c) {
        var d = this.k, e = b - d[0], d = c - d[1];
        return e * e + d * d <= Gm(this)
    };
    l.ye = function () {
        return this.k.slice(0, this.B)
    };
    l.cd = function (b) {
        var c = this.k, d = c[this.B] - c[0];
        return Vd(c[0] - d, c[1] - d, c[0] + d, c[1] + d, b)
    };
    l.Hf = function () {
        return Math.sqrt(Gm(this))
    };

    function Gm(b) {
        var c = b.k[b.B] - b.k[0];
        b = b.k[b.B + 1] - b.k[1];
        return c * c + b * b
    }

    l.O = function () {
        return "Circle"
    };
    l.Jj = function (b) {
        var c = this.B, d = b.slice();
        d[c] = d[0] + (this.k[c] - this.k[0]);
        var e;
        for (e = 1; e < c; ++e) d[c + e] = b[e];
        pk(this, this.a, d);
        this.l()
    };
    l.pg = function (b, c, d) {
        if (null === b) pk(this, "XY", null); else {
            qk(this, d, b, 0);
            null === this.k && (this.k = []);
            d = this.k;
            b = Ak(d, b);
            d[b++] = d[0] + c;
            var e;
            c = 1;
            for (e = this.B; c < e; ++c) d[b++] = d[c];
            d.length = b
        }
        this.l()
    };
    l.Kj = function (b) {
        this.k[this.B] = this.k[0] + b;
        this.l()
    };

    function Hm(b) {
        lk.call(this);
        this.c = m(b) ? b : null;
        Im(this)
    }

    v(Hm, lk);

    function Jm(b) {
        var c = [], d, e;
        d = 0;
        for (e = b.length; d < e; ++d) c.push(b[d].clone());
        return c
    }

    function Km(b) {
        var c, d;
        if (null !== b.c) for (c = 0, d = b.c.length; c < d; ++c) Vc(b.c[c], "change", b.l, !1, b)
    }

    function Im(b) {
        var c, d;
        if (null !== b.c) for (c = 0, d = b.c.length; c < d; ++c) w(b.c[c], "change", b.l, !1, b)
    }

    l = Hm.prototype;
    l.clone = function () {
        var b = new Hm(null);
        b.qg(this.c);
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        var f = this.c, g, h;
        g = 0;
        for (h = f.length; g < h; ++g) e = f[g].Ya(b, c, d, e);
        return e
    };
    l.Jb = function (b, c) {
        var d = this.c, e, f;
        e = 0;
        for (f = d.length; e < f; ++e) if (d[e].Jb(b, c)) return !0;
        return !1
    };
    l.cd = function (b) {
        Vd(Infinity, Infinity, -Infinity, -Infinity, b);
        for (var c = this.c, d = 0, e = c.length; d < e; ++d) de(b, c[d].J());
        return b
    };
    l.nf = function () {
        return Jm(this.c)
    };
    l.se = function (b) {
        this.i != this.d && (wb(this.e), this.g = 0, this.i = this.d);
        if (0 > b || 0 !== this.g && b < this.g) return this;
        var c = b.toString();
        if (this.e.hasOwnProperty(c)) return this.e[c];
        var d = [], e = this.c, f = !1, g, h;
        g = 0;
        for (h = e.length; g < h; ++g) {
            var k = e[g], n = k.se(b);
            d.push(n);
            n !== k && (f = !0)
        }
        if (f) return b = new Hm(null), Km(b), b.c = d, Im(b), b.l(), this.e[c] = b;
        this.g = b;
        return this
    };
    l.O = function () {
        return "GeometryCollection"
    };
    l.ja = function (b) {
        var c = this.c, d, e;
        d = 0;
        for (e = c.length; d < e; ++d) if (c[d].ja(b)) return !0;
        return !1
    };
    l.la = function () {
        return 0 == this.c.length
    };
    l.qg = function (b) {
        b = Jm(b);
        Km(this);
        this.c = b;
        Im(this);
        this.l()
    };
    l.qa = function (b) {
        var c = this.c, d, e;
        d = 0;
        for (e = c.length; d < e; ++d) c[d].qa(b);
        this.l()
    };
    l.Ia = function (b, c) {
        var d = this.c, e, f;
        e = 0;
        for (f = d.length; e < f; ++e) d[e].Ia(b, c);
        this.l()
    };
    l.P = function () {
        Km(this);
        Hm.T.P.call(this)
    };

    function Lm(b, c, d, e, f) {
        var g = NaN, h = NaN, k = (d - c) / e;
        if (0 !== k) if (1 == k) g = b[c], h = b[c + 1]; else if (2 == k) g = .5 * b[c] + .5 * b[c + e], h = .5 * b[c + 1] + .5 * b[c + e + 1]; else {
            var h = b[c], k = b[c + 1], n = 0, g = [0], p;
            for (p = c + e; p < d; p += e) {
                var q = b[p], r = b[p + 1], n = n + Math.sqrt((q - h) * (q - h) + (r - k) * (r - k));
                g.push(n);
                h = q;
                k = r
            }
            d = .5 * n;
            for (var s, h = db, k = 0, n = g.length; k < n;) p = k + n >> 1, q = h(d, g[p]), 0 < q ? k = p + 1 : (n = p, s = !q);
            s = s ? k : ~k;
            0 > s ? (d = (d - g[-s - 2]) / (g[-s - 1] - g[-s - 2]), c += (-s - 2) * e, g = Xb(b[c], b[c + e], d), h = Xb(b[c + 1], b[c + e + 1], d)) : (g = b[c + s * e], h = b[c + s * e + 1])
        }
        return null != f ?
            (f[0] = g, f[1] = h, f) : [g, h]
    }

    function Mm(b, c, d, e, f, g) {
        if (d == c) return null;
        if (f < b[c + e - 1]) return g ? (d = b.slice(c, c + e), d[e - 1] = f, d) : null;
        if (b[d - 1] < f) return g ? (d = b.slice(d - e, d), d[e - 1] = f, d) : null;
        if (f == b[c + e - 1]) return b.slice(c, c + e);
        c /= e;
        for (d /= e; c < d;) g = c + d >> 1, f < b[(g + 1) * e - 1] ? d = g : c = g + 1;
        d = b[c * e - 1];
        if (f == d) return b.slice((c - 1) * e, (c - 1) * e + e);
        g = (f - d) / (b[(c + 1) * e - 1] - d);
        d = [];
        var h;
        for (h = 0; h < e - 1; ++h) d.push(Xb(b[(c - 1) * e + h], b[c * e + h], g));
        d.push(f);
        return d
    }

    function Nm(b, c, d, e, f, g) {
        var h = 0;
        if (g) return Mm(b, h, c[c.length - 1], d, e, f);
        if (e < b[d - 1]) return f ? (b = b.slice(0, d), b[d - 1] = e, b) : null;
        if (b[b.length - 1] < e) return f ? (b = b.slice(b.length - d), b[d - 1] = e, b) : null;
        f = 0;
        for (g = c.length; f < g; ++f) {
            var k = c[f];
            if (h != k) {
                if (e < b[h + d - 1]) break;
                if (e <= b[k - 1]) return Mm(b, h, k, d, e, !1);
                h = k
            }
        }
        return null
    };

    function Om(b, c) {
        nk.call(this);
        this.b = null;
        this.o = this.p = this.n = -1;
        this.V(b, c)
    }

    v(Om, nk);
    l = Om.prototype;
    l.gh = function (b) {
        null === this.k ? this.k = b.slice() : $a(this.k, b);
        this.l()
    };
    l.clone = function () {
        var b = new Om(null);
        Pm(b, this.a, this.k.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        this.o != this.d && (this.p = Math.sqrt(wk(this.k, 0, this.k.length, this.B, 0)), this.o = this.d);
        return yk(this.k, 0, this.k.length, this.B, this.p, !1, b, c, d, e)
    };
    l.Lj = function (b, c) {
        return "XYM" != this.a && "XYZM" != this.a ? null : Mm(this.k, 0, this.k.length, this.B, b, m(c) ? c : !1)
    };
    l.Q = function () {
        return Dk(this.k, 0, this.k.length, this.B)
    };
    l.Mj = function () {
        var b = this.k, c = this.B, d = b[0], e = b[1], f = 0, g;
        for (g = 0 + c; g < this.k.length; g += c) var h = b[g], k = b[g + 1], f = f + Math.sqrt((h - d) * (h - d) + (k - e) * (k - e)), d = h, e = k;
        return f
    };

    function bm(b) {
        b.n != b.d && (b.b = Lm(b.k, 0, b.k.length, b.B, b.b), b.n = b.d);
        return b.b
    }

    l.nc = function (b) {
        var c = [];
        c.length = Fk(this.k, 0, this.k.length, this.B, b, c, 0);
        b = new Om(null);
        Pm(b, "XY", c);
        return b
    };
    l.O = function () {
        return "LineString"
    };
    l.ja = function (b) {
        return Qk(this.k, 0, this.k.length, this.B, b)
    };
    l.V = function (b, c) {
        null === b ? Pm(this, "XY", null) : (qk(this, c, b, 1), null === this.k && (this.k = []), this.k.length = Bk(this.k, 0, b, this.B), this.l())
    };

    function Pm(b, c, d) {
        pk(b, c, d);
        b.l()
    };

    function Qm(b, c) {
        nk.call(this);
        this.b = [];
        this.n = this.o = -1;
        this.V(b, c)
    }

    v(Qm, nk);
    l = Qm.prototype;
    l.hh = function (b) {
        null === this.k ? this.k = b.k.slice() : $a(this.k, b.k.slice());
        this.b.push(this.k.length);
        this.l()
    };
    l.clone = function () {
        var b = new Qm(null);
        Rm(b, this.a, this.k.slice(), this.b.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        this.n != this.d && (this.o = Math.sqrt(xk(this.k, 0, this.b, this.B, 0)), this.n = this.d);
        return zk(this.k, 0, this.b, this.B, this.o, !1, b, c, d, e)
    };
    l.Oj = function (b, c, d) {
        return "XYM" != this.a && "XYZM" != this.a || 0 === this.k.length ? null : Nm(this.k, this.b, this.B, b, m(c) ? c : !1, m(d) ? d : !1)
    };
    l.Q = function () {
        return Ek(this.k, 0, this.b, this.B)
    };
    l.Nh = function (b) {
        if (0 > b || this.b.length <= b) return null;
        var c = new Om(null);
        Pm(c, this.a, this.k.slice(0 === b ? 0 : this.b[b - 1], this.b[b]));
        return c
    };
    l.Lc = function () {
        var b = this.k, c = this.b, d = this.a, e = [], f = 0, g, h;
        g = 0;
        for (h = c.length; g < h; ++g) {
            var k = c[g], n = new Om(null);
            Pm(n, d, b.slice(f, k));
            e.push(n);
            f = k
        }
        return e
    };

    function cm(b) {
        var c = [], d = b.k, e = 0, f = b.b;
        b = b.B;
        var g, h;
        g = 0;
        for (h = f.length; g < h; ++g) {
            var k = f[g], e = Lm(d, e, k, b);
            $a(c, e);
            e = k
        }
        return c
    }

    l.nc = function (b) {
        var c = [], d = [], e = this.k, f = this.b, g = this.B, h = 0, k = 0, n, p;
        n = 0;
        for (p = f.length; n < p; ++n) {
            var q = f[n], k = Fk(e, h, q, g, b, c, k);
            d.push(k);
            h = q
        }
        c.length = k;
        b = new Qm(null);
        Rm(b, "XY", c, d);
        return b
    };
    l.O = function () {
        return "MultiLineString"
    };
    l.ja = function (b) {
        a:{
            var c = this.k, d = this.b, e = this.B, f = 0, g, h;
            g = 0;
            for (h = d.length; g < h; ++g) {
                if (Qk(c, f, d[g], e, b)) {
                    b = !0;
                    break a
                }
                f = d[g]
            }
            b = !1
        }
        return b
    };
    l.V = function (b, c) {
        if (null === b) Rm(this, "XY", null, this.b); else {
            qk(this, c, b, 2);
            null === this.k && (this.k = []);
            var d = Ck(this.k, 0, b, this.B, this.b);
            this.k.length = 0 === d.length ? 0 : d[d.length - 1];
            this.l()
        }
    };

    function Rm(b, c, d, e) {
        pk(b, c, d);
        b.b = e;
        b.l()
    }

    function Sm(b, c) {
        var d = "XY", e = [], f = [], g, h;
        g = 0;
        for (h = c.length; g < h; ++g) {
            var k = c[g];
            0 === g && (d = k.a);
            $a(e, k.k);
            f.push(e.length)
        }
        Rm(b, d, e, f)
    };

    function Tm(b, c) {
        nk.call(this);
        this.V(b, c)
    }

    v(Tm, nk);
    l = Tm.prototype;
    l.jh = function (b) {
        null === this.k ? this.k = b.k.slice() : $a(this.k, b.k);
        this.l()
    };
    l.clone = function () {
        var b = new Tm(null);
        pk(b, this.a, this.k.slice());
        b.l();
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        var f = this.k, g = this.B, h, k, n;
        h = 0;
        for (k = f.length; h < k; h += g) if (n = uk(b, c, f[h], f[h + 1]), n < e) {
            e = n;
            for (n = 0; n < g; ++n) d[n] = f[h + n];
            d.length = g
        }
        return e
    };
    l.Q = function () {
        return Dk(this.k, 0, this.k.length, this.B)
    };
    l.Xh = function (b) {
        var c = null === this.k ? 0 : this.k.length / this.B;
        if (0 > b || c <= b) return null;
        c = new Jk(null);
        Kk(c, this.a, this.k.slice(b * this.B, (b + 1) * this.B));
        return c
    };
    l.Ed = function () {
        var b = this.k, c = this.a, d = this.B, e = [], f, g;
        f = 0;
        for (g = b.length; f < g; f += d) {
            var h = new Jk(null);
            Kk(h, c, b.slice(f, f + d));
            e.push(h)
        }
        return e
    };
    l.O = function () {
        return "MultiPoint"
    };
    l.ja = function (b) {
        var c = this.k, d = this.B, e, f, g, h;
        e = 0;
        for (f = c.length; e < f; e += d) if (g = c[e], h = c[e + 1], $d(b, g, h)) return !0;
        return !1
    };
    l.V = function (b, c) {
        null === b ? pk(this, "XY", null) : (qk(this, c, b, 1), null === this.k && (this.k = []), this.k.length = Bk(this.k, 0, b, this.B));
        this.l()
    };

    function Um(b, c) {
        nk.call(this);
        this.b = [];
        this.o = -1;
        this.p = null;
        this.H = this.r = this.D = -1;
        this.n = null;
        this.V(b, c)
    }

    v(Um, nk);
    l = Um.prototype;
    l.kh = function (b) {
        if (null === this.k) this.k = b.k.slice(), b = b.b.slice(), this.b.push(); else {
            var c = this.k.length;
            $a(this.k, b.k);
            b = b.b.slice();
            var d, e;
            d = 0;
            for (e = b.length; d < e; ++d) b[d] += c
        }
        this.b.push(b);
        this.l()
    };
    l.clone = function () {
        var b = new Um(null);
        Vm(b, this.a, this.k.slice(), this.b.slice());
        return b
    };
    l.Ya = function (b, c, d, e) {
        if (e < Yd(this.J(), b, c)) return e;
        if (this.r != this.d) {
            var f = this.b, g = 0, h = 0, k, n;
            k = 0;
            for (n = f.length; k < n; ++k) var p = f[k], h = xk(this.k, g, p, this.B, h), g = p[p.length - 1];
            this.D = Math.sqrt(h);
            this.r = this.d
        }
        f = dm(this);
        g = this.b;
        h = this.B;
        k = this.D;
        n = 0;
        var p = m(void 0) ? void 0 : [NaN, NaN], q, r;
        q = 0;
        for (r = g.length; q < r; ++q) {
            var s = g[q];
            e = zk(f, n, s, h, k, !0, b, c, d, e, p);
            n = s[s.length - 1]
        }
        return e
    };
    l.Jb = function (b, c) {
        var d;
        a:{
            d = dm(this);
            var e = this.b, f = 0;
            if (0 !== e.length) {
                var g, h;
                g = 0;
                for (h = e.length; g < h; ++g) {
                    var k = e[g];
                    if (Nk(d, f, k, this.B, b, c)) {
                        d = !0;
                        break a
                    }
                    f = k[k.length - 1]
                }
            }
            d = !1
        }
        return d
    };
    l.Pj = function () {
        var b = dm(this), c = this.b, d = 0, e = 0, f, g;
        f = 0;
        for (g = c.length; f < g; ++f) var h = c[f], e = e + sk(b, d, h, this.B), d = h[h.length - 1];
        return e
    };
    l.Q = function () {
        var b = this.k, c = this.b, d = this.B, e = 0, f = m(void 0) ? void 0 : [], g = 0, h, k;
        h = 0;
        for (k = c.length; h < k; ++h) {
            var n = c[h];
            f[g++] = Ek(b, e, n, d, f[g]);
            e = n[n.length - 1]
        }
        f.length = g;
        return f
    };

    function em(b) {
        if (b.o != b.d) {
            var c = b.k, d = b.b, e = b.B, f = 0, g = [], h, k, n = Sd();
            h = 0;
            for (k = d.length; h < k; ++h) {
                var p = d[h], n = ee(Vd(Infinity, Infinity, -Infinity, -Infinity, void 0), c, f, p[0], e);
                g.push((n[0] + n[2]) / 2, (n[1] + n[3]) / 2);
                f = p[p.length - 1]
            }
            c = dm(b);
            d = b.b;
            e = b.B;
            f = 0;
            h = [];
            k = 0;
            for (n = d.length; k < n; ++k) p = d[k], h = Ok(c, f, p, e, g, 2 * k, h), f = p[p.length - 1];
            b.p = h;
            b.o = b.d
        }
        return b.p
    }

    l.Kh = function () {
        var b = new Tm(null), c = em(this).slice();
        pk(b, "XY", c);
        b.l();
        return b
    };

    function dm(b) {
        if (b.H != b.d) {
            var c = b.k, d;
            a:{
                d = b.b;
                var e, f;
                e = 0;
                for (f = d.length; e < f; ++e) if (!Tk(c, d[e], b.B)) {
                    d = !1;
                    break a
                }
                d = !0
            }
            if (d) b.n = c; else {
                b.n = c.slice();
                d = c = b.n;
                e = b.b;
                f = b.B;
                var g = 0, h, k;
                h = 0;
                for (k = e.length; h < k; ++h) g = Uk(d, g, e[h], f);
                c.length = g
            }
            b.H = b.d
        }
        return b.n
    }

    l.nc = function (b) {
        var c = [], d = [], e = this.k, f = this.b, g = this.B;
        b = Math.sqrt(b);
        var h = 0, k = 0, n, p;
        n = 0;
        for (p = f.length; n < p; ++n) {
            var q = f[n], r = [], k = Gk(e, h, q, g, b, c, k, r);
            d.push(r);
            h = q[q.length - 1]
        }
        c.length = k;
        e = new Um(null);
        Vm(e, "XY", c, d);
        return e
    };
    l.Yh = function (b) {
        if (0 > b || this.b.length <= b) return null;
        var c;
        0 === b ? c = 0 : (c = this.b[b - 1], c = c[c.length - 1]);
        b = this.b[b].slice();
        var d = b[b.length - 1];
        if (0 !== c) {
            var e, f;
            e = 0;
            for (f = b.length; e < f; ++e) b[e] -= c
        }
        e = new F(null);
        Vk(e, this.a, this.k.slice(c, d), b);
        return e
    };
    l.pd = function () {
        var b = this.a, c = this.k, d = this.b, e = [], f = 0, g, h, k, n;
        g = 0;
        for (h = d.length; g < h; ++g) {
            var p = d[g].slice(), q = p[p.length - 1];
            if (0 !== f) for (k = 0, n = p.length; k < n; ++k) p[k] -= f;
            k = new F(null);
            Vk(k, b, c.slice(f, q), p);
            e.push(k);
            f = q
        }
        return e
    };
    l.O = function () {
        return "MultiPolygon"
    };
    l.ja = function (b) {
        a:{
            var c = dm(this), d = this.b, e = this.B, f = 0, g, h;
            g = 0;
            for (h = d.length; g < h; ++g) {
                var k = d[g];
                if (Rk(c, f, k, e, b)) {
                    b = !0;
                    break a
                }
                f = k[k.length - 1]
            }
            b = !1
        }
        return b
    };
    l.V = function (b, c) {
        if (null === b) Vm(this, "XY", null, this.b); else {
            qk(this, c, b, 3);
            null === this.k && (this.k = []);
            var d = this.k, e = this.B, f = this.b, g = 0, f = m(f) ? f : [], h = 0, k, n;
            k = 0;
            for (n = b.length; k < n; ++k) g = Ck(d, g, b[k], e, f[h]), f[h++] = g, g = g[g.length - 1];
            f.length = h;
            0 === f.length ? this.k.length = 0 : (d = f[f.length - 1], this.k.length = 0 === d.length ? 0 : d[d.length - 1]);
            this.l()
        }
    };

    function Vm(b, c, d, e) {
        pk(b, c, d);
        b.b = e;
        b.l()
    }

    function Wm(b, c) {
        var d = "XY", e = [], f = [], g, h, k;
        g = 0;
        for (h = c.length; g < h; ++g) {
            var n = c[g];
            0 === g && (d = n.a);
            var p = e.length;
            k = n.b;
            var q, r;
            q = 0;
            for (r = k.length; q < r; ++q) k[q] += p;
            $a(e, n.k);
            f.push(k)
        }
        Vm(b, d, e, f)
    };

    function Xm(b, c) {
        return ma(b) - ma(c)
    }

    function Ym(b, c) {
        var d = .5 * b / c;
        return d * d
    }

    function Zm(b, c, d, e, f, g) {
        var h = !1, k, n;
        k = d.e;
        null !== k && (n = k.Oc(), 2 == n || 3 == n ? k.Oe(f, g) : (0 == n && k.load(), k.we(f, g), h = !0));
        f = (0, d.c)(c);
        null != f && (e = f.se(e), (0, $m[e.O()])(b, e, d, c));
        return h
    }

    var $m = {
        Point: function (b, c, d, e) {
            var f = d.e;
            if (null !== f) {
                if (2 != f.Oc()) return;
                var g = b.a(d.a, "Image");
                g.ib(f);
                g.ub(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), b.vb(c.Q(), 0, 2, 2, c, e))
        }, LineString: function (b, c, d, e) {
            var f = d.b;
            if (null !== f) {
                var g = b.a(d.a, "LineString");
                g.Ba(null, f);
                g.Eb(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), b.vb(bm(c), 0, 2, 2, c, e))
        }, Polygon: function (b, c, d, e) {
            var f = d.f, g = d.b;
            if (null !== f || null !== g) {
                var h = b.a(d.a, "Polygon");
                h.Ba(f, g);
                h.Rb(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f),
                b.vb(Xk(c), 0, 2, 2, c, e))
        }, MultiPoint: function (b, c, d, e) {
            var f = d.e;
            if (null !== f) {
                if (2 != f.Oc()) return;
                var g = b.a(d.a, "Image");
                g.ib(f);
                g.tb(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), d = c.k, b.vb(d, 0, d.length, c.B, c, e))
        }, MultiLineString: function (b, c, d, e) {
            var f = d.b;
            if (null !== f) {
                var g = b.a(d.a, "LineString");
                g.Ba(null, f);
                g.lc(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), d = cm(c), b.vb(d, 0, d.length, 2, c, e))
        }, MultiPolygon: function (b, c, d, e) {
            var f = d.f, g = d.b;
            if (null !== g || null !== f) {
                var h = b.a(d.a, "Polygon");
                h.Ba(f,
                    g);
                h.mc(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), d = em(c), b.vb(d, 0, d.length, 2, c, e))
        }, GeometryCollection: function (b, c, d, e) {
            c = c.c;
            var f, g;
            f = 0;
            for (g = c.length; f < g; ++f) (0, $m[c[f].O()])(b, c[f], d, e)
        }, Circle: function (b, c, d, e) {
            var f = d.f, g = d.b;
            if (null !== f || null !== g) {
                var h = b.a(d.a, "Polygon");
                h.Ba(f, g);
                h.kc(c, e)
            }
            f = d.d;
            null !== f && (b = b.a(d.a, "Text"), b.Ca(f), b.vb(c.ye(), 0, 2, 2, c, e))
        }
    };

    function an(b, c, d, e, f) {
        Oi.call(this, b, c, d, 2, e);
        this.d = f
    }

    v(an, Oi);
    an.prototype.a = function () {
        return this.d
    };

    function bn(b) {
        Ki.call(this, {
            attributions: b.attributions,
            extent: b.extent,
            logo: b.logo,
            projection: b.projection,
            state: b.state
        });
        this.i = m(b.resolutions) ? b.resolutions : null
    }

    v(bn, Ki);

    function cn(b, c) {
        if (null !== b.i) {
            var d = ac(b.i, c, 0);
            c = b.i[d]
        }
        return c
    }

    function dn(b, c) {
        b.a().src = c
    };

    function en(b) {
        bn.call(this, {
            attributions: b.attributions,
            logo: b.logo,
            projection: b.projection,
            resolutions: b.resolutions,
            state: m(b.state) ? b.state : void 0
        });
        this.H = b.canvasFunction;
        this.p = null;
        this.r = 0;
        this.N = m(b.ratio) ? b.ratio : 1.5
    }

    v(en, bn);
    en.prototype.sc = function (b, c, d, e) {
        c = cn(this, c);
        var f = this.p;
        if (null !== f && this.r == this.d && f.resolution == c && f.f == d && Zd(f.J(), b)) return f;
        b = b.slice();
        se(b, this.N);
        e = this.H(b, c, d, [qe(b) / c * d, ne(b) / c * d], e);
        null === e || (f = new an(b, c, d, this.f, e));
        this.p = f;
        this.r = this.d;
        return f
    };
    var fn;
    (function () {
        var b = {lf: {}};
        (function () {
            function c(b, d) {
                if (!(this instanceof c)) return new c(b, d);
                this.ge = Math.max(4, b || 9);
                this.Ze = Math.max(2, Math.ceil(.4 * this.ge));
                d && this.bh(d);
                this.clear()
            }

            function d(b, c) {
                b.bbox = e(b, 0, b.children.length, c)
            }

            function e(b, c, d, e) {
                for (var g = [Infinity, Infinity, -Infinity, -Infinity], h; c < d; c++) h = b.children[c], f(g, b.za ? e(h) : h.bbox);
                return g
            }

            function f(b, c) {
                b[0] = Math.min(b[0], c[0]);
                b[1] = Math.min(b[1], c[1]);
                b[2] = Math.max(b[2], c[2]);
                b[3] = Math.max(b[3], c[3])
            }

            function g(b, c) {
                return b.bbox[0] -
                    c.bbox[0]
            }

            function h(b, c) {
                return b.bbox[1] - c.bbox[1]
            }

            function k(b) {
                return (b[2] - b[0]) * (b[3] - b[1])
            }

            function n(b) {
                return b[2] - b[0] + (b[3] - b[1])
            }

            function p(b, c) {
                return b[0] <= c[0] && b[1] <= c[1] && c[2] <= b[2] && c[3] <= b[3]
            }

            function q(b, c) {
                return c[0] <= b[2] && c[1] <= b[3] && c[2] >= b[0] && c[3] >= b[1]
            }

            function r(b, c, d, e, f) {
                for (var g = [c, d], h; g.length;) d = g.pop(), c = g.pop(), d - c <= e || (h = c + Math.ceil((d - c) / e / 2) * e, s(b, c, d, h, f), g.push(c, h, h, d))
            }

            function s(b, c, d, e, f) {
                for (var g, h, k, n, p; d > c;) {
                    600 < d - c && (g = d - c + 1, h = e - c + 1, k = Math.log(g),
                        n = .5 * Math.exp(2 * k / 3), p = .5 * Math.sqrt(k * n * (g - n) / g) * (0 > h - g / 2 ? -1 : 1), k = Math.max(c, Math.floor(e - h * n / g + p)), h = Math.min(d, Math.floor(e + (g - h) * n / g + p)), s(b, k, h, e, f));
                    g = b[e];
                    h = c;
                    n = d;
                    u(b, c, e);
                    for (0 < f(b[d], g) && u(b, c, d); h < n;) {
                        u(b, h, n);
                        h++;
                        for (n--; 0 > f(b[h], g);) h++;
                        for (; 0 < f(b[n], g);) n--
                    }
                    0 === f(b[c], g) ? u(b, c, n) : (n++, u(b, n, d));
                    n <= e && (c = n + 1);
                    e <= n && (d = n - 1)
                }
            }

            function u(b, c, d) {
                var e = b[c];
                b[c] = b[d];
                b[d] = e
            }

            c.prototype = {
                all: function () {
                    return this.Ve(this.data, [])
                }, search: function (b) {
                    var c = this.data, d = [], e = this.Ka;
                    if (!q(b, c.bbox)) return d;
                    for (var f = [], g, h, k, n; c;) {
                        g = 0;
                        for (h = c.children.length; g < h; g++) k = c.children[g], n = c.za ? e(k) : k.bbox, q(b, n) && (c.za ? d.push(k) : p(b, n) ? this.Ve(k, d) : f.push(k));
                        c = f.pop()
                    }
                    return d
                }, load: function (b) {
                    if (!b || !b.length) return this;
                    if (b.length < this.Ze) {
                        for (var c = 0, d = b.length; c < d; c++) this.sa(b[c]);
                        return this
                    }
                    b = this.Xe(b.slice(), 0, b.length - 1, 0);
                    this.data.children.length ? this.data.height === b.height ? this.$e(this.data, b) : (this.data.height < b.height && (c = this.data, this.data = b, b = c), this.Ye(b, this.data.height - b.height - 1,
                        !0)) : this.data = b;
                    return this
                }, sa: function (b) {
                    b && this.Ye(b, this.data.height - 1);
                    return this
                }, clear: function () {
                    this.data = {children: [], height: 1, bbox: [Infinity, Infinity, -Infinity, -Infinity], za: !0};
                    return this
                }, remove: function (b) {
                    if (!b) return this;
                    for (var c = this.data, d = this.Ka(b), e = [], f = [], g, h, k, n; c || e.length;) {
                        c || (c = e.pop(), h = e[e.length - 1], g = f.pop(), n = !0);
                        if (c.za && (k = c.children.indexOf(b), -1 !== k)) {
                            c.children.splice(k, 1);
                            e.push(c);
                            this.ah(e);
                            break
                        }
                        n || c.za || !p(c.bbox, d) ? h ? (g++, c = h.children[g], n = !1) : c = null :
                            (e.push(c), f.push(g), g = 0, h = c, c = c.children[0])
                    }
                    return this
                }, Ka: function (b) {
                    return b
                }, je: function (b, c) {
                    return b[0] - c[0]
                }, ke: function (b, c) {
                    return b[1] - c[1]
                }, toJSON: function () {
                    return this.data
                }, Ve: function (b, c) {
                    for (var d = []; b;) b.za ? c.push.apply(c, b.children) : d.push.apply(d, b.children), b = d.pop();
                    return c
                }, Xe: function (b, c, e, f) {
                    var g = e - c + 1, h = this.ge, k;
                    if (g <= h) return k = {
                        children: b.slice(c, e + 1),
                        height: 1,
                        bbox: null,
                        za: !0
                    }, d(k, this.Ka), k;
                    f || (f = Math.ceil(Math.log(g) / Math.log(h)), h = Math.ceil(g / Math.pow(h, f - 1)));
                    k = {children: [], height: f, bbox: null};
                    var g = Math.ceil(g / h), h = g * Math.ceil(Math.sqrt(h)), n, p, q;
                    for (r(b, c, e, h, this.je); c <= e; c += h) for (p = Math.min(c + h - 1, e), r(b, c, p, g, this.ke), n = c; n <= p; n += g) q = Math.min(n + g - 1, p), k.children.push(this.Xe(b, n, q, f - 1));
                    d(k, this.Ka);
                    return k
                }, $g: function (b, c, d, e) {
                    for (var f, g, h, n, p, q, r, s; ;) {
                        e.push(c);
                        if (c.za || e.length - 1 === d) break;
                        r = s = Infinity;
                        f = 0;
                        for (g = c.children.length; f < g; f++) {
                            h = c.children[f];
                            p = k(h.bbox);
                            q = b;
                            var u = h.bbox;
                            q = (Math.max(u[2], q[2]) - Math.min(u[0], q[0])) * (Math.max(u[3],
                                q[3]) - Math.min(u[1], q[1])) - p;
                            q < s ? (s = q, r = p < r ? p : r, n = h) : q === s && p < r && (r = p, n = h)
                        }
                        c = n
                    }
                    return c
                }, Ye: function (b, c, d) {
                    var e = this.Ka;
                    d = d ? b.bbox : e(b);
                    var e = [], g = this.$g(d, this.data, c, e);
                    g.children.push(b);
                    for (f(g.bbox, d); 0 <= c;) if (e[c].children.length > this.ge) this.dh(e, c), c--; else break;
                    this.Xg(d, e, c)
                }, dh: function (b, c) {
                    var e = b[c], f = e.children.length, g = this.Ze;
                    this.Yg(e, g, f);
                    f = {children: e.children.splice(this.Zg(e, g, f)), height: e.height};
                    e.za && (f.za = !0);
                    d(e, this.Ka);
                    d(f, this.Ka);
                    c ? b[c - 1].children.push(f) : this.$e(e,
                        f)
                }, $e: function (b, c) {
                    this.data = {children: [b, c], height: b.height + 1};
                    d(this.data, this.Ka)
                }, Zg: function (b, c, d) {
                    var f, g, h, n, p, q, r;
                    p = q = Infinity;
                    for (f = c; f <= d - c; f++) {
                        g = e(b, 0, f, this.Ka);
                        h = e(b, f, d, this.Ka);
                        var s = g, u = h;
                        n = Math.max(s[0], u[0]);
                        var mb = Math.max(s[1], u[1]), Pb = Math.min(s[2], u[2]), s = Math.min(s[3], u[3]);
                        n = Math.max(0, Pb - n) * Math.max(0, s - mb);
                        g = k(g) + k(h);
                        n < p ? (p = n, r = f, q = g < q ? g : q) : n === p && g < q && (q = g, r = f)
                    }
                    return r
                }, Yg: function (b, c, d) {
                    var e = b.za ? this.je : g, f = b.za ? this.ke : h, k = this.We(b, c, d, e);
                    c = this.We(b, c, d, f);
                    k < c && b.children.sort(e)
                }, We: function (b, c, d, g) {
                    b.children.sort(g);
                    g = this.Ka;
                    var h = e(b, 0, c, g), k = e(b, d - c, d, g), p = n(h) + n(k), q, r;
                    for (q = c; q < d - c; q++) r = b.children[q], f(h, b.za ? g(r) : r.bbox), p += n(h);
                    for (q = d - c - 1; q >= c; q--) r = b.children[q], f(k, b.za ? g(r) : r.bbox), p += n(k);
                    return p
                }, Xg: function (b, c, d) {
                    for (; 0 <= d; d--) f(c[d].bbox, b)
                }, ah: function (b) {
                    for (var c = b.length - 1, e; 0 <= c; c--) 0 === b[c].children.length ? 0 < c ? (e = b[c - 1].children, e.splice(e.indexOf(b[c]), 1)) : this.clear() : d(b[c], this.Ka)
                }, bh: function (b) {
                    var c = ["return a", " - b",
                        ";"];
                    this.je = new Function("a", "b", c.join(b[0]));
                    this.ke = new Function("a", "b", c.join(b[1]));
                    this.Ka = new Function("a", "return [a" + b.join(", a") + "];")
                }
            };
            "function" === typeof define && define.Mm ? define(function () {
                return c
            }) : "undefined" !== typeof b ? b.lf = c : "undefined" !== typeof self ? self.a = c : window.a = c
        })();
        fn = b.lf
    })();

    function gn(b) {
        this.d = fn(b);
        this.a = {}
    }

    l = gn.prototype;
    l.sa = function (b, c) {
        var d = [b[0], b[1], b[2], b[3], c];
        this.d.sa(d);
        this.a[ma(c)] = d
    };
    l.load = function (b, c) {
        for (var d = Array(c.length), e = 0, f = c.length; e < f; e++) {
            var g = b[e], h = c[e], g = [g[0], g[1], g[2], g[3], h];
            d[e] = g;
            this.a[ma(h)] = g
        }
        this.d.load(d)
    };
    l.remove = function (b) {
        b = ma(b);
        var c = this.a[b];
        xb(this.a, b);
        return null !== this.d.remove(c)
    };
    l.update = function (b, c) {
        var d = ma(c);
        ce(this.a[d].slice(0, 4), b) || (this.remove(c), this.sa(b, c))
    };

    function hn(b) {
        b = b.d.all();
        return Ra(b, function (b) {
            return b[4]
        })
    }

    function jn(b, c) {
        var d = b.d.search(c);
        return Ra(d, function (b) {
            return b[4]
        })
    }

    l.forEach = function (b, c) {
        return kn(hn(this), b, c)
    };

    function ln(b, c, d, e) {
        return kn(jn(b, c), d, e)
    }

    function kn(b, c, d) {
        for (var e, f = 0, g = b.length; f < g && !(e = c.call(d, b[f])); f++) ;
        return e
    }

    l.la = function () {
        return vb(this.a)
    };
    l.clear = function () {
        this.d.clear();
        this.a = {}
    };
    l.J = function () {
        return this.d.data.bbox
    };

    function mn(b) {
        b = m(b) ? b : {};
        Ki.call(this, {
            attributions: b.attributions,
            logo: b.logo,
            projection: b.projection,
            state: m(b.state) ? b.state : void 0
        });
        this.b = new gn;
        this.c = {};
        this.e = {};
        this.i = {};
        this.j = {};
        m(b.features) && this.lb(b.features)
    }

    v(mn, Ki);
    l = mn.prototype;
    l.Va = function (b) {
        var c = ma(b).toString();
        nn(this, c, b);
        var d = b.R();
        null != d ? (d = d.J(), this.b.sa(d, b)) : this.c[c] = b;
        on(this, c, b);
        this.dispatchEvent(new pn("addfeature", b));
        this.l()
    };

    function nn(b, c, d) {
        b.j[c] = [w(d, "change", b.Pf, !1, b), w(d, "propertychange", b.Pf, !1, b)]
    }

    function on(b, c, d) {
        var e = d.aa;
        m(e) ? b.e[e.toString()] = d : b.i[c] = d
    }

    l.Ga = function (b) {
        this.lb(b);
        this.l()
    };
    l.lb = function (b) {
        var c, d, e, f, g = [], h = [];
        d = 0;
        for (e = b.length; d < e; d++) {
            f = b[d];
            c = ma(f).toString();
            nn(this, c, f);
            var k = f.R();
            null != k ? (c = k.J(), g.push(c), h.push(f)) : this.c[c] = f
        }
        this.b.load(g, h);
        d = 0;
        for (e = b.length; d < e; d++) f = b[d], c = ma(f).toString(), on(this, c, f), this.dispatchEvent(new pn("addfeature", f))
    };
    l.clear = function (b) {
        if (b) {
            for (var c in this.j) Pa(this.j[c], Wc);
            this.j = {};
            this.e = {};
            this.i = {}
        } else b = this.lg, this.b.forEach(b, this), kb(this.c, b, this);
        this.b.clear();
        this.c = {};
        this.dispatchEvent(new pn("clear"));
        this.l()
    };
    l.$a = function (b, c) {
        return this.b.forEach(b, c)
    };

    function qn(b, c, d) {
        b.wa([c[0], c[1], c[0], c[1]], function (b) {
            if (b.R().Jb(c[0], c[1])) return d.call(void 0, b)
        })
    }

    l.wa = function (b, c, d) {
        return ln(this.b, b, c, d)
    };
    l.Fb = function (b, c, d, e) {
        return this.wa(b, d, e)
    };
    l.Ma = function (b, c, d) {
        return this.wa(b, function (e) {
            if (e.R().ja(b) && (e = c.call(d, e))) return e
        })
    };
    l.Aa = function () {
        var b = hn(this.b);
        vb(this.c) || $a(b, qb(this.c));
        return b
    };
    l.Oa = function (b) {
        var c = [];
        qn(this, b, function (b) {
            c.push(b)
        });
        return c
    };
    l.ab = function (b) {
        var c = b[0], d = b[1], e = null, f = [NaN, NaN], g = Infinity, h = [-Infinity, -Infinity, Infinity, Infinity];
        ln(this.b, h, function (b) {
            var n = b.R(), p = g;
            g = n.Ya(c, d, f, g);
            g < p && (e = b, b = Math.sqrt(g), h[0] = c - b, h[1] = d - b, h[2] = c + b, h[3] = d + b)
        });
        return e
    };
    l.J = function () {
        return this.b.J()
    };
    l.Na = function (b) {
        b = this.e[b.toString()];
        return m(b) ? b : null
    };
    l.Pf = function (b) {
        b = b.target;
        var c = ma(b).toString(), d = b.R();
        null != d ? (d = d.J(), c in this.c ? (delete this.c[c], this.b.sa(d, b)) : this.b.update(d, b)) : c in this.c || (this.b.remove(b), this.c[c] = b);
        d = b.aa;
        m(d) ? (d = d.toString(), c in this.i ? (delete this.i[c], this.e[d] = b) : this.e[d] !== b && (rn(this, b), this.e[d] = b)) : c in this.i || (rn(this, b), this.i[c] = b);
        this.l();
        this.dispatchEvent(new pn("changefeature", b))
    };
    l.la = function () {
        return this.b.la() && vb(this.c)
    };
    l.Hb = ca;
    l.fb = function (b) {
        var c = ma(b).toString();
        c in this.c ? delete this.c[c] : this.b.remove(b);
        this.lg(b);
        this.l()
    };
    l.lg = function (b) {
        var c = ma(b).toString();
        Pa(this.j[c], Wc);
        delete this.j[c];
        var d = b.aa;
        m(d) ? delete this.e[d.toString()] : delete this.i[c];
        this.dispatchEvent(new pn("removefeature", b))
    };

    function rn(b, c) {
        for (var d in b.e) if (b.e[d] === c) {
            delete b.e[d];
            break
        }
    }

    function pn(b, c) {
        qc.call(this, b);
        this.feature = c
    }

    v(pn, qc);

    function sn(b) {
        this.a = b.source;
        this.S = Hd();
        this.b = Nf();
        this.c = [0, 0];
        this.j = null;
        en.call(this, {
            attributions: b.attributions,
            canvasFunction: ra(this.lh, this),
            logo: b.logo,
            projection: b.projection,
            ratio: b.ratio,
            resolutions: b.resolutions,
            state: this.a.q
        });
        this.o = null;
        this.e = void 0;
        this.Mf(b.style);
        w(this.a, "change", this.fk, void 0, this)
    }

    v(sn, en);
    l = sn.prototype;
    l.lh = function (b, c, d, e, f) {
        var g = new vm(.5 * c / d, b, c);
        this.a.Hb(b, c, f);
        var h = !1;
        this.a.Fb(b, c, function (b) {
            var e;
            if (!(e = h)) {
                var f;
                m(b.a) ? f = b.a.call(b, c) : m(this.e) && (f = this.e(b, c));
                if (null != f) {
                    var q, r = !1;
                    e = 0;
                    for (q = f.length; e < q; ++e) r = Zm(g, b, f[e], Ym(c, d), this.ek, this) || r;
                    e = r
                } else e = !1
            }
            h = e
        }, this);
        wm(g);
        if (h) return null;
        this.c[0] != e[0] || this.c[1] != e[1] ? (this.b.canvas.width = e[0], this.b.canvas.height = e[1], this.c[0] = e[0], this.c[1] = e[1]) : this.b.clearRect(0, 0, e[0], e[1]);
        b = tn(this, ke(b), c, d, e);
        zm(g, this.b, d, b, 0,
            {});
        this.j = g;
        return this.b.canvas
    };
    l.Hd = function (b, c, d, e, f) {
        if (null !== this.j) {
            var g = {};
            return this.j.b(b, c, 0, e, function (b) {
                var c = ma(b).toString();
                if (!(c in g)) return g[c] = !0, f(b)
            })
        }
    };
    l.bk = function () {
        return this.a
    };
    l.ck = function () {
        return this.o
    };
    l.dk = function () {
        return this.e
    };

    function tn(b, c, d, e, f) {
        return hj(b.S, f[0] / 2, f[1] / 2, e / d, -e / d, 0, -c[0], -c[1])
    }

    l.ek = function () {
        this.l()
    };
    l.fk = function () {
        Li(this, this.a.q)
    };
    l.Mf = function (b) {
        this.o = m(b) ? b : ul;
        this.e = null === b ? void 0 : tl(this.o);
        this.l()
    };

    function un(b) {
        Am.call(this, b);
        this.f = null;
        this.e = Hd();
        this.b = this.c = null
    }

    v(un, Am);
    l = un.prototype;
    l.Ua = function (b, c, d, e) {
        var f = this.a;
        return f.a().Hd(b, c.viewState.resolution, c.viewState.rotation, c.skippedFeatureUids, function (b) {
            return d.call(e, b, f)
        })
    };
    l.bc = function (b, c, d, e) {
        if (!fa(this.Gd())) if (this.a.a() instanceof sn) {
            if (b = b.slice(), kj(c.pixelToCoordinateMatrix, b, b), this.Ua(b, c, bd, this)) return d.call(e, this.a)
        } else if (null === this.c && (this.c = Hd(), Nd(this.e, this.c)), c = Dm(b, this.c), null === this.b && (this.b = Nf(1, 1)), this.b.clearRect(0, 0, 1, 1), this.b.drawImage(this.Gd(), c[0], c[1], 1, 1, 0, 0, 1, 1), 0 < this.b.getImageData(0, 0, 1, 1).data[3]) return d.call(e, this.a)
    };
    l.Gd = function () {
        return null === this.f ? null : this.f.a()
    };
    l.of = function () {
        return this.e
    };
    l.Ae = function (b, c) {
        var d = b.pixelRatio, e = b.viewState, f = e.center, g = e.resolution, h = e.rotation, k, n = this.a.a(),
            p = b.viewHints;
        k = b.extent;
        m(c.extent) && (k = oe(k, c.extent));
        p[0] || p[1] || re(k) || (e = e.projection, p = n.g, null === p || (e = p), k = n.sc(k, g, d, e), null !== k && nj(this, k) && (this.f = k));
        if (null !== this.f) {
            k = this.f;
            var e = k.J(), p = k.resolution, q = k.f, g = d * p / (g * q);
            hj(this.e, d * b.size[0] / 2, d * b.size[1] / 2, g, g, h, q * (e[0] - f[0]) / p, q * (f[1] - e[3]) / p);
            this.c = null;
            pj(b.attributions, k.e);
            qj(b, n)
        }
        return !0
    };

    function vn(b) {
        Am.call(this, b);
        this.b = this.e = null;
        this.i = !1;
        this.g = null;
        this.j = Hd();
        this.f = null;
        this.p = this.o = NaN;
        this.n = this.c = null
    }

    v(vn, Am);
    vn.prototype.Gd = function () {
        return this.e
    };
    vn.prototype.of = function () {
        return this.j
    };
    vn.prototype.Ae = function (b, c) {
        var d = b.pixelRatio, e = b.viewState, f = e.projection, g = this.a, h = g.a(), k = fj(h, f), n = h.hd(),
            p = ac(k.a, e.resolution, 0), q = h.Nc(p, b.pixelRatio, f), r = k.na(p), s = r / (q / k.ua(p)),
            u = e.center, z;
        r == e.resolution ? (u = sj(u, r, b.size), z = me(u, r, e.rotation, b.size)) : z = b.extent;
        m(c.extent) && (z = oe(z, c.extent));
        if (re(z)) return !1;
        var y = Zi(k, z, r), A = q * (y.c - y.a + 1), D = q * (y.d - y.b + 1), x, M;
        null === this.e ? (M = Nf(A, D), this.e = M.canvas, this.b = [A, D], this.g = M, this.i = !Em(this.b)) : (x = this.e, M = this.g, this.b[0] < A || this.b[1] <
        D || this.p !== q || this.i && (this.b[0] > A || this.b[1] > D) ? (x.width = A, x.height = D, this.b = [A, D], this.i = !Em(this.b), this.c = null) : (A = this.b[0], D = this.b[1], (x = p != this.o) || (x = this.c, x = !(x.a <= y.a && y.c <= x.c && x.b <= y.b && y.d <= x.d)), x && (this.c = null)));
        var Q, O;
        null === this.c ? (A /= q, D /= q, Q = y.a - Math.floor((A - (y.c - y.a + 1)) / 2), O = y.b - Math.floor((D - (y.d - y.b + 1)) / 2), this.o = p, this.p = q, this.c = new lf(Q, Q + A - 1, O, O + D - 1), this.n = Array(A * D), D = this.c) : (D = this.c, A = D.c - D.a + 1);
        x = {};
        x[p] = {};
        var W = [], Ja = this.dd(h, x), lb = g.ea(), Ka = Sd(), mb = new lf(0,
            0, 0, 0), Pb, Ya, Ub;
        for (O = y.a; O <= y.c; ++O) for (Ub = y.b; Ub <= y.d; ++Ub) Ya = h.Vb(p, O, Ub, d, f), Q = Ya.state, 2 == Q || 4 == Q || 3 == Q && !lb ? x[p][kf(Ya.a)] = Ya : (Pb = k.fd(Ya.a, Ja, null, mb, Ka), Pb || (W.push(Ya), Pb = k.sd(Ya.a, mb, Ka), null === Pb || Ja(p + 1, Pb)));
        Ja = 0;
        for (Pb = W.length; Ja < Pb; ++Ja) Ya = W[Ja], O = q * (Ya.a[1] - D.a), Ub = q * (D.d - Ya.a[2]), M.clearRect(O, Ub, q, q);
        W = Ra(rb(x), Number);
        cb(W);
        var nb = h.H, Mc = je(Xi(k, [p, D.a, D.d], Ka)), sc, Oe, jj, Wh, Rf, Xl, Ja = 0;
        for (Pb = W.length; Ja < Pb; ++Ja) if (sc = W[Ja], q = h.Nc(sc, d, f), Wh = x[sc], sc == p) for (jj in Wh) Ya = Wh[jj], Oe =
            (Ya.a[2] - D.b) * A + (Ya.a[1] - D.a), this.n[Oe] != Ya && (O = q * (Ya.a[1] - D.a), Ub = q * (D.d - Ya.a[2]), Q = Ya.state, 4 != Q && (3 != Q || lb) && nb || M.clearRect(O, Ub, q, q), 2 == Q && M.drawImage(Ya.Ta(), n, n, q, q, O, Ub, q, q), this.n[Oe] = Ya); else for (jj in sc = k.na(sc) / r, Wh) for (Ya = Wh[jj], Oe = Xi(k, Ya.a, Ka), O = (Oe[0] - Mc[0]) / s, Ub = (Mc[1] - Oe[3]) / s, Xl = sc * q, Rf = sc * q, Q = Ya.state, 4 != Q && nb || M.clearRect(O, Ub, Xl, Rf), 2 == Q && M.drawImage(Ya.Ta(), n, n, q, q, O, Ub, Xl, Rf), Ya = Yi(k, Oe, p, mb), Q = Math.max(Ya.a, D.a), Ub = Math.min(Ya.c, D.c), O = Math.max(Ya.b, D.b), Ya = Math.min(Ya.d,
            D.d); Q <= Ub; ++Q) for (Rf = O; Rf <= Ya; ++Rf) Oe = (Rf - D.b) * A + (Q - D.a), this.n[Oe] = void 0;
        rj(b.usedTiles, h, p, y);
        tj(b, h, k, d, f, z, p, g.r());
        oj(b, h);
        qj(b, h);
        hj(this.j, d * b.size[0] / 2, d * b.size[1] / 2, d * s / e.resolution, d * s / e.resolution, e.rotation, (Mc[0] - u[0]) / s, (u[1] - Mc[1]) / s);
        this.f = null;
        return !0
    };
    vn.prototype.bc = function (b, c, d, e) {
        if (null !== this.g && (null === this.f && (this.f = Hd(), Nd(this.j, this.f)), b = Dm(b, this.f), 0 < this.g.getImageData(b[0], b[1], 1, 1).data[3])) return d.call(e, this.a)
    };

    function wn(b) {
        Am.call(this, b);
        this.c = !1;
        this.i = -1;
        this.n = NaN;
        this.e = Sd();
        this.b = this.g = null;
        this.f = Nf()
    }

    v(wn, Am);
    wn.prototype.q = function (b, c, d) {
        var e = Cm(this, b);
        Bm(this, "precompose", d, b, e);
        var f = this.b;
        if (null !== f && !f.la()) {
            var g;
            jd(this.a, "render") ? (this.f.canvas.width = d.canvas.width, this.f.canvas.height = d.canvas.height, g = this.f) : g = d;
            var h = g.globalAlpha;
            g.globalAlpha = c.opacity;
            zm(f, g, b.pixelRatio, e, b.viewState.rotation, b.skippedFeatureUids);
            g != d && (Bm(this, "render", g, b, e), d.drawImage(g.canvas, 0, 0));
            g.globalAlpha = h
        }
        Bm(this, "postcompose", d, b, e)
    };
    wn.prototype.Ua = function (b, c, d, e) {
        if (null !== this.b) {
            var f = this.a, g = {};
            return this.b.b(b, c.viewState.resolution, c.viewState.rotation, c.skippedFeatureUids, function (b) {
                var c = ma(b).toString();
                if (!(c in g)) return g[c] = !0, d.call(e, b, f)
            })
        }
    };
    wn.prototype.j = function () {
        mj(this)
    };
    wn.prototype.Ae = function (b) {
        function c(b) {
            var c;
            m(b.a) ? c = b.a.call(b, k) : m(d.r) && (c = (0, d.r)(b, k));
            if (null != c) {
                if (null != c) {
                    var e, f, g = !1;
                    e = 0;
                    for (f = c.length; e < f; ++e) g = Zm(q, b, c[e], Ym(k, n), this.j, this) || g;
                    b = g
                } else b = !1;
                this.c = this.c || b
            }
        }

        var d = this.a, e = d.a();
        pj(b.attributions, e.f);
        qj(b, e);
        if (!this.c && (!d.Ac && b.viewHints[0] || b.viewHints[1])) return !0;
        var f = b.extent, g = b.viewState, h = g.projection, k = g.resolution, n = b.pixelRatio;
        b = d.d;
        var p = d.ea, g = d.get("renderOrder");
        m(g) || (g = Xm);
        f = Wd(f, p * k);
        if (!this.c && this.n ==
            k && this.i == b && this.g == g && Zd(this.e, f)) return !0;
        pc(this.b);
        this.b = null;
        this.c = !1;
        var q = new vm(.5 * k / n, f, k, d.ea);
        e.Hb(f, k, h);
        if (null === g) e.Fb(f, k, c, this); else {
            var r = [];
            e.Fb(f, k, function (b) {
                r.push(b)
            }, this);
            cb(r, g);
            Pa(r, c, this)
        }
        wm(q);
        this.n = k;
        this.i = b;
        this.g = g;
        this.e = f;
        this.b = q;
        return !0
    };

    function xn(b, c) {
        zj.call(this, 0, c);
        this.c = Nf();
        this.a = this.c.canvas;
        this.a.style.width = "100%";
        this.a.style.height = "100%";
        this.a.className = "ol-unselectable";
        Gf(b, this.a, 0);
        this.d = !0;
        this.f = Hd()
    }

    v(xn, zj);
    xn.prototype.le = function (b) {
        return b instanceof H ? new un(b) : b instanceof I ? new vn(b) : b instanceof J ? new wn(b) : null
    };

    function yn(b, c, d) {
        var e = b.g, f = b.c;
        if (jd(e, c)) {
            var g = d.extent, h = d.pixelRatio, k = d.viewState, n = k.resolution, p = k.rotation;
            hj(b.f, b.a.width / 2, b.a.height / 2, h / n, -h / n, -p, -k.center[0], -k.center[1]);
            k = new vm(.5 * n / h, g, n);
            g = new Tl(f, h, g, b.f, p);
            e.dispatchEvent(new Zk(c, e, g, k, d, f, null));
            wm(k);
            k.la() || zm(k, f, h, b.f, p, {});
            fm(g);
            b.b = k
        }
    }

    xn.prototype.O = function () {
        return "canvas"
    };
    xn.prototype.Wd = function (b) {
        if (null === b) this.d && (Mg(this.a, !1), this.d = !1); else {
            var c = this.c, d = b.size[0] * b.pixelRatio, e = b.size[1] * b.pixelRatio;
            this.a.width != d || this.a.height != e ? (this.a.width = d, this.a.height = e) : c.clearRect(0, 0, this.a.width, this.a.height);
            Aj(b);
            yn(this, "precompose", b);
            var d = b.layerStatesArray, e = b.viewState.resolution, f, g, h, k;
            f = 0;
            for (g = d.length; f < g; ++f) k = d[f], h = k.layer, h = Cj(this, h), Ni(k, e) && "ready" == k.yc && h.Ae(b, k) && h.q(b, k, c);
            yn(this, "postcompose", b);
            this.d || (Mg(this.a, !0), this.d = !0);
            Dj(this, b);
            b.postRenderFunctions.push(Bj)
        }
    };

    function zn(b, c) {
        lj.call(this, b);
        this.target = c
    }

    v(zn, lj);
    zn.prototype.f = ca;
    zn.prototype.n = ca;

    function An(b) {
        var c = Df("DIV");
        c.style.position = "absolute";
        zn.call(this, b, c);
        this.b = null;
        this.c = Jd()
    }

    v(An, zn);
    An.prototype.Ua = function (b, c, d, e) {
        var f = this.a;
        return f.a().Hd(b, c.viewState.resolution, c.viewState.rotation, c.skippedFeatureUids, function (b) {
            return d.call(e, b, f)
        })
    };
    An.prototype.f = function () {
        Ff(this.target);
        this.b = null
    };
    An.prototype.e = function (b, c) {
        var d = b.viewState, e = d.center, f = d.resolution, g = d.rotation, h = this.b, k = this.a.a(),
            n = b.viewHints, p = b.extent;
        m(c.extent) && (p = oe(p, c.extent));
        n[0] || n[1] || re(p) || (d = d.projection, n = k.g, null === n || (d = n), p = k.sc(p, f, b.pixelRatio, d), null === p || nj(this, p) && (h = p));
        null !== h && (d = h.J(), n = h.resolution, p = Hd(), hj(p, b.size[0] / 2, b.size[1] / 2, n / f, n / f, g, (d[0] - e[0]) / n, (e[1] - d[3]) / n), h != this.b && (e = h.a(this), e.style.maxWidth = "none", e.style.position = "absolute", Ff(this.target), this.target.appendChild(e),
            this.b = h), ij(p, this.c) || (Sf(this.target, p), Kd(this.c, p)), pj(b.attributions, h.e), qj(b, k));
        return !0
    };

    function Bn(b) {
        var c = Df("DIV");
        c.style.position = "absolute";
        zn.call(this, b, c);
        this.c = !0;
        this.i = 1;
        this.g = 0;
        this.b = {}
    }

    v(Bn, zn);
    Bn.prototype.f = function () {
        Ff(this.target);
        this.g = 0
    };
    Bn.prototype.e = function (b, c) {
        if (!c.visible) return this.c && (Mg(this.target, !1), this.c = !1), !0;
        var d = b.pixelRatio, e = b.viewState, f = e.projection, g = this.a, h = g.a(), k = fj(h, f), n = h.hd(),
            p = ac(k.a, e.resolution, 0), q = k.na(p), r = e.center, s;
        q == e.resolution ? (r = sj(r, q, b.size), s = me(r, q, e.rotation, b.size)) : s = b.extent;
        m(c.extent) && (s = oe(s, c.extent));
        var q = Zi(k, s, q), u = {};
        u[p] = {};
        var z = this.dd(h, u), y = g.ea(), A = Sd(), D = new lf(0, 0, 0, 0), x, M, Q, O;
        for (Q = q.a; Q <= q.c; ++Q) for (O = q.b; O <= q.d; ++O) x = h.Vb(p, Q, O, d, f), M = x.state, 2 == M ? u[p][kf(x.a)] =
            x : 4 == M || 3 == M && !y || (M = k.fd(x.a, z, null, D, A), M || (x = k.sd(x.a, D, A), null === x || z(p + 1, x)));
        var W;
        if (this.g != h.d) {
            for (W in this.b) y = this.b[+W], Hf(y.target);
            this.b = {};
            this.g = h.d
        }
        A = Ra(rb(u), Number);
        cb(A);
        var z = {}, Ja;
        Q = 0;
        for (O = A.length; Q < O; ++Q) {
            W = A[Q];
            W in this.b ? y = this.b[W] : (y = k.Mc(r, W), y = new Cn(k, y), z[W] = !0, this.b[W] = y);
            W = u[W];
            for (Ja in W) {
                x = y;
                M = W[Ja];
                var lb = n, Ka = M.a, mb = Ka[0], Pb = Ka[1], Ya = Ka[2], Ka = kf(Ka);
                if (!(Ka in x.d)) {
                    var mb = x.c.ua(mb), Ub = M.Ta(x), nb = Ub.style;
                    nb.maxWidth = "none";
                    var Mc = void 0, sc = void 0;
                    0 < lb ?
                        (Mc = Df("DIV"), sc = Mc.style, sc.overflow = "hidden", sc.width = mb + "px", sc.height = mb + "px", nb.position = "absolute", nb.left = -lb + "px", nb.top = -lb + "px", nb.width = mb + 2 * lb + "px", nb.height = mb + 2 * lb + "px", Mc.appendChild(Ub)) : (nb.width = mb + "px", nb.height = mb + "px", Mc = Ub, sc = nb);
                    sc.position = "absolute";
                    sc.left = (Pb - x.b[1]) * mb + "px";
                    sc.top = (x.b[2] - Ya) * mb + "px";
                    null === x.a && (x.a = document.createDocumentFragment());
                    x.a.appendChild(Mc);
                    x.d[Ka] = M
                }
            }
            null !== y.a && (y.target.appendChild(y.a), y.a = null)
        }
        n = Ra(rb(this.b), Number);
        cb(n);
        Q = Hd();
        Ja =
            0;
        for (A = n.length; Ja < A; ++Ja) if (W = n[Ja], y = this.b[W], W in u) if (x = y.g, O = y.e, hj(Q, b.size[0] / 2, b.size[1] / 2, x / e.resolution, x / e.resolution, e.rotation, (O[0] - r[0]) / x, (r[1] - O[1]) / x), O = y, x = Q, ij(x, O.f) || (Sf(O.target, x), Kd(O.f, x)), W in z) {
            for (--W; 0 <= W; --W) if (W in this.b) {
                O = this.b[W].target;
                O.parentNode && O.parentNode.insertBefore(y.target, O.nextSibling);
                break
            }
            0 > W && Gf(this.target, y.target, 0)
        } else {
            if (!b.viewHints[0] && !b.viewHints[1]) {
                M = Yi(y.c, s, y.b[0], D);
                W = [];
                x = O = void 0;
                for (x in y.d) O = y.d[x], M.contains(O.a) || W.push(O);
                lb = M = void 0;
                M = 0;
                for (lb = W.length; M < lb; ++M) O = W[M], x = kf(O.a), Hf(O.Ta(y)), delete y.d[x]
            }
        } else Hf(y.target), delete this.b[W];
        c.opacity != this.i && (this.i = this.target.style.opacity = c.opacity);
        c.visible && !this.c && (Mg(this.target, !0), this.c = !0);
        rj(b.usedTiles, h, p, q);
        tj(b, h, k, d, f, s, p, g.r());
        oj(b, h);
        qj(b, h);
        return !0
    };

    function Cn(b, c) {
        this.target = Df("DIV");
        this.target.style.position = "absolute";
        this.target.style.width = "100%";
        this.target.style.height = "100%";
        this.c = b;
        this.b = c;
        this.e = je(Xi(b, c));
        this.g = b.na(c[0]);
        this.d = {};
        this.a = null;
        this.f = Jd()
    };

    function Dn(b) {
        this.g = Nf();
        var c = this.g.canvas;
        c.style.maxWidth = "none";
        c.style.position = "absolute";
        zn.call(this, b, c);
        this.c = !1;
        this.o = -1;
        this.q = NaN;
        this.i = Sd();
        this.b = this.j = null;
        this.r = Hd();
        this.p = Hd()
    }

    v(Dn, zn);
    Dn.prototype.n = function (b, c) {
        var d = b.viewState, e = d.center, f = d.rotation, g = d.resolution, d = b.pixelRatio, h = b.size[0],
            k = b.size[1], n = h * d, p = k * d, e = hj(this.r, d * h / 2, d * k / 2, d / g, -d / g, -f, -e[0], -e[1]),
            g = this.g;
        g.canvas.width = n;
        g.canvas.height = p;
        h = hj(this.p, 0, 0, 1 / d, 1 / d, 0, -(n - h) / 2 * d, -(p - k) / 2 * d);
        Sf(g.canvas, h);
        En(this, "precompose", b, e);
        h = this.b;
        null === h || h.la() || (g.globalAlpha = c.opacity, zm(h, g, d, e, f, b.skippedFeatureUids), En(this, "render", b, e));
        En(this, "postcompose", b, e)
    };

    function En(b, c, d, e) {
        var f = b.g;
        b = b.a;
        jd(b, c) && (e = new Tl(f, d.pixelRatio, d.extent, e, d.viewState.rotation), b.dispatchEvent(new Zk(c, b, e, null, d, f, null)), fm(e))
    }

    Dn.prototype.Ua = function (b, c, d, e) {
        if (null !== this.b) {
            var f = this.a, g = {};
            return this.b.b(b, c.viewState.resolution, c.viewState.rotation, c.skippedFeatureUids, function (b) {
                var c = ma(b).toString();
                if (!(c in g)) return g[c] = !0, d.call(e, b, f)
            })
        }
    };
    Dn.prototype.D = function () {
        mj(this)
    };
    Dn.prototype.e = function (b) {
        function c(b) {
            var c;
            m(b.a) ? c = b.a.call(b, k) : m(d.r) && (c = (0, d.r)(b, k));
            if (null != c) {
                if (null != c) {
                    var e, f, g = !1;
                    e = 0;
                    for (f = c.length; e < f; ++e) g = Zm(q, b, c[e], Ym(k, n), this.D, this) || g;
                    b = g
                } else b = !1;
                this.c = this.c || b
            }
        }

        var d = this.a, e = d.a();
        pj(b.attributions, e.f);
        qj(b, e);
        if (!this.c && (!d.Ac && b.viewHints[0] || b.viewHints[1])) return !0;
        var f = b.extent, g = b.viewState, h = g.projection, k = g.resolution, n = b.pixelRatio;
        b = d.d;
        var p = d.ea, g = d.get("renderOrder");
        m(g) || (g = Xm);
        f = Wd(f, p * k);
        if (!this.c && this.q ==
            k && this.o == b && this.j == g && Zd(this.i, f)) return !0;
        pc(this.b);
        this.b = null;
        this.c = !1;
        var q = new vm(.5 * k / n, f, k, d.ea);
        e.Hb(f, k, h);
        if (null === g) e.Fb(f, k, c, this); else {
            var r = [];
            e.Fb(f, k, function (b) {
                r.push(b)
            }, this);
            cb(r, g);
            Pa(r, c, this)
        }
        wm(q);
        this.q = k;
        this.o = b;
        this.j = g;
        this.i = f;
        this.b = q;
        return !0
    };

    function Fn(b, c) {
        zj.call(this, 0, c);
        this.d = null;
        this.d = Nf();
        var d = this.d.canvas;
        d.style.position = "absolute";
        d.style.width = "100%";
        d.style.height = "100%";
        d.className = "ol-unselectable";
        Gf(b, d, 0);
        this.f = Hd();
        this.a = Df("DIV");
        this.a.className = "ol-unselectable";
        d = this.a.style;
        d.position = "absolute";
        d.width = "100%";
        d.height = "100%";
        w(this.a, "touchstart", tc);
        Gf(b, this.a, 0);
        this.c = !0
    }

    v(Fn, zj);
    Fn.prototype.P = function () {
        Hf(this.a);
        Fn.T.P.call(this)
    };
    Fn.prototype.le = function (b) {
        if (b instanceof H) b = new An(b); else if (b instanceof I) b = new Bn(b); else if (b instanceof J) b = new Dn(b); else return null;
        return b
    };

    function Gn(b, c, d) {
        var e = b.g;
        if (jd(e, c)) {
            var f = d.extent, g = d.pixelRatio, h = d.viewState, k = h.resolution, n = h.rotation, p = b.d,
                q = p.canvas;
            hj(b.f, q.width / 2, q.height / 2, g / h.resolution, -g / h.resolution, -h.rotation, -h.center[0], -h.center[1]);
            h = new Tl(p, g, f, b.f, n);
            f = new vm(.5 * k / g, f, k);
            e.dispatchEvent(new Zk(c, e, h, f, d, p, null));
            wm(f);
            f.la() || zm(f, p, g, b.f, n, {});
            fm(h);
            b.b = f
        }
    }

    Fn.prototype.O = function () {
        return "dom"
    };
    Fn.prototype.Wd = function (b) {
        if (null === b) this.c && (Mg(this.a, !1), this.c = !1); else {
            var c;
            c = function (b, c) {
                Gf(this.a, b, c)
            };
            var d = this.g;
            if (jd(d, "precompose") || jd(d, "postcompose")) {
                var d = this.d.canvas, e = b.pixelRatio;
                d.width = b.size[0] * e;
                d.height = b.size[1] * e
            }
            Gn(this, "precompose", b);
            var d = b.layerStatesArray, f, g, h, e = 0;
            for (f = d.length; e < f; ++e) h = d[e], g = h.layer, g = Cj(this, g), c.call(this, g.target, e), "ready" == h.yc ? g.e(b, h) && g.n(b, h) : g.f();
            c = b.layerStates;
            for (var k in this.e) k in c || (g = this.e[k], Hf(g.target));
            this.c ||
            (Mg(this.a, !0), this.c = !0);
            Aj(b);
            Dj(this, b);
            b.postRenderFunctions.push(Bj);
            Gn(this, "postcompose", b)
        }
    };

    function Hn(b) {
        this.a = b
    }

    function In(b) {
        this.a = b
    }

    v(In, Hn);
    In.prototype.O = function () {
        return 35632
    };

    function Jn(b) {
        this.a = b
    }

    v(Jn, Hn);
    Jn.prototype.O = function () {
        return 35633
    };

    function Kn() {
        this.a = "precision mediump float;varying vec2 a;varying float b;uniform mat4 k;uniform float l;uniform sampler2D m;void main(void){vec4 texColor=texture2D(m,a);float alpha=texColor.a*b*l;if(alpha==0.0){discard;}gl_FragColor.a=alpha;gl_FragColor.rgb=(k*vec4(texColor.rgb,1.)).rgb;}"
    }

    v(Kn, In);
    da(Kn);

    function Ln() {
        this.a = "varying vec2 a;varying float b;attribute vec2 c;attribute vec2 d;attribute vec2 e;attribute float f;attribute float g;uniform mat4 h;uniform mat4 i;uniform mat4 j;void main(void){mat4 offsetMatrix=i;if(g==1.0){offsetMatrix=i*j;}vec4 offsets=offsetMatrix*vec4(e,0.,0.);gl_Position=h*vec4(c,0.,1.)+offsets;a=d;b=f;}"
    }

    v(Ln, Jn);
    da(Ln);

    function Mn(b, c) {
        this.j = b.getUniformLocation(c, "k");
        this.n = b.getUniformLocation(c, "j");
        this.i = b.getUniformLocation(c, "i");
        this.e = b.getUniformLocation(c, "l");
        this.g = b.getUniformLocation(c, "h");
        this.a = b.getAttribLocation(c, "e");
        this.d = b.getAttribLocation(c, "f");
        this.c = b.getAttribLocation(c, "c");
        this.b = b.getAttribLocation(c, "g");
        this.f = b.getAttribLocation(c, "d")
    };

    function Nn() {
        this.a = "precision mediump float;varying vec2 a;varying float b;uniform float k;uniform sampler2D l;void main(void){vec4 texColor=texture2D(l,a);gl_FragColor.rgb=texColor.rgb;float alpha=texColor.a*b*k;if(alpha==0.0){discard;}gl_FragColor.a=alpha;}"
    }

    v(Nn, In);
    da(Nn);

    function On() {
        this.a = "varying vec2 a;varying float b;attribute vec2 c;attribute vec2 d;attribute vec2 e;attribute float f;attribute float g;uniform mat4 h;uniform mat4 i;uniform mat4 j;void main(void){mat4 offsetMatrix=i;if(g==1.0){offsetMatrix=i*j;}vec4 offsets=offsetMatrix*vec4(e,0.,0.);gl_Position=h*vec4(c,0.,1.)+offsets;a=d;b=f;}"
    }

    v(On, Jn);
    da(On);

    function Pn(b, c) {
        this.n = b.getUniformLocation(c, "j");
        this.i = b.getUniformLocation(c, "i");
        this.e = b.getUniformLocation(c, "k");
        this.g = b.getUniformLocation(c, "h");
        this.a = b.getAttribLocation(c, "e");
        this.d = b.getAttribLocation(c, "f");
        this.c = b.getAttribLocation(c, "c");
        this.b = b.getAttribLocation(c, "g");
        this.f = b.getAttribLocation(c, "d")
    };

    function Qn(b) {
        this.a = m(b) ? b : [];
        this.d = m(void 0) ? void 0 : 35044
    };

    function Rn(b, c) {
        this.j = b;
        this.a = c;
        this.d = {};
        this.e = {};
        this.f = {};
        this.n = this.i = this.c = this.g = null;
        (this.b = Va(va, "OES_element_index_uint")) && c.getExtension("OES_element_index_uint");
        w(this.j, "webglcontextlost", this.bl, !1, this);
        w(this.j, "webglcontextrestored", this.cl, !1, this)
    }

    function Sn(b, c, d) {
        var e = b.a, f = d.a, g = ma(d);
        if (g in b.d) e.bindBuffer(c, b.d[g].buffer); else {
            var h = e.createBuffer();
            e.bindBuffer(c, h);
            var k;
            34962 == c ? k = new Float32Array(f) : 34963 == c && (k = b.b ? new Uint32Array(f) : new Uint16Array(f));
            e.bufferData(c, k, d.d);
            b.d[g] = {b: d, buffer: h}
        }
    }

    function Tn(b, c) {
        var d = b.a, e = ma(c), f = b.d[e];
        d.isContextLost() || d.deleteBuffer(f.buffer);
        delete b.d[e]
    }

    l = Rn.prototype;
    l.P = function () {
        var b = this.a;
        b.isContextLost() || (kb(this.d, function (c) {
            b.deleteBuffer(c.buffer)
        }), kb(this.f, function (c) {
            b.deleteProgram(c)
        }), kb(this.e, function (c) {
            b.deleteShader(c)
        }), b.deleteFramebuffer(this.c), b.deleteRenderbuffer(this.n), b.deleteTexture(this.i))
    };
    l.al = function () {
        return this.a
    };
    l.qe = function () {
        if (null === this.c) {
            var b = this.a, c = b.createFramebuffer();
            b.bindFramebuffer(b.FRAMEBUFFER, c);
            var d = Un(b, 1, 1), e = b.createRenderbuffer();
            b.bindRenderbuffer(b.RENDERBUFFER, e);
            b.renderbufferStorage(b.RENDERBUFFER, b.DEPTH_COMPONENT16, 1, 1);
            b.framebufferTexture2D(b.FRAMEBUFFER, b.COLOR_ATTACHMENT0, b.TEXTURE_2D, d, 0);
            b.framebufferRenderbuffer(b.FRAMEBUFFER, b.DEPTH_ATTACHMENT, b.RENDERBUFFER, e);
            b.bindTexture(b.TEXTURE_2D, null);
            b.bindRenderbuffer(b.RENDERBUFFER, null);
            b.bindFramebuffer(b.FRAMEBUFFER,
                null);
            this.c = c;
            this.i = d;
            this.n = e
        }
        return this.c
    };

    function Vn(b, c) {
        var d = ma(c);
        if (d in b.e) return b.e[d];
        var e = b.a, f = e.createShader(c.O());
        e.shaderSource(f, c.a);
        e.compileShader(f);
        return b.e[d] = f
    }

    function Wn(b, c, d) {
        var e = ma(c) + "/" + ma(d);
        if (e in b.f) return b.f[e];
        var f = b.a, g = f.createProgram();
        f.attachShader(g, Vn(b, c));
        f.attachShader(g, Vn(b, d));
        f.linkProgram(g);
        return b.f[e] = g
    }

    l.bl = function () {
        wb(this.d);
        wb(this.e);
        wb(this.f);
        this.n = this.i = this.c = this.g = null
    };
    l.cl = function () {
    };
    l.Pd = function (b) {
        if (b == this.g) return !1;
        this.a.useProgram(b);
        this.g = b;
        return !0
    };

    function Xn(b, c, d) {
        var e = b.createTexture();
        b.bindTexture(b.TEXTURE_2D, e);
        b.texParameteri(b.TEXTURE_2D, b.TEXTURE_MAG_FILTER, b.LINEAR);
        b.texParameteri(b.TEXTURE_2D, b.TEXTURE_MIN_FILTER, b.LINEAR);
        m(c) && b.texParameteri(3553, 10242, c);
        m(d) && b.texParameteri(3553, 10243, d);
        return e
    }

    function Un(b, c, d) {
        var e = Xn(b, void 0, void 0);
        b.texImage2D(b.TEXTURE_2D, 0, b.RGBA, c, d, 0, b.RGBA, b.UNSIGNED_BYTE, null);
        return e
    }

    function Yn(b, c) {
        var d = Xn(b, 33071, 33071);
        b.texImage2D(b.TEXTURE_2D, 0, b.RGBA, b.RGBA, b.UNSIGNED_BYTE, c);
        return d
    };

    function Zn(b, c) {
        this.r = this.p = void 0;
        this.Ea = new ug;
        this.i = ke(c);
        this.o = [];
        this.e = [];
        this.N = void 0;
        this.f = [];
        this.c = [];
        this.W = this.S = void 0;
        this.d = [];
        this.H = this.D = this.n = null;
        this.ca = void 0;
        this.ka = Jd();
        this.va = Jd();
        this.oa = this.da = void 0;
        this.Fa = Jd();
        this.Da = this.fa = this.pa = void 0;
        this.ia = [];
        this.g = [];
        this.a = [];
        this.q = null;
        this.b = [];
        this.j = [];
        this.ea = void 0
    }

    function $n(b, c) {
        var d = b.q, e = b.n, f = b.ia, g = b.g, h = c.a;
        return function () {
            if (!h.isContextLost()) {
                var b, n;
                b = 0;
                for (n = f.length; b < n; ++b) h.deleteTexture(f[b]);
                b = 0;
                for (n = g.length; b < n; ++b) h.deleteTexture(g[b])
            }
            Tn(c, d);
            Tn(c, e)
        }
    }

    function ao(b, c, d, e) {
        var f = b.p, g = b.r, h = b.N, k = b.S, n = b.W, p = b.ca, q = b.da, r = b.oa, s = b.pa ? 1 : 0, u = b.fa,
            z = b.Da, y = b.ea, A = Math.cos(u), u = Math.sin(u), D = b.d.length, x = b.a.length, M, Q, O, W, Ja, lb;
        for (M = 0; M < d; M += e) Ja = c[M] - b.i[0], lb = c[M + 1] - b.i[1], Q = x / 8, O = -z * f, W = -z * (h - g), b.a[x++] = Ja, b.a[x++] = lb, b.a[x++] = O * A - W * u, b.a[x++] = O * u + W * A, b.a[x++] = q / n, b.a[x++] = (r + h) / k, b.a[x++] = p, b.a[x++] = s, O = z * (y - f), W = -z * (h - g), b.a[x++] = Ja, b.a[x++] = lb, b.a[x++] = O * A - W * u, b.a[x++] = O * u + W * A, b.a[x++] = (q + y) / n, b.a[x++] = (r + h) / k, b.a[x++] = p, b.a[x++] = s, O = z * (y -
            f), W = z * g, b.a[x++] = Ja, b.a[x++] = lb, b.a[x++] = O * A - W * u, b.a[x++] = O * u + W * A, b.a[x++] = (q + y) / n, b.a[x++] = r / k, b.a[x++] = p, b.a[x++] = s, O = -z * f, W = z * g, b.a[x++] = Ja, b.a[x++] = lb, b.a[x++] = O * A - W * u, b.a[x++] = O * u + W * A, b.a[x++] = q / n, b.a[x++] = r / k, b.a[x++] = p, b.a[x++] = s, b.d[D++] = Q, b.d[D++] = Q + 1, b.d[D++] = Q + 2, b.d[D++] = Q, b.d[D++] = Q + 2, b.d[D++] = Q + 3
    }

    l = Zn.prototype;
    l.tb = function (b, c) {
        this.b.push(this.d.length);
        this.j.push(c);
        var d = b.k;
        ao(this, d, d.length, b.B)
    };
    l.ub = function (b, c) {
        this.b.push(this.d.length);
        this.j.push(c);
        var d = b.k;
        ao(this, d, d.length, b.B)
    };
    l.Kb = function (b) {
        var c = b.a;
        this.o.push(this.d.length);
        this.e.push(this.d.length);
        this.q = new Qn(this.a);
        Sn(b, 34962, this.q);
        this.n = new Qn(this.d);
        Sn(b, 34963, this.n);
        b = {};
        bo(this.ia, this.f, b, c);
        bo(this.g, this.c, b, c);
        this.N = this.r = this.p = void 0;
        this.c = this.f = null;
        this.W = this.S = void 0;
        this.d = null;
        this.Da = this.fa = this.pa = this.oa = this.da = this.ca = void 0;
        this.a = null;
        this.ea = void 0
    };

    function bo(b, c, d, e) {
        var f, g, h, k = c.length;
        for (h = 0; h < k; ++h) f = c[h], g = ma(f).toString(), g in d ? f = d[g] : (f = Yn(e, f), d[g] = f), b[h] = f
    }

    l.ac = function (b, c, d, e, f, g, h, k, n, p, q, r, s, u, z) {
        g = b.a;
        Sn(b, 34962, this.q);
        Sn(b, 34963, this.n);
        var y = k || 1 != n || p || 1 != q, A, D;
        y ? (A = Kn.Pa(), D = Ln.Pa()) : (A = Nn.Pa(), D = On.Pa());
        D = Wn(b, A, D);
        y ? null === this.D ? this.D = A = new Mn(g, D) : A = this.D : null === this.H ? this.H = A = new Pn(g, D) : A = this.H;
        b.Pd(D);
        g.enableVertexAttribArray(A.c);
        g.vertexAttribPointer(A.c, 2, 5126, !1, 32, 0);
        g.enableVertexAttribArray(A.a);
        g.vertexAttribPointer(A.a, 2, 5126, !1, 32, 8);
        g.enableVertexAttribArray(A.f);
        g.vertexAttribPointer(A.f, 2, 5126, !1, 32, 16);
        g.enableVertexAttribArray(A.d);
        g.vertexAttribPointer(A.d, 1, 5126, !1, 32, 24);
        g.enableVertexAttribArray(A.b);
        g.vertexAttribPointer(A.b, 1, 5126, !1, 32, 28);
        D = this.Fa;
        hj(D, 0, 0, 2 / (d * f[0]), 2 / (d * f[1]), -e, -(c[0] - this.i[0]), -(c[1] - this.i[1]));
        c = this.va;
        d = 2 / f[0];
        f = 2 / f[1];
        Ld(c);
        c[0] = d;
        c[5] = f;
        c[10] = 1;
        c[15] = 1;
        f = this.ka;
        Ld(f);
        0 !== e && Qd(f, -e);
        g.uniformMatrix4fv(A.g, !1, D);
        g.uniformMatrix4fv(A.i, !1, c);
        g.uniformMatrix4fv(A.n, !1, f);
        g.uniform1f(A.e, h);
        y && g.uniformMatrix4fv(A.j, !1, vg(this.Ea, k, n, p, q));
        var x;
        if (m(s)) {
            if (u) a:{
                e = b.b ? 5125 : 5123;
                b = b.b ? 4 : 2;
                p =
                    this.b.length - 1;
                for (h = this.g.length - 1; 0 <= h; --h) for (g.bindTexture(3553, this.g[h]), k = 0 < h ? this.e[h - 1] : 0, q = this.e[h]; 0 <= p && this.b[p] >= k;) {
                    n = this.b[p];
                    u = this.j[p];
                    x = ma(u).toString();
                    if (!m(r[x]) && (!m(z) || pe(z, u.R().J())) && (g.clear(g.COLOR_BUFFER_BIT | g.DEPTH_BUFFER_BIT), g.drawElements(4, q - n, e, n * b), q = s(u))) {
                        r = q;
                        break a
                    }
                    q = n;
                    p--
                }
                r = void 0
            } else g.clear(g.COLOR_BUFFER_BIT | g.DEPTH_BUFFER_BIT), co(this, g, b, r, this.g, this.e), r = (r = s(null)) ? r : void 0;
            x = r
        } else co(this, g, b, r, this.ia, this.o);
        g.disableVertexAttribArray(A.c);
        g.disableVertexAttribArray(A.a);
        g.disableVertexAttribArray(A.f);
        g.disableVertexAttribArray(A.d);
        g.disableVertexAttribArray(A.b);
        return x
    };

    function co(b, c, d, e, f, g) {
        var h = d.b ? 5125 : 5123;
        d = d.b ? 4 : 2;
        if (vb(e)) {
            var k;
            b = 0;
            e = f.length;
            for (k = 0; b < e; ++b) {
                c.bindTexture(3553, f[b]);
                var n = g[b];
                c.drawElements(4, n - k, h, k * d);
                k = n
            }
        } else {
            k = 0;
            var p, n = 0;
            for (p = f.length; n < p; ++n) {
                c.bindTexture(3553, f[n]);
                for (var q = 0 < n ? g[n - 1] : 0, r = g[n], s = q; k < b.b.length && b.b[k] <= r;) {
                    var u = ma(b.j[k]).toString();
                    m(e[u]) ? (s !== q && c.drawElements(4, q - s, h, s * d), q = s = k === b.b.length - 1 ? r : b.b[k + 1]) : q = k === b.b.length - 1 ? r : b.b[k + 1];
                    k++
                }
                s !== q && c.drawElements(4, q - s, h, s * d)
            }
        }
    }

    l.ib = function (b) {
        var c = b.wb(), d = b.Bb(1), e = b.jd(), f = b.Id(1), g = b.o, h = b.Cb(), k = b.p, n = b.i, p = b.gb();
        b = b.j;
        var q;
        0 === this.f.length ? this.f.push(d) : (q = this.f[this.f.length - 1], ma(q) != ma(d) && (this.o.push(this.d.length), this.f.push(d)));
        0 === this.c.length ? this.c.push(f) : (q = this.c[this.c.length - 1], ma(q) != ma(f) && (this.e.push(this.d.length), this.c.push(f)));
        this.p = c[0];
        this.r = c[1];
        this.N = p[1];
        this.S = e[1];
        this.W = e[0];
        this.ca = g;
        this.da = h[0];
        this.oa = h[1];
        this.fa = n;
        this.pa = k;
        this.Da = b;
        this.ea = p[0]
    };

    function eo(b, c, d) {
        this.f = c;
        this.e = b;
        this.c = d;
        this.d = {}
    }

    function fo(b, c) {
        var d = [], e;
        for (e in b.d) d.push($n(b.d[e], c));
        return fd.apply(null, d)
    }

    function go(b, c) {
        for (var d in b.d) b.d[d].Kb(c)
    }

    eo.prototype.a = function (b, c) {
        var d = this.d[c];
        m(d) || (d = new ho[c](this.e, this.f), this.d[c] = d);
        return d
    };
    eo.prototype.la = function () {
        return vb(this.d)
    };

    function io(b, c, d, e, f, g, h, k, n, p, q, r, s, u, z) {
        var y = jo, A, D;
        for (A = gm.length - 1; 0 <= A; --A) if (D = b.d[gm[A]], m(D) && (D = D.ac(c, d, e, f, y, g, h, k, n, p, q, r, s, u, z))) return D
    }

    eo.prototype.b = function (b, c, d, e, f, g, h, k, n, p, q, r, s, u) {
        var z = c.a;
        z.bindFramebuffer(z.FRAMEBUFFER, c.qe());
        var y;
        m(this.c) && (y = Wd(be(b), e * this.c));
        return io(this, c, b, e, f, h, k, n, p, q, r, s, function (b) {
            var c = new Uint8Array(4);
            z.readPixels(0, 0, 1, 1, z.RGBA, z.UNSIGNED_BYTE, c);
            if (0 < c[3] && (b = u(b))) return b
        }, !0, y)
    };

    function ko(b, c, d, e, f, g, h, k, n, p, q, r) {
        var s = d.a;
        s.bindFramebuffer(s.FRAMEBUFFER, d.qe());
        b = io(b, d, c, e, f, g, h, k, n, p, q, r, function () {
            var b = new Uint8Array(4);
            s.readPixels(0, 0, 1, 1, s.RGBA, s.UNSIGNED_BYTE, b);
            return 0 < b[3]
        }, !1);
        return m(b)
    }

    var ho = {Image: Zn}, jo = [1, 1];

    function lo(b, c, d, e, f, g, h) {
        this.b = b;
        this.f = c;
        this.a = g;
        this.e = h;
        this.i = f;
        this.n = e;
        this.g = d;
        this.c = null;
        this.d = {}
    }

    l = lo.prototype;
    l.jc = function (b, c) {
        var d = b.toString(), e = this.d[d];
        m(e) ? e.push(c) : this.d[d] = [c]
    };
    l.kc = function () {
    };
    l.me = function (b, c) {
        var d = (0, c.c)(b);
        if (null != d && pe(this.a, d.J())) {
            var e = c.a;
            m(e) || (e = 0);
            this.jc(e, function (b) {
                b.Ba(c.f, c.b);
                b.ib(c.e);
                b.Ca(c.d);
                var e = mo[d.O()];
                e && e.call(b, d, null)
            })
        }
    };
    l.ed = function (b, c) {
        var d = b.c, e, f;
        e = 0;
        for (f = d.length; e < f; ++e) {
            var g = d[e], h = mo[g.O()];
            h && h.call(this, g, c)
        }
    };
    l.ub = function (b, c) {
        var d = this.b, e = (new eo(1, this.a)).a(0, "Image");
        e.ib(this.c);
        e.ub(b, c);
        e.Kb(d);
        e.ac(this.b, this.f, this.g, this.n, this.i, this.a, this.e, 1, 0, 1, 0, 1, {});
        $n(e, d)()
    };
    l.Eb = function () {
    };
    l.lc = function () {
    };
    l.tb = function (b, c) {
        var d = this.b, e = (new eo(1, this.a)).a(0, "Image");
        e.ib(this.c);
        e.tb(b, c);
        e.Kb(d);
        e.ac(this.b, this.f, this.g, this.n, this.i, this.a, this.e, 1, 0, 1, 0, 1, {});
        $n(e, d)()
    };
    l.mc = function () {
    };
    l.Rb = function () {
    };
    l.vb = function () {
    };
    l.Ba = function () {
    };
    l.ib = function (b) {
        this.c = b
    };
    l.Ca = function () {
    };
    var mo = {Point: lo.prototype.ub, MultiPoint: lo.prototype.tb, GeometryCollection: lo.prototype.ed};

    function no() {
        this.a = "precision mediump float;varying vec2 a;uniform mat4 f;uniform float g;uniform sampler2D h;void main(void){vec4 texColor=texture2D(h,a);gl_FragColor.rgb=(f*vec4(texColor.rgb,1.)).rgb;gl_FragColor.a=texColor.a*g;}"
    }

    v(no, In);
    da(no);

    function oo() {
        this.a = "varying vec2 a;attribute vec2 b;attribute vec2 c;uniform mat4 d;uniform mat4 e;void main(void){gl_Position=e*vec4(b,0.,1.);a=(d*vec4(c,0.,1.)).st;}"
    }

    v(oo, Jn);
    da(oo);

    function po(b, c) {
        this.g = b.getUniformLocation(c, "f");
        this.b = b.getUniformLocation(c, "g");
        this.c = b.getUniformLocation(c, "e");
        this.e = b.getUniformLocation(c, "d");
        this.f = b.getUniformLocation(c, "h");
        this.a = b.getAttribLocation(c, "b");
        this.d = b.getAttribLocation(c, "c")
    };

    function qo() {
        this.a = "precision mediump float;varying vec2 a;uniform float f;uniform sampler2D g;void main(void){vec4 texColor=texture2D(g,a);gl_FragColor.rgb=texColor.rgb;gl_FragColor.a=texColor.a*f;}"
    }

    v(qo, In);
    da(qo);

    function ro() {
        this.a = "varying vec2 a;attribute vec2 b;attribute vec2 c;uniform mat4 d;uniform mat4 e;void main(void){gl_Position=e*vec4(b,0.,1.);a=(d*vec4(c,0.,1.)).st;}"
    }

    v(ro, Jn);
    da(ro);

    function so(b, c) {
        this.b = b.getUniformLocation(c, "f");
        this.c = b.getUniformLocation(c, "e");
        this.e = b.getUniformLocation(c, "d");
        this.f = b.getUniformLocation(c, "g");
        this.a = b.getAttribLocation(c, "b");
        this.d = b.getAttribLocation(c, "c")
    };

    function to(b, c) {
        lj.call(this, c);
        this.b = b;
        this.N = new Qn([-1, -1, 0, 0, 1, -1, 1, 0, -1, 1, 0, 1, 1, 1, 1, 1]);
        this.f = this.Wa = null;
        this.e = void 0;
        this.i = Hd();
        this.o = Jd();
        this.S = new ug;
        this.q = this.j = null
    }

    v(to, lj);

    function uo(b, c, d) {
        var e = b.b.f;
        if (m(b.e) && b.e == d) e.bindFramebuffer(36160, b.f); else {
            c.postRenderFunctions.push(sa(function (b, c, d) {
                b.isContextLost() || (b.deleteFramebuffer(c), b.deleteTexture(d))
            }, e, b.f, b.Wa));
            c = Un(e, d, d);
            var f = e.createFramebuffer();
            e.bindFramebuffer(36160, f);
            e.framebufferTexture2D(36160, 36064, 3553, c, 0);
            b.Wa = c;
            b.f = f;
            b.e = d
        }
    }

    to.prototype.Lf = function (b, c, d) {
        vo(this, "precompose", d, b);
        Sn(d, 34962, this.N);
        var e = d.a, f = c.brightness || 1 != c.contrast || c.hue || 1 != c.saturation, g, h;
        f ? (g = no.Pa(), h = oo.Pa()) : (g = qo.Pa(), h = ro.Pa());
        g = Wn(d, g, h);
        f ? null === this.j ? this.j = h = new po(e, g) : h = this.j : null === this.q ? this.q = h = new so(e, g) : h = this.q;
        d.Pd(g) && (e.enableVertexAttribArray(h.a), e.vertexAttribPointer(h.a, 2, 5126, !1, 16, 0), e.enableVertexAttribArray(h.d), e.vertexAttribPointer(h.d, 2, 5126, !1, 16, 8), e.uniform1i(h.f, 0));
        e.uniformMatrix4fv(h.e, !1, this.i);
        e.uniformMatrix4fv(h.c, !1, this.o);
        f && e.uniformMatrix4fv(h.g, !1, vg(this.S, c.brightness, c.contrast, c.hue, c.saturation));
        e.uniform1f(h.b, c.opacity);
        e.bindTexture(3553, this.Wa);
        e.drawArrays(5, 0, 4);
        vo(this, "postcompose", d, b)
    };

    function vo(b, c, d, e) {
        b = b.a;
        if (jd(b, c)) {
            var f = e.viewState;
            b.dispatchEvent(new Zk(c, b, new lo(d, f.center, f.resolution, f.rotation, e.size, e.extent, e.pixelRatio), null, e, null, d))
        }
    }

    to.prototype.Be = function () {
        this.f = this.Wa = null;
        this.e = void 0
    };

    function wo(b, c) {
        to.call(this, b, c);
        this.n = this.g = this.c = null
    }

    v(wo, to);

    function xo(b, c) {
        var d = c.a();
        return Yn(b.b.f, d)
    }

    wo.prototype.Ua = function (b, c, d, e) {
        var f = this.a;
        return f.a().Hd(b, c.viewState.resolution, c.viewState.rotation, c.skippedFeatureUids, function (b) {
            return d.call(e, b, f)
        })
    };
    wo.prototype.Ce = function (b, c) {
        var d = this.b.f, e = b.viewState, f = e.center, g = e.resolution, h = e.rotation, k = this.c, n = this.Wa,
            p = this.a.a(), q = b.viewHints, r = b.extent;
        m(c.extent) && (r = oe(r, c.extent));
        q[0] || q[1] || re(r) || (e = e.projection, q = p.g, null === q || (e = q), r = p.sc(r, g, b.pixelRatio, e), null !== r && nj(this, r) && (k = r, n = xo(this, r), null === this.Wa || b.postRenderFunctions.push(sa(function (b, c) {
            b.isContextLost() || b.deleteTexture(c)
        }, d, this.Wa))));
        null !== k && (d = this.b.c.j, yo(this, d.width, d.height, f, g, h, k.J()), this.n = null, f = this.i,
            Ld(f), Pd(f, 1, -1), Od(f, 0, -1), this.c = k, this.Wa = n, pj(b.attributions, k.e), qj(b, p));
        return !0
    };

    function yo(b, c, d, e, f, g, h) {
        c *= f;
        d *= f;
        b = b.o;
        Ld(b);
        Pd(b, 2 / c, 2 / d);
        Qd(b, -g);
        Od(b, h[0] - e[0], h[1] - e[1]);
        Pd(b, (h[2] - h[0]) / 2, (h[3] - h[1]) / 2);
        Od(b, 1, 1)
    }

    wo.prototype.Fd = function (b, c) {
        var d = this.Ua(b, c, bd, this);
        return m(d)
    };
    wo.prototype.bc = function (b, c, d, e) {
        if (null !== this.c && !fa(this.c.a())) if (this.a.a() instanceof sn) {
            if (b = b.slice(), kj(c.pixelToCoordinateMatrix, b, b), this.Ua(b, c, bd, this)) return d.call(e, this.a)
        } else {
            var f = [this.c.a().width, this.c.a().height];
            if (null === this.n) {
                var g = c.size;
                c = Hd();
                Ld(c);
                Od(c, -1, -1);
                Pd(c, 2 / g[0], 2 / g[1]);
                Od(c, 0, g[1]);
                Pd(c, 1, -1);
                g = Hd();
                Nd(this.o, g);
                var h = Hd();
                Ld(h);
                Od(h, 0, f[1]);
                Pd(h, 1, -1);
                Pd(h, f[0] / 2, f[1] / 2);
                Od(h, 1, 1);
                var k = Hd();
                Md(h, g, k);
                Md(k, c, k);
                this.n = k
            }
            c = [0, 0];
            kj(this.n, b, c);
            if (!(0 > c[0] ||
                c[0] > f[0] || 0 > c[1] || c[1] > f[1]) && (null === this.g && (this.g = Nf(1, 1)), this.g.clearRect(0, 0, 1, 1), this.g.drawImage(this.c.a(), c[0], c[1], 1, 1, 0, 0, 1, 1), 0 < this.g.getImageData(0, 0, 1, 1).data[3])) return d.call(e, this.a)
        }
    };

    function zo() {
        this.a = "precision mediump float;varying vec2 a;uniform sampler2D e;void main(void){gl_FragColor=texture2D(e,a);}"
    }

    v(zo, In);
    da(zo);

    function Ao() {
        this.a = "varying vec2 a;attribute vec2 b;attribute vec2 c;uniform vec4 d;void main(void){gl_Position=vec4(b*d.xy+d.zw,0.,1.);a=c;}"
    }

    v(Ao, Jn);
    da(Ao);

    function Bo(b, c) {
        this.b = b.getUniformLocation(c, "e");
        this.c = b.getUniformLocation(c, "d");
        this.a = b.getAttribLocation(c, "b");
        this.d = b.getAttribLocation(c, "c")
    };

    function Co(b, c) {
        to.call(this, b, c);
        this.D = zo.Pa();
        this.H = Ao.Pa();
        this.c = null;
        this.r = new Qn([0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0]);
        this.p = this.g = null;
        this.n = -1
    }

    v(Co, to);
    l = Co.prototype;
    l.P = function () {
        Tn(this.b.c, this.r);
        Co.T.P.call(this)
    };
    l.dd = function (b, c) {
        var d = this.b;
        return function (e, f) {
            return ej(b, e, f, function (b) {
                var f = Si(d.d, b.qb());
                f && (c[e] || (c[e] = {}), c[e][b.a.toString()] = b);
                return f
            })
        }
    };
    l.Be = function () {
        Co.T.Be.call(this);
        this.c = null
    };
    l.Ce = function (b, c, d) {
        var e = this.b, f = d.a, g = b.viewState, h = g.projection, k = this.a, n = k.a(), p = fj(n, h),
            q = ac(p.a, g.resolution, 0), r = p.na(q), s = n.Nc(q, b.pixelRatio, h), u = s / p.ua(q), z = r / u,
            y = n.hd(), A = g.center, D;
        r == g.resolution ? (A = sj(A, r, b.size), D = me(A, r, g.rotation, b.size)) : D = b.extent;
        r = Zi(p, D, r);
        if (null !== this.g && of(this.g, r) && this.n == n.d) z = this.p; else {
            var x = [r.c - r.a + 1, r.d - r.b + 1], x = Math.max(x[0] * s, x[1] * s),
                M = Math.pow(2, Math.ceil(Math.log(x) / Math.LN2)), x = z * M, Q = p.Lb(q), O = Q[0] + r.a * s * z,
                z = Q[1] + r.b * s * z, z = [O, z, O + x, z + x];
            uo(this, b, M);
            f.viewport(0, 0, M, M);
            f.clearColor(0, 0, 0, 0);
            f.clear(16384);
            f.disable(3042);
            M = Wn(d, this.D, this.H);
            d.Pd(M);
            null === this.c && (this.c = new Bo(f, M));
            Sn(d, 34962, this.r);
            f.enableVertexAttribArray(this.c.a);
            f.vertexAttribPointer(this.c.a, 2, 5126, !1, 16, 0);
            f.enableVertexAttribArray(this.c.d);
            f.vertexAttribPointer(this.c.d, 2, 5126, !1, 16, 8);
            f.uniform1i(this.c.b, 0);
            d = {};
            d[q] = {};
            var W = this.dd(n, d), Ja = k.ea(), M = !0, O = Sd(), lb = new lf(0, 0, 0, 0), Ka, mb, Pb;
            for (mb = r.a; mb <= r.c; ++mb) for (Pb = r.b; Pb <= r.d; ++Pb) {
                Q = n.Vb(q,
                    mb, Pb, u, h);
                if (m(c.extent) && (Ka = Xi(p, Q.a, O), !pe(Ka, c.extent))) continue;
                Ka = Q.state;
                if (2 == Ka) {
                    if (Si(e.d, Q.qb())) {
                        d[q][kf(Q.a)] = Q;
                        continue
                    }
                } else if (4 == Ka || 3 == Ka && !Ja) continue;
                M = !1;
                Ka = p.fd(Q.a, W, null, lb, O);
                Ka || (Q = p.sd(Q.a, lb, O), null === Q || W(q + 1, Q))
            }
            c = Ra(rb(d), Number);
            cb(c);
            for (var W = new Float32Array(4), Ya, Ub, nb, Ja = 0, lb = c.length; Ja < lb; ++Ja) for (Ya in Ub = d[c[Ja]], Ub) Q = Ub[Ya], Ka = Xi(p, Q.a, O), mb = 2 * (Ka[2] - Ka[0]) / x, Pb = 2 * (Ka[3] - Ka[1]) / x, nb = 2 * (Ka[0] - z[0]) / x - 1, Ka = 2 * (Ka[1] - z[1]) / x - 1, Gd(W, mb, Pb, nb, Ka), f.uniform4fv(this.c.c,
                W), Do(e, Q, s, y * u), f.drawArrays(5, 0, 4);
            M ? (this.g = r, this.p = z, this.n = n.d) : (this.p = this.g = null, this.n = -1, b.animate = !0)
        }
        rj(b.usedTiles, n, q, r);
        var Mc = e.i;
        tj(b, n, p, u, h, D, q, k.r(), function (b) {
            var c;
            (c = 2 != b.state || Si(e.d, b.qb())) || (c = b.qb() in Mc.b);
            c || uj(Mc, [b, aj(p, b.a), p.na(b.a[0]), s, y * u])
        }, this);
        oj(b, n);
        qj(b, n);
        f = this.i;
        Ld(f);
        Od(f, (A[0] - z[0]) / (z[2] - z[0]), (A[1] - z[1]) / (z[3] - z[1]));
        0 !== g.rotation && Qd(f, g.rotation);
        Pd(f, b.size[0] * g.resolution / (z[2] - z[0]), b.size[1] * g.resolution / (z[3] - z[1]));
        Od(f, -.5, -.5);
        return !0
    };
    l.bc = function (b, c, d, e) {
        if (null !== this.f) {
            var f = [0, 0];
            kj(this.i, [b[0] / c.size[0], (c.size[1] - b[1]) / c.size[1]], f);
            b = [f[0] * this.e, f[1] * this.e];
            c = this.b.c.a;
            c.bindFramebuffer(c.FRAMEBUFFER, this.f);
            f = new Uint8Array(4);
            c.readPixels(b[0], b[1], 1, 1, c.RGBA, c.UNSIGNED_BYTE, f);
            if (0 < f[3]) return d.call(e, this.a)
        }
    };

    function Eo(b, c) {
        to.call(this, b, c);
        this.n = !1;
        this.H = -1;
        this.D = NaN;
        this.p = Sd();
        this.g = this.c = this.r = null
    }

    v(Eo, to);
    l = Eo.prototype;
    l.Lf = function (b, c, d) {
        this.g = c;
        var e = b.viewState, f = this.c;
        if (null !== f && !f.la()) {
            var g = e.center, h = e.resolution, e = e.rotation, k = b.size, n = b.pixelRatio, p = c.opacity,
                q = c.brightness, r = c.contrast, s = c.hue;
            c = c.saturation;
            b = b.skippedFeatureUids;
            var u, z, y;
            u = 0;
            for (z = gm.length; u < z; ++u) y = f.d[gm[u]], m(y) && y.ac(d, g, h, e, k, n, p, q, r, s, c, b, void 0, !1)
        }
    };
    l.P = function () {
        var b = this.c;
        null !== b && (fo(b, this.b.c)(), this.c = null);
        Eo.T.P.call(this)
    };
    l.Ua = function (b, c, d, e) {
        if (null !== this.c && null !== this.g) {
            var f = c.viewState, g = this.a, h = this.g, k = {};
            return this.c.b(b, this.b.c, f.center, f.resolution, f.rotation, c.size, c.pixelRatio, h.opacity, h.brightness, h.contrast, h.hue, h.saturation, c.skippedFeatureUids, function (b) {
                var c = ma(b).toString();
                if (!(c in k)) return k[c] = !0, d.call(e, b, g)
            })
        }
    };
    l.Fd = function (b, c) {
        if (null === this.c || null === this.g) return !1;
        var d = c.viewState, e = this.g;
        return ko(this.c, b, this.b.c, d.resolution, d.rotation, c.pixelRatio, e.opacity, e.brightness, e.contrast, e.hue, e.saturation, c.skippedFeatureUids)
    };
    l.bc = function (b, c, d, e) {
        b = b.slice();
        kj(c.pixelToCoordinateMatrix, b, b);
        if (this.Fd(b, c)) return d.call(e, this.a)
    };
    l.Wj = function () {
        mj(this)
    };
    l.Ce = function (b, c, d) {
        function e(b) {
            var c;
            m(b.a) ? c = b.a.call(b, n) : m(f.r) && (c = (0, f.r)(b, n));
            if (null != c) {
                if (null != c) {
                    var d, e, g = !1;
                    d = 0;
                    for (e = c.length; d < e; ++d) g = Zm(s, b, c[d], Ym(n, p), this.Wj, this) || g;
                    b = g
                } else b = !1;
                this.n = this.n || b
            }
        }

        var f = this.a;
        c = f.a();
        pj(b.attributions, c.f);
        qj(b, c);
        if (!this.n && (!f.Ac && b.viewHints[0] || b.viewHints[1])) return !0;
        var g = b.extent, h = b.viewState, k = h.projection, n = h.resolution, p = b.pixelRatio, h = f.d, q = f.ea,
            r = f.get("renderOrder");
        m(r) || (r = Xm);
        g = Wd(g, q * n);
        if (!this.n && this.D == n && this.H ==
            h && this.r == r && Zd(this.p, g)) return !0;
        null === this.c || b.postRenderFunctions.push(fo(this.c, d));
        this.n = !1;
        var s = new eo(.5 * n / p, g, f.ea);
        c.Hb(g, n, k);
        if (null === r) c.Fb(g, n, e, this); else {
            var u = [];
            c.Fb(g, n, function (b) {
                u.push(b)
            }, this);
            cb(u, r);
            Pa(u, e, this)
        }
        go(s, d);
        this.D = n;
        this.H = h;
        this.r = r;
        this.p = g;
        this.c = s;
        return !0
    };

    function Fo(b, c) {
        zj.call(this, 0, c);
        this.a = Df("CANVAS");
        this.a.style.width = "100%";
        this.a.style.height = "100%";
        this.a.className = "ol-unselectable";
        Gf(b, this.a, 0);
        this.p = 0;
        this.r = Nf();
        this.j = !0;
        this.f = Uf(this.a, {antialias: !0, depth: !1, th: !0, preserveDrawingBuffer: !1, stencil: !0});
        this.c = new Rn(this.a, this.f);
        w(this.a, "webglcontextlost", this.Uj, !1, this);
        w(this.a, "webglcontextrestored", this.Vj, !1, this);
        this.d = new Ri;
        this.o = null;
        this.i = new Ej(ra(function (b) {
            var c = b[1];
            b = b[2];
            var f = c[0] - this.o[0], c = c[1] - this.o[1];
            return 65536 * Math.log(b) + Math.sqrt(f * f + c * c) / b
        }, this), function (b) {
            return b[0].qb()
        });
        this.D = ra(function () {
            if (!this.i.la()) {
                Ij(this.i);
                var b = Fj(this.i);
                Do(this, b[0], b[3], b[4])
            }
        }, this);
        this.n = 0;
        Go(this)
    }

    v(Fo, zj);

    function Do(b, c, d, e) {
        var f = b.f, g = c.qb();
        if (Si(b.d, g)) b = b.d.get(g), f.bindTexture(3553, b.Wa), 9729 != b.Af && (f.texParameteri(3553, 10240, 9729), b.Af = 9729), 9729 != b.Bf && (f.texParameteri(3553, 10240, 9729), b.Bf = 9729); else {
            var h = f.createTexture();
            f.bindTexture(3553, h);
            if (0 < e) {
                var k = b.r.canvas, n = b.r;
                b.p != d ? (k.width = d, k.height = d, b.p = d) : n.clearRect(0, 0, d, d);
                n.drawImage(c.Ta(), e, e, d, d, 0, 0, d, d);
                f.texImage2D(3553, 0, 6408, 6408, 5121, k)
            } else f.texImage2D(3553, 0, 6408, 6408, 5121, c.Ta());
            f.texParameteri(3553, 10240, 9729);
            f.texParameteri(3553,
                10241, 9729);
            f.texParameteri(3553, 10242, 33071);
            f.texParameteri(3553, 10243, 33071);
            b.d.set(g, {Wa: h, Af: 9729, Bf: 9729})
        }
    }

    l = Fo.prototype;
    l.le = function (b) {
        return b instanceof H ? new wo(this, b) : b instanceof I ? new Co(this, b) : b instanceof J ? new Eo(this, b) : null
    };

    function Ho(b, c, d) {
        var e = b.g;
        if (jd(e, c)) {
            var f = b.c, g = d.extent, h = d.size, k = d.viewState, n = d.pixelRatio, p = k.resolution, q = k.center,
                r = k.rotation, k = new lo(f, q, p, r, h, g, n), g = new eo(.5 * p / n, g);
            e.dispatchEvent(new Zk(c, e, k, g, d, null, f));
            go(g, f);
            if (!g.la()) {
                var s = Io;
                c = s.opacity;
                d = s.brightness;
                var e = s.contrast, u = s.hue, s = s.saturation, z = {}, y, A, D;
                y = 0;
                for (A = gm.length; y < A; ++y) D = g.d[gm[y]], m(D) && D.ac(f, q, p, r, h, n, c, d, e, u, s, z, void 0, !1)
            }
            fo(g, f)();
            f = Ra(rb(k.d), Number);
            cb(f);
            h = 0;
            for (n = f.length; h < n; ++h) for (p = k.d[f[h].toString()],
                                                    q = 0, r = p.length; q < r; ++q) p[q](k);
            b.b = g
        }
    }

    l.P = function () {
        var b = this.f;
        b.isContextLost() || this.d.forEach(function (c) {
            null === c || b.deleteTexture(c.Wa)
        });
        pc(this.c);
        Fo.T.P.call(this)
    };
    l.qh = function (b, c) {
        for (var d = this.f, e; 1024 < this.d.Tb() - this.n;) {
            e = this.d.a.gc;
            if (null === e) if (+this.d.a.Bd == c.index) break; else --this.n; else d.deleteTexture(e.Wa);
            this.d.pop()
        }
    };
    l.O = function () {
        return "webgl"
    };
    l.Uj = function (b) {
        b.preventDefault();
        this.d.clear();
        this.n = 0;
        kb(this.e, function (b) {
            b.Be()
        })
    };
    l.Vj = function () {
        Go(this);
        this.g.render()
    };

    function Go(b) {
        b = b.f;
        b.activeTexture(33984);
        b.blendFuncSeparate(770, 771, 1, 771);
        b.disable(2884);
        b.disable(2929);
        b.disable(3089);
        b.disable(2960)
    }

    l.Wd = function (b) {
        var c = this.c, d = this.f;
        if (d.isContextLost()) return !1;
        if (null === b) return this.j && (Mg(this.a, !1), this.j = !1), !1;
        this.o = b.focus;
        this.d.set((-b.index).toString(), null);
        ++this.n;
        var e = [], f = b.layerStatesArray, g = b.viewState.resolution, h, k, n, p;
        h = 0;
        for (k = f.length; h < k; ++h) p = f[h], Ni(p, g) && "ready" == p.yc && (n = Cj(this, p.layer), n.Ce(b, p, c) && e.push(p));
        f = b.size[0] * b.pixelRatio;
        g = b.size[1] * b.pixelRatio;
        if (this.a.width != f || this.a.height != g) this.a.width = f, this.a.height = g;
        d.bindFramebuffer(36160, null);
        d.clearColor(0, 0, 0, 0);
        d.clear(16384);
        d.enable(3042);
        d.viewport(0, 0, this.a.width, this.a.height);
        Ho(this, "precompose", b);
        h = 0;
        for (k = e.length; h < k; ++h) p = e[h], n = Cj(this, p.layer), n.Lf(b, p, c);
        this.j || (Mg(this.a, !0), this.j = !0);
        Aj(b);
        1024 < this.d.Tb() - this.n && b.postRenderFunctions.push(ra(this.qh, this));
        this.i.la() || (b.postRenderFunctions.push(this.D), b.animate = !0);
        Ho(this, "postcompose", b);
        Dj(this, b);
        b.postRenderFunctions.push(Bj)
    };
    l.ze = function (b, c, d, e, f, g) {
        var h;
        if (this.f.isContextLost()) return !1;
        var k = this.c, n = c.viewState;
        if (null !== this.b) {
            var p = {}, q = Io;
            if (h = this.b.b(b, k, n.center, n.resolution, n.rotation, c.size, c.pixelRatio, q.opacity, q.brightness, q.contrast, q.hue, q.saturation, {}, function (b) {
                var c = ma(b).toString();
                if (!(c in p)) return p[c] = !0, d.call(e, b, null)
            })) return h
        }
        k = c.layerStatesArray;
        for (q = k.length - 1; 0 <= q; --q) {
            h = k[q];
            var r = h.layer;
            if (Ni(h, n.resolution) && f.call(g, r) && (h = Cj(this, r).Ua(b, c, d, e))) return h
        }
    };
    l.Kf = function (b, c, d, e) {
        var f = !1;
        if (this.f.isContextLost()) return !1;
        var g = this.c, h = c.viewState;
        if (null !== this.b && (f = Io, f = ko(this.b, b, g, h.resolution, h.rotation, c.pixelRatio, f.opacity, f.brightness, f.contrast, f.hue, f.saturation, {}))) return !0;
        var g = c.layerStatesArray, k;
        for (k = g.length - 1; 0 <= k; --k) {
            var n = g[k], p = n.layer;
            if (Ni(n, h.resolution) && d.call(e, p) && (f = Cj(this, p).Fd(b, c))) return !0
        }
        return f
    };
    l.Jf = function (b, c, d, e, f) {
        if (this.f.isContextLost()) return !1;
        var g = this.c, h = c.viewState, k;
        if (null !== this.b) {
            var n = Io;
            k = this.g.ra(b);
            if (ko(this.b, k, g, h.resolution, h.rotation, c.pixelRatio, n.opacity, n.brightness, n.contrast, n.hue, n.saturation, {}) && (k = d.call(e, null))) return k
        }
        g = c.layerStatesArray;
        for (n = g.length - 1; 0 <= n; --n) {
            k = g[n];
            var p = k.layer;
            if (Ni(k, h.resolution) && f.call(e, p) && (k = Cj(this, p).bc(b, c, d, e))) return k
        }
    };
    var Io = {opacity: 1, brightness: 0, contrast: 1, hue: 0, saturation: 1};
    var Jo = ["canvas", "webgl", "dom"];

    function K(b) {
        qd.call(this);
        var c = Ko(b);
        this.xc = m(b.loadTilesWhileAnimating) ? b.loadTilesWhileAnimating : !1;
        this.yc = m(b.loadTilesWhileInteracting) ? b.loadTilesWhileInteracting : !1;
        this.Ac = m(b.pixelRatio) ? b.pixelRatio : Wf;
        this.zc = c.logos;
        this.r = new jh(this.Ql, void 0, this);
        oc(this, this.r);
        this.vc = Hd();
        this.$c = Hd();
        this.wc = 0;
        this.c = null;
        this.Ea = Sd();
        this.o = this.W = null;
        this.b = Af("DIV", "ol-viewport");
        this.b.style.position = "relative";
        this.b.style.overflow = "hidden";
        this.b.style.width = "100%";
        this.b.style.height =
            "100%";
        this.b.style.msTouchAction = "none";
        bg && (this.b.className = "ol-touch");
        this.ka = Af("DIV", "ol-overlaycontainer");
        this.b.appendChild(this.ka);
        this.H = Af("DIV", "ol-overlaycontainer-stopevent");
        w(this.H, ["click", "dblclick", "mousedown", "touchstart", "MSPointerDown", Gi, Gb ? "DOMMouseScroll" : "mousewheel"], rc);
        this.b.appendChild(this.H);
        b = new yi(this);
        w(b, qb(Ji), this.uf, !1, this);
        oc(this, b);
        this.fa = c.keyboardEventTarget;
        this.D = new Dh;
        w(this.D, "key", this.sf, !1, this);
        oc(this, this.D);
        b = new Lh(this.b);
        w(b, "mousewheel",
            this.sf, !1, this);
        oc(this, b);
        this.i = c.controls;
        this.g = c.interactions;
        this.j = c.overlays;
        this.p = new c.Sl(this.b, this);
        oc(this, this.p);
        this.ic = new yh;
        oc(this, this.ic);
        w(this.ic, "resize", this.q, !1, this);
        this.ca = null;
        this.N = [];
        this.va = [];
        this.kb = new Jj(ra(this.ci, this), ra(this.xj, this));
        this.da = {};
        w(this, ud("layergroup"), this.vi, !1, this);
        w(this, ud("view"), this.yj, !1, this);
        w(this, ud("size"), this.Li, !1, this);
        w(this, ud("target"), this.Mi, !1, this);
        this.C(c.pm);
        this.i.forEach(function (b) {
            b.setMap(this)
        }, this);
        w(this.i, "add", function (b) {
            b.element.setMap(this)
        }, !1, this);
        w(this.i, "remove", function (b) {
            b.element.setMap(null)
        }, !1, this);
        this.g.forEach(function (b) {
            b.setMap(this)
        }, this);
        w(this.g, "add", function (b) {
            b.element.setMap(this)
        }, !1, this);
        w(this.g, "remove", function (b) {
            b.element.setMap(null)
        }, !1, this);
        this.j.forEach(function (b) {
            b.setMap(this)
        }, this);
        w(this.j, "add", function (b) {
            b.element.setMap(this)
        }, !1, this);
        w(this.j, "remove", function (b) {
            b.element.setMap(null)
        }, !1, this)
    }

    v(K, qd);
    l = K.prototype;
    l.eh = function (b) {
        this.i.push(b)
    };
    l.fh = function (b) {
        this.g.push(b)
    };
    l.bf = function (b) {
        this.Ub().$b().push(b)
    };
    l.cf = function (b) {
        this.j.push(b)
    };
    l.La = function (b) {
        this.render();
        Array.prototype.push.apply(this.N, arguments)
    };
    l.P = function () {
        Hf(this.b);
        K.T.P.call(this)
    };
    l.oe = function (b, c, d, e, f) {
        if (null !== this.c) return b = this.ra(b), this.p.ze(b, this.c, c, m(d) ? d : null, m(e) ? e : bd, m(f) ? f : null)
    };
    l.wj = function (b, c, d, e, f) {
        if (null !== this.c) return this.p.Jf(b, this.c, c, m(d) ? d : null, m(e) ? e : bd, m(f) ? f : null)
    };
    l.Oi = function (b, c, d) {
        if (null === this.c) return !1;
        b = this.ra(b);
        return this.p.Kf(b, this.c, m(c) ? c : bd, m(d) ? d : null)
    };
    l.Bh = function (b) {
        return this.ra(this.gd(b))
    };
    l.gd = function (b) {
        if (m(b.changedTouches)) {
            var c = b.changedTouches[0];
            b = Jg(this.b);
            return [c.clientX - b.x, c.clientY - b.y]
        }
        c = this.b;
        b = Jg(b);
        c = Jg(c);
        c = new rf(b.x - c.x, b.y - c.y);
        return [c.x, c.y]
    };
    l.rc = function () {
        return this.get("target")
    };
    K.prototype.getTarget = K.prototype.rc;
    l = K.prototype;
    l.te = function () {
        var b = this.rc();
        return m(b) ? wf(b) : null
    };
    l.ra = function (b) {
        var c = this.c;
        if (null === c) return null;
        b = b.slice();
        return kj(c.pixelToCoordinateMatrix, b, b)
    };
    l.zh = function () {
        return this.i
    };
    l.Vh = function () {
        return this.j
    };
    l.Ih = function () {
        return this.g
    };
    l.Ub = function () {
        return this.get("layergroup")
    };
    K.prototype.getLayerGroup = K.prototype.Ub;
    K.prototype.ea = function () {
        return this.Ub().$b()
    };
    K.prototype.e = function (b) {
        var c = this.c;
        if (null === c) return null;
        b = b.slice(0, 2);
        return kj(c.coordinateToPixelMatrix, b, b)
    };
    K.prototype.f = function () {
        return this.get("size")
    };
    K.prototype.getSize = K.prototype.f;
    K.prototype.a = function () {
        return this.get("view")
    };
    K.prototype.getView = K.prototype.a;
    l = K.prototype;
    l.ei = function () {
        return this.b
    };
    l.ci = function (b, c, d, e) {
        var f = this.c;
        if (!(null !== f && c in f.wantedTiles && f.wantedTiles[c][kf(b.a)])) return Infinity;
        b = d[0] - f.focus[0];
        d = d[1] - f.focus[1];
        return 65536 * Math.log(e) + Math.sqrt(b * b + d * d) / e
    };
    l.sf = function (b, c) {
        var d = new wi(c || b.type, this, b);
        this.uf(d)
    };
    l.uf = function (b) {
        if (null !== this.c) {
            this.ca = b.coordinate;
            b.frameState = this.c;
            var c = this.g.a, d;
            if (!1 !== this.dispatchEvent(b)) for (d = c.length - 1; 0 <= d; d--) {
                var e = c[d];
                if (e.a() && !e.handleEvent(b)) break
            }
        }
    };
    l.Ji = function () {
        var b = this.c, c = this.kb;
        if (!c.la()) {
            var d = 16, e = d, f = 0;
            null !== b && (f = b.viewHints, f[0] && (d = this.xc ? 8 : 0, e = 2), f[1] && (d = this.yc ? 8 : 0, e = 2), f = pb(b.wantedTiles));
            d *= f;
            e *= f;
            if (c.c < d) {
                Ij(c);
                d = Math.min(d - c.c, e, c.Tb());
                for (e = 0; e < d; ++e) f = Fj(c)[0], w(f, "change", c.g, !1, c), f.load();
                c.c += d
            }
        }
        c = this.va;
        d = 0;
        for (e = c.length; d < e; ++d) c[d](this, b);
        c.length = 0
    };
    l.Li = function () {
        this.render()
    };
    l.Mi = function () {
        var b = this.rc(), b = m(b) ? wf(b) : null;
        Kh(this.D);
        null === b ? Hf(this.b) : (b.appendChild(this.b), Eh(this.D, null === this.fa ? b : this.fa));
        this.q()
    };
    l.xj = function () {
        this.render()
    };
    l.Ni = function () {
        this.render()
    };
    l.yj = function () {
        null !== this.W && (Wc(this.W), this.W = null);
        var b = this.a();
        null !== b && (this.W = w(b, "propertychange", this.Ni, !1, this));
        this.render()
    };
    l.wi = function () {
        this.render()
    };
    l.xi = function () {
        this.render()
    };
    l.vi = function () {
        if (null !== this.o) {
            for (var b = this.o.length, c = 0; c < b; ++c) Wc(this.o[c]);
            this.o = null
        }
        b = this.Ub();
        null != b && (this.o = [w(b, "propertychange", this.xi, !1, this), w(b, "change", this.wi, !1, this)]);
        this.render()
    };
    l.Rl = function () {
        var b = this.r;
        kh(b);
        b.kf()
    };
    l.render = function () {
        null != this.r.aa || this.r.start()
    };
    l.Ll = function (b) {
        if (m(this.i.remove(b))) return b
    };
    l.Ml = function (b) {
        var c;
        m(this.g.remove(b)) && (c = b);
        return c
    };
    l.Nl = function (b) {
        return this.Ub().$b().remove(b)
    };
    l.Ol = function (b) {
        if (m(this.j.remove(b))) return b
    };
    l.Ql = function (b) {
        var c, d, e, f = this.f(), g = this.a(), h = null;
        if (m(f) && 0 < f[0] && 0 < f[1] && null !== g && Ye(g)) {
            var h = g.q.slice(), k = this.Ub().Xa(), n = {};
            c = 0;
            for (d = k.length; c < d; ++c) n[ma(k[c].layer)] = k[c];
            e = Xe(g);
            h = {
                animate: !1,
                attributions: {},
                coordinateToPixelMatrix: this.vc,
                extent: null,
                focus: null === this.ca ? e.center : this.ca,
                index: this.wc++,
                layerStates: n,
                layerStatesArray: k,
                logos: Ab(this.zc),
                pixelRatio: this.Ac,
                pixelToCoordinateMatrix: this.$c,
                postRenderFunctions: [],
                size: f,
                skippedFeatureUids: this.da,
                tileQueue: this.kb,
                time: b,
                usedTiles: {},
                viewState: e,
                viewHints: h,
                wantedTiles: {}
            }
        }
        if (null !== h) {
            b = this.N;
            c = f = 0;
            for (d = b.length; c < d; ++c) g = b[c], g(this, h) && (b[f++] = g);
            b.length = f;
            h.extent = me(e.center, e.resolution, e.rotation, h.size)
        }
        this.c = h;
        this.p.Wd(h);
        null !== h && (h.animate && this.render(), Array.prototype.push.apply(this.va, h.postRenderFunctions), 0 !== this.N.length || h.viewHints[0] || h.viewHints[1] || ce(h.extent, this.Ea) || (this.dispatchEvent(new Tg("moveend", this, h)), Xd(h.extent, this.Ea)));
        this.dispatchEvent(new Tg("postrender",
            this, h));
        oh(this.Ji, this)
    };
    l.rg = function (b) {
        this.set("layergroup", b)
    };
    K.prototype.setLayerGroup = K.prototype.rg;
    K.prototype.S = function (b) {
        this.set("size", b)
    };
    K.prototype.setSize = K.prototype.S;
    K.prototype.ia = function (b) {
        this.set("target", b)
    };
    K.prototype.setTarget = K.prototype.ia;
    K.prototype.Fa = function (b) {
        this.set("view", b)
    };
    K.prototype.setView = K.prototype.Fa;
    K.prototype.Xa = function (b) {
        b = ma(b).toString();
        this.da[b] = !0;
        this.render()
    };
    K.prototype.q = function () {
        var b = this.rc(), b = m(b) ? wf(b) : null;
        if (null === b) this.S(void 0); else {
            var c = vf(b), d = Fb && b.currentStyle;
            d && Lf(tf(c)) && "auto" != d.width && "auto" != d.height && !d.boxSizing ? (c = Ng(b, d.width, "width", "pixelWidth"), b = Ng(b, d.height, "height", "pixelHeight"), b = new sf(c, b)) : (d = new sf(b.offsetWidth, b.offsetHeight), c = Pg(b, "padding"), b = Sg(b), b = new sf(d.width - b.left - c.left - c.right - b.right, d.height - b.top - c.top - c.bottom - b.bottom));
            this.S([b.width, b.height])
        }
    };
    K.prototype.hc = function (b) {
        b = ma(b).toString();
        delete this.da[b];
        this.render()
    };

    function Ko(b) {
        var c = null;
        m(b.keyboardEventTarget) && (c = ia(b.keyboardEventTarget) ? document.getElementById(b.keyboardEventTarget) : b.keyboardEventTarget);
        var d = {}, e = {};
        if (!m(b.logo) || "boolean" == typeof b.logo && b.logo) e["data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAAHGAAABxgEXwfpGAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAhNQTFRF////AP//AICAgP//AFVVQECA////K1VVSbbbYL/fJ05idsTYJFtbbcjbJllmZszWWMTOIFhoHlNiZszTa9DdUcHNHlNlV8XRIVdiasrUHlZjIVZjaMnVH1RlIFRkH1RkH1ZlasvYasvXVsPQH1VkacnVa8vWIVZjIFRjVMPQa8rXIVVkXsXRsNveIFVkIFZlIVVj3eDeh6GmbMvXH1ZkIFRka8rWbMvXIFVkIFVjIFVkbMvWH1VjbMvWIFVlbcvWIFVla8vVIFVkbMvWbMvVH1VkbMvWIFVlbcvWIFVkbcvVbMvWjNPbIFVkU8LPwMzNIFVkbczWIFVkbsvWbMvXIFVkRnB8bcvW2+TkW8XRIFVkIlZlJVloJlpoKlxrLl9tMmJwOWd0Omh1RXF8TneCT3iDUHiDU8LPVMLPVcLPVcPQVsPPVsPQV8PQWMTQWsTQW8TQXMXSXsXRX4SNX8bSYMfTYcfTYsfTY8jUZcfSZsnUaIqTacrVasrVa8jTa8rWbI2VbMvWbcvWdJObdcvUdszUd8vVeJaee87Yfc3WgJyjhqGnitDYjaarldPZnrK2oNbborW5o9bbo9fbpLa6q9ndrL3ArtndscDDutzfu8fJwN7gwt7gxc/QyuHhy+HizeHi0NfX0+Pj19zb1+Tj2uXk29/e3uLg3+Lh3+bl4uXj4ufl4+fl5Ofl5ufl5ujm5+jmySDnBAAAAFp0Uk5TAAECAgMEBAYHCA0NDg4UGRogIiMmKSssLzU7PkJJT1JTVFliY2hrdHZ3foSFhYeJjY2QkpugqbG1tre5w8zQ09XY3uXn6+zx8vT09vf4+Pj5+fr6/P39/f3+gz7SsAAAAVVJREFUOMtjYKA7EBDnwCPLrObS1BRiLoJLnte6CQy8FLHLCzs2QUG4FjZ5GbcmBDDjxJBXDWxCBrb8aM4zbkIDzpLYnAcE9VXlJSWlZRU13koIeW57mGx5XjoMZEUqwxWYQaQbSzLSkYGfKFSe0QMsX5WbjgY0YS4MBplemI4BdGBW+DQ11eZiymfqQuXZIjqwyadPNoSZ4L+0FVM6e+oGI6g8a9iKNT3o8kVzNkzRg5lgl7p4wyRUL9Yt2jAxVh6mQCogae6GmflI8p0r13VFWTHBQ0rWPW7ahgWVcPm+9cuLoyy4kCJDzCm6d8PSFoh0zvQNC5OjDJhQopPPJqph1doJBUD5tnkbZiUEqaCnB3bTqLTFG1bPn71kw4b+GFdpLElKIzRxxgYgWNYc5SCENVHKeUaltHdXx0dZ8uBI1hJ2UUDgq82CM2MwKeibqAvSO7MCABq0wXEPiqWEAAAAAElFTkSuQmCC"] = "http://openlayers.org/";
        else {
            var f = b.logo;
            ia(f) ? e[f] = "" : la(f) && (e[f.src] = f.href)
        }
        f = b.layers instanceof G ? b.layers : new G({layers: b.layers});
        d.layergroup = f;
        d.target = b.target;
        d.view = m(b.view) ? b.view : new B;
        var f = zj, g;
        m(b.renderer) ? ga(b.renderer) ? g = b.renderer : ia(b.renderer) && (g = [b.renderer]) : g = Jo;
        var h, k;
        h = 0;
        for (k = g.length; h < k; ++h) {
            var n = g[h];
            if ("canvas" == n) {
                if (Zf) {
                    f = xn;
                    break
                }
            } else if ("dom" == n) {
                f = Fn;
                break
            } else if ("webgl" == n && Vf) {
                f = Fo;
                break
            }
        }
        var p;
        m(b.controls) ? p = ga(b.controls) ? new lg(b.controls.slice()) : b.controls : p = ah();
        var q;
        m(b.interactions) ? q = ga(b.interactions) ? new lg(b.interactions.slice()) : b.interactions : q = Ll();
        b = m(b.overlays) ? ga(b.overlays) ? new lg(b.overlays.slice()) : b.overlays : new lg;
        return {controls: p, interactions: q, keyboardEventTarget: c, logos: e, overlays: b, Sl: f, pm: d}
    }

    Sl();

    function L(b) {
        qd.call(this);
        this.H = m(b.insertFirst) ? b.insertFirst : !0;
        this.N = m(b.stopEvent) ? b.stopEvent : !0;
        this.ba = Df("DIV");
        this.ba.style.position = "absolute";
        this.D = m(b.autoPan) ? b.autoPan : !1;
        this.g = m(b.autoPanAnimation) ? b.autoPanAnimation : {};
        this.r = m(b.autoPanMargin) ? b.autoPanMargin : 20;
        this.a = {ad: "", Cd: "", Xd: "", Yd: "", visible: !0};
        this.f = null;
        w(this, ud("element"), this.oi, !1, this);
        w(this, ud("map"), this.Di, !1, this);
        w(this, ud("offset"), this.Fi, !1, this);
        w(this, ud("position"), this.Hi, !1, this);
        w(this, ud("positioning"),
            this.Ii, !1, this);
        m(b.element) && this.Me(b.element);
        this.o(m(b.offset) ? b.offset : [0, 0]);
        this.p(m(b.positioning) ? b.positioning : "top-left");
        m(b.position) && this.e(b.position)
    }

    v(L, qd);
    L.prototype.b = function () {
        return this.get("element")
    };
    L.prototype.getElement = L.prototype.b;
    L.prototype.c = function () {
        return this.get("map")
    };
    L.prototype.getMap = L.prototype.c;
    L.prototype.i = function () {
        return this.get("offset")
    };
    L.prototype.getOffset = L.prototype.i;
    L.prototype.q = function () {
        return this.get("position")
    };
    L.prototype.getPosition = L.prototype.q;
    L.prototype.j = function () {
        return this.get("positioning")
    };
    L.prototype.getPositioning = L.prototype.j;
    l = L.prototype;
    l.oi = function () {
        Ff(this.ba);
        var b = this.b();
        null != b && Ef(this.ba, b)
    };
    l.Di = function () {
        null !== this.f && (Hf(this.ba), Wc(this.f), this.f = null);
        var b = this.c();
        null != b && (this.f = w(b, "postrender", this.render, !1, this), Lo(this), b = this.N ? b.H : b.ka, this.H ? Gf(b, this.ba, 0) : Ef(b, this.ba))
    };
    l.render = function () {
        Lo(this)
    };
    l.Fi = function () {
        Lo(this)
    };
    l.Hi = function () {
        Lo(this);
        if (m(this.get("position")) && this.D) {
            var b = this.c();
            if (m(b) && !fa(b.te())) {
                var c = Mo(b.te(), b.f()), d = this.b(), e = d.offsetWidth,
                    f = d.currentStyle || window.getComputedStyle(d),
                    e = e + (parseInt(f.marginLeft, 10) + parseInt(f.marginRight, 10)), f = d.offsetHeight,
                    g = d.currentStyle || window.getComputedStyle(d),
                    f = f + (parseInt(g.marginTop, 10) + parseInt(g.marginBottom, 10)), h = Mo(d, [e, f]), d = this.r;
                Zd(c, h) || (e = h[0] - c[0], f = c[2] - h[2], g = h[1] - c[1], h = c[3] - h[3], c = [0, 0], 0 > e ? c[0] = e - d : 0 > f && (c[0] = Math.abs(f) + d),
                    0 > g ? c[1] = g - d : 0 > h && (c[1] = Math.abs(h) + d), 0 === c[0] && 0 === c[1]) || (d = b.a().b(), e = b.e(d), c = [e[0] + c[0], e[1] + c[1]], null !== this.g && (this.g.source = d, b.La(df(this.g))), b.a().Ha(b.ra(c)))
            }
        }
    };
    l.Ii = function () {
        Lo(this)
    };
    l.Me = function (b) {
        this.set("element", b)
    };
    L.prototype.setElement = L.prototype.Me;
    L.prototype.setMap = function (b) {
        this.set("map", b)
    };
    L.prototype.setMap = L.prototype.setMap;
    L.prototype.o = function (b) {
        this.set("offset", b)
    };
    L.prototype.setOffset = L.prototype.o;
    L.prototype.e = function (b) {
        this.set("position", b)
    };
    L.prototype.setPosition = L.prototype.e;

    function Mo(b, c) {
        var d = vf(b);
        Fg(b, "position");
        var e = new rf(0, 0), f;
        f = d ? vf(d) : document;
        f = !Fb || Fb && 9 <= Rb || Lf(tf(f)) ? f.documentElement : f.body;
        b != f && (f = Ig(b), d = Mf(tf(d)), e.x = f.left + d.x, e.y = f.top + d.y);
        return [e.x, e.y, e.x + c[0], e.y + c[1]]
    }

    L.prototype.p = function (b) {
        this.set("positioning", b)
    };
    L.prototype.setPositioning = L.prototype.p;

    function Lo(b) {
        var c = b.c(), d = b.q();
        if (m(c) && null !== c.c && m(d)) {
            var d = c.e(d), e = c.f(), c = b.ba.style, f = b.i(), g = b.j(), h = f[0], f = f[1];
            if ("bottom-right" == g || "center-right" == g || "top-right" == g) "" !== b.a.Cd && (b.a.Cd = c.left = ""), h = Math.round(e[0] - d[0] - h) + "px", b.a.Xd != h && (b.a.Xd = c.right = h); else {
                "" !== b.a.Xd && (b.a.Xd = c.right = "");
                if ("bottom-center" == g || "center-center" == g || "top-center" == g) h -= Kg(b.ba).width / 2;
                h = Math.round(d[0] + h) + "px";
                b.a.Cd != h && (b.a.Cd = c.left = h)
            }
            if ("bottom-left" == g || "bottom-center" == g || "bottom-right" ==
                g) "" !== b.a.Yd && (b.a.Yd = c.top = ""), d = Math.round(e[1] - d[1] - f) + "px", b.a.ad != d && (b.a.ad = c.bottom = d); else {
                "" !== b.a.ad && (b.a.ad = c.bottom = "");
                if ("center-left" == g || "center-center" == g || "center-right" == g) f -= Kg(b.ba).height / 2;
                d = Math.round(d[1] + f) + "px";
                b.a.Yd != d && (b.a.Yd = c.top = d)
            }
            b.a.visible || (Mg(b.ba, !0), b.a.visible = !0)
        } else b.a.visible && (Mg(b.ba, !1), b.a.visible = !1)
    };

    function No(b) {
        b = m(b) ? b : {};
        this.e = m(b.collapsed) ? b.collapsed : !0;
        this.g = m(b.collapsible) ? b.collapsible : !0;
        this.g || (this.e = !1);
        var c = m(b.className) ? b.className : "ol-overviewmap", d = m(b.tipLabel) ? b.tipLabel : "Overview map",
            e = m(b.collapseLabel) ? b.collapseLabel : "\u00ab";
        this.o = ia(e) ? Af("SPAN", {}, e) : e;
        e = m(b.label) ? b.label : "\u00bb";
        this.p = ia(e) ? Af("SPAN", {}, e) : e;
        d = Af("BUTTON", {type: "button", title: d}, this.g && !this.e ? this.o : this.p);
        w(d, "click", this.Gj, !1, this);
        w(d, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        var e = Af("DIV", "ol-overviewmap-map"),
            f = this.c = new K({controls: new lg, interactions: new lg, target: e});
        m(b.layers) && b.layers.forEach(function (b) {
            f.bf(b)
        }, this);
        var g = Af("DIV", "ol-overviewmap-box");
        this.j = new L({position: [0, 0], positioning: "bottom-left", element: g});
        this.c.cf(this.j);
        c = Af("DIV", c + " ol-unselectable ol-control" + (this.e && this.g ? " ol-collapsed" : "") + (this.g ? "" : " ol-uncollapsible"), e, d);
        Ug.call(this, {element: c, render: m(b.render) ? b.render : Oo, target: b.target})
    }

    v(No, Ug);
    l = No.prototype;
    l.setMap = function (b) {
        var c = this.a;
        null === b && null !== c && Vc(c, ud("view"), this.Gf, !1, this);
        No.T.setMap.call(this, b);
        null !== b && (0 === this.c.ea().Ib() && this.c.K("layergroup", b), Po(this), w(b, ud("view"), this.Gf, !1, this), this.c.q(), Qo(this))
    };

    function Po(b) {
        var c = b.a.a();
        null === c || b.c.a().K("rotation", c)
    }

    function Oo() {
        var b = this.a, c = this.c;
        if (null !== b.c && null !== c.c) {
            var d = b.f(), b = b.a().g(d), e = c.f(), d = c.a().g(e), f = c.e(je(b)), c = c.e(he(b)),
                c = new sf(Math.abs(f[0] - c[0]), Math.abs(f[1] - c[1])), f = e[0], e = e[1];
            c.width < .1 * f || c.height < .1 * e || c.width > .75 * f || c.height > .75 * e ? Qo(this) : Zd(d, b) || (b = this.c, d = this.a.a(), b.a().Ha(d.b()))
        }
        Ro(this)
    }

    l.Gf = function () {
        Po(this)
    };

    function Qo(b) {
        var c = b.a;
        b = b.c;
        var d = c.f(), c = c.a().g(d), d = b.f();
        b = b.a();
        var e = Math.log(7.5) / Math.LN2;
        se(c, 1 / (.1 * Math.pow(2, e / 2)));
        b.ne(c, d)
    }

    function Ro(b) {
        var c = b.a, d = b.c;
        if (null !== c.c && null !== d.c) {
            var e = c.f(), f = c.a(), g = d.a();
            d.f();
            var c = f.c(), h = b.j, d = b.j.b(), f = f.g(e), e = g.a(), g = ge(f), f = ie(f), k;
            b = b.a.a().b();
            m(b) && (k = [g[0] - b[0], g[1] - b[1]], Ad(k, c), vd(k, b));
            h.e(k);
            null != d && (k = new sf(Math.abs((g[0] - f[0]) / e), Math.abs((f[1] - g[1]) / e)), c = Lf(tf(vf(d))), !Fb || Ob("10") || c && Ob("8") ? (d = d.style, Gb ? d.MozBoxSizing = "border-box" : Hb ? d.WebkitBoxSizing = "border-box" : d.boxSizing = "border-box", d.width = Math.max(k.width, 0) + "px", d.height = Math.max(k.height, 0) + "px") :
                (b = d.style, c ? (c = Pg(d, "padding"), d = Sg(d), b.pixelWidth = k.width - d.left - c.left - c.right - d.right, b.pixelHeight = k.height - d.top - c.top - c.bottom - d.bottom) : (b.pixelWidth = k.width, b.pixelHeight = k.height)))
        }
    }

    l.Gj = function (b) {
        b.preventDefault();
        So(this)
    };

    function So(b) {
        Ag(b.element, "ol-collapsed");
        b.e ? If(b.o, b.p) : If(b.p, b.o);
        b.e = !b.e;
        var c = b.c;
        b.e || null !== c.c || (c.q(), Qo(b), Uc(c, "postrender", function () {
            Ro(this)
        }, !1, b))
    }

    l.Fj = function () {
        return this.g
    };
    l.Ij = function (b) {
        this.g !== b && (this.g = b, Ag(this.element, "ol-uncollapsible"), !b && this.e && So(this))
    };
    l.Hj = function (b) {
        this.g && this.e !== b && So(this)
    };
    l.Ej = function () {
        return this.e
    };

    function To(b) {
        b = m(b) ? b : {};
        var c = m(b.className) ? b.className : "ol-scale-line";
        this.g = Af("DIV", c + "-inner");
        this.ba = Af("DIV", c + " ol-unselectable", this.g);
        this.r = null;
        this.j = m(b.minWidth) ? b.minWidth : 64;
        this.c = !1;
        this.H = void 0;
        this.D = "";
        this.e = null;
        Ug.call(this, {element: this.ba, render: m(b.render) ? b.render : Uo, target: b.target});
        w(this, ud("units"), this.N, !1, this);
        this.p(b.units || "metric")
    }

    v(To, Ug);
    var Vo = [1, 2, 5];
    To.prototype.o = function () {
        return this.get("units")
    };
    To.prototype.getUnits = To.prototype.o;

    function Uo(b) {
        b = b.frameState;
        null === b ? this.r = null : this.r = b.viewState;
        Wo(this)
    }

    To.prototype.N = function () {
        Wo(this)
    };
    To.prototype.p = function (b) {
        this.set("units", b)
    };
    To.prototype.setUnits = To.prototype.p;

    function Wo(b) {
        var c = b.r;
        if (null === c) b.c && (Mg(b.ba, !1), b.c = !1); else {
            var d = c.center, e = c.projection, c = e.re(c.resolution, d), f = e.d, g = b.o();
            "degrees" != f || "metric" != g && "imperial" != g && "us" != g && "nautical" != g ? "ft" != f && "m" != f || "degrees" != g ? b.e = null : (null === b.e && (b.e = ze(e, Ae("EPSG:4326"))), d = Math.cos(Yb(b.e(d)[1])), e = ve.radius, "ft" == f && (e /= .3048), c *= 180 / (Math.PI * d * e)) : (b.e = null, d = Math.cos(Yb(d[1])), c *= Math.PI * d * ve.radius / 180);
            d = b.j * c;
            f = "";
            "degrees" == g ? d < 1 / 60 ? (f = "\u2033", c *= 3600) : 1 > d ? (f = "\u2032", c *= 60) : f = "\u00b0" :
                "imperial" == g ? .9144 > d ? (f = "in", c /= .0254) : 1609.344 > d ? (f = "ft", c /= .3048) : (f = "mi", c /= 1609.344) : "nautical" == g ? (c /= 1852, f = "nm") : "metric" == g ? 1 > d ? (f = "mm", c *= 1E3) : 1E3 > d ? f = "m" : (f = "km", c /= 1E3) : "us" == g && (.9144 > d ? (f = "in", c *= 39.37) : 1609.344 > d ? (f = "ft", c /= .30480061) : (f = "mi", c /= 1609.3472));
            for (d = 3 * Math.floor(Math.log(b.j * c) / Math.log(10)); ;) {
                e = Vo[d % 3] * Math.pow(10, Math.floor(d / 3));
                g = Math.round(e / c);
                if (isNaN(g)) {
                    Mg(b.ba, !1);
                    b.c = !1;
                    return
                }
                if (g >= b.j) break;
                ++d
            }
            c = e + " " + f;
            b.D != c && (b.g.innerHTML = c, b.D = c);
            b.H != g && (b.g.style.width =
                g + "px", b.H = g);
            b.c || (Mg(b.ba, !0), b.c = !0)
        }
    };

    function Xo(b) {
        lc.call(this);
        this.d = b;
        this.a = {}
    }

    v(Xo, lc);
    var Yo = [];
    Xo.prototype.Ra = function (b, c, d, e) {
        ga(c) || (c && (Yo[0] = c.toString()), c = Yo);
        for (var f = 0; f < c.length; f++) {
            var g = w(b, c[f], d || this.handleEvent, e || !1, this.d || this);
            if (!g) break;
            this.a[g.key] = g
        }
        return this
    };
    Xo.prototype.Ne = function (b, c, d, e, f) {
        if (ga(c)) for (var g = 0; g < c.length; g++) this.Ne(b, c[g], d, e, f); else d = d || this.handleEvent, f = f || this.d || this, d = Oc(d), e = !!e, c = Bc(b) ? Ic(b.mb, String(c), d, e, f) : b ? (b = Qc(b)) ? Ic(b, c, d, e, f) : null : null, c && (Wc(c), delete this.a[c.key]);
        return this
    };

    function Zo(b) {
        kb(b.a, Wc);
        b.a = {}
    }

    Xo.prototype.P = function () {
        Xo.T.P.call(this);
        Zo(this)
    };
    Xo.prototype.handleEvent = function () {
        throw Error("EventHandler.handleEvent not implemented");
    };

    function $o(b, c, d) {
        hd.call(this);
        this.target = b;
        this.handle = c || b;
        this.a = d || new Cg(NaN, NaN, NaN, NaN);
        this.b = vf(b);
        this.d = new Xo(this);
        oc(this, this.d);
        w(this.handle, ["touchstart", "mousedown"], this.rf, !1, this)
    }

    v($o, hd);
    var ap = Fb || Gb && Ob("1.9.3");
    l = $o.prototype;
    l.clientX = 0;
    l.clientY = 0;
    l.screenX = 0;
    l.screenY = 0;
    l.sg = 0;
    l.tg = 0;
    l.oc = 0;
    l.pc = 0;
    l.Yb = !1;
    l.P = function () {
        $o.T.P.call(this);
        Vc(this.handle, ["touchstart", "mousedown"], this.rf, !1, this);
        Zo(this.d);
        ap && this.b.releaseCapture();
        this.handle = this.target = null
    };
    l.rf = function (b) {
        var c = "mousedown" == b.type;
        if (this.Yb || c && !zc(b)) this.dispatchEvent("earlycancel"); else if (bp(b), this.dispatchEvent(new cp("start", this, b.clientX, b.clientY))) {
            this.Yb = !0;
            b.preventDefault();
            var c = this.b, d = c.documentElement, e = !ap;
            this.d.Ra(c, ["touchmove", "mousemove"], this.Ei, e);
            this.d.Ra(c, ["touchend", "mouseup"], this.vd, e);
            ap ? (d.setCapture(!1), this.d.Ra(d, "losecapture", this.vd)) : this.d.Ra(c ? c.parentWindow || c.defaultView : window, "blur", this.vd);
            this.f && this.d.Ra(this.f, "scroll", this.il,
                e);
            this.clientX = this.sg = b.clientX;
            this.clientY = this.tg = b.clientY;
            this.screenX = b.screenX;
            this.screenY = b.screenY;
            this.oc = this.target.offsetLeft;
            this.pc = this.target.offsetTop;
            this.c = Mf(tf(this.b));
            ta()
        }
    };
    l.vd = function (b) {
        Zo(this.d);
        ap && this.b.releaseCapture();
        if (this.Yb) {
            bp(b);
            this.Yb = !1;
            var c = dp(this, this.oc), d = ep(this, this.pc);
            this.dispatchEvent(new cp("end", this, b.clientX, b.clientY, 0, c, d))
        } else this.dispatchEvent("earlycancel")
    };

    function bp(b) {
        var c = b.type;
        "touchstart" == c || "touchmove" == c ? xc(b, b.a.targetTouches[0], b.b) : "touchend" != c && "touchcancel" != c || xc(b, b.a.changedTouches[0], b.b)
    }

    l.Ei = function (b) {
        bp(b);
        var c = 1 * (b.clientX - this.clientX), d = b.clientY - this.clientY;
        this.clientX = b.clientX;
        this.clientY = b.clientY;
        this.screenX = b.screenX;
        this.screenY = b.screenY;
        if (!this.Yb) {
            var e = this.sg - this.clientX, f = this.tg - this.clientY;
            if (0 < e * e + f * f) if (this.dispatchEvent(new cp("start", this, b.clientX, b.clientY))) this.Yb = !0; else {
                this.oa || this.vd(b);
                return
            }
        }
        d = fp(this, c, d);
        c = d.x;
        d = d.y;
        this.Yb && this.dispatchEvent(new cp("beforedrag", this, b.clientX, b.clientY, 0, c, d)) && (gp(this, b, c, d), b.preventDefault())
    };

    function fp(b, c, d) {
        var e = Mf(tf(b.b));
        c += e.x - b.c.x;
        d += e.y - b.c.y;
        b.c = e;
        b.oc += c;
        b.pc += d;
        c = dp(b, b.oc);
        b = ep(b, b.pc);
        return new rf(c, b)
    }

    l.il = function (b) {
        var c = fp(this, 0, 0);
        b.clientX = this.clientX;
        b.clientY = this.clientY;
        gp(this, b, c.x, c.y)
    };

    function gp(b, c, d, e) {
        b.target.style.left = d + "px";
        b.target.style.top = e + "px";
        b.dispatchEvent(new cp("drag", b, c.clientX, c.clientY, 0, d, e))
    }

    function dp(b, c) {
        var d = b.a, e = isNaN(d.left) ? null : d.left, d = isNaN(d.width) ? 0 : d.width;
        return Math.min(null != e ? e + d : Infinity, Math.max(null != e ? e : -Infinity, c))
    }

    function ep(b, c) {
        var d = b.a, e = isNaN(d.top) ? null : d.top, d = isNaN(d.height) ? 0 : d.height;
        return Math.min(null != e ? e + d : Infinity, Math.max(null != e ? e : -Infinity, c))
    }

    function cp(b, c, d, e, f, g, h) {
        qc.call(this, b);
        this.clientX = d;
        this.clientY = e;
        this.left = m(g) ? g : c.oc;
        this.top = m(h) ? h : c.pc
    }

    v(cp, qc);

    function hp(b) {
        b = m(b) ? b : {};
        this.e = void 0;
        this.g = ip;
        this.j = null;
        this.o = !1;
        var c = m(b.className) ? b.className : "ol-zoomslider", d = Af("DIV", [c + "-thumb", "ol-unselectable"]),
            c = Af("DIV", [c, "ol-unselectable", "ol-control"], d);
        this.c = new $o(d);
        oc(this, this.c);
        w(this.c, "start", this.ni, !1, this);
        w(this.c, "drag", this.li, !1, this);
        w(this.c, "end", this.mi, !1, this);
        w(c, "click", this.ki, !1, this);
        w(d, "click", rc);
        Ug.call(this, {element: c, render: m(b.render) ? b.render : jp})
    }

    v(hp, Ug);
    var ip = 0;
    l = hp.prototype;
    l.setMap = function (b) {
        hp.T.setMap.call(this, b);
        null === b || b.render()
    };

    function jp(b) {
        if (null !== b.frameState) {
            if (!this.o) {
                var c = this.element, d = Kg(c), e = Jf(c), c = Pg(e, "margin"),
                    f = new sf(e.offsetWidth, e.offsetHeight), e = f.width + c.right + c.left,
                    c = f.height + c.top + c.bottom;
                this.j = [e, c];
                e = d.width - e;
                c = d.height - c;
                d.width > d.height ? (this.g = 1, d = new Cg(0, 0, e, 0)) : (this.g = ip, d = new Cg(0, 0, 0, c));
                this.c.a = d || new Cg(NaN, NaN, NaN, NaN);
                this.o = !0
            }
            b = b.frameState.viewState.resolution;
            b !== this.e && (this.e = b, b = 1 - We(this.a.a())(b), d = this.c, c = Jf(this.element), 1 == this.g ? Gg(c, d.a.left + d.a.width * b) : Gg(c,
                d.a.left, d.a.top + d.a.height * b))
        }
    }

    l.ki = function (b) {
        var c = this.a, d = c.a(), e = d.a();
        c.La(ff({resolution: e, duration: 200, easing: $e}));
        b = kp(this, b.offsetX - this.j[0] / 2, b.offsetY - this.j[1] / 2);
        b = lp(this, b);
        d.f(d.constrainResolution(b))
    };
    l.ni = function () {
        Ze(this.a.a(), 1)
    };
    l.li = function (b) {
        b = kp(this, b.left, b.top);
        this.e = lp(this, b);
        this.a.a().f(this.e)
    };
    l.mi = function () {
        var b = this.a, c = b.a();
        Ze(c, -1);
        b.La(ff({resolution: this.e, duration: 200, easing: $e}));
        b = c.constrainResolution(this.e);
        c.f(b)
    };

    function kp(b, c, d) {
        var e = b.c.a;
        return Vb(1 === b.g ? (c - e.left) / e.width : (d - e.top) / e.height, 0, 1)
    }

    function lp(b, c) {
        return Ve(b.a.a())(1 - c)
    };

    function mp(b) {
        b = m(b) ? b : {};
        this.c = m(b.extent) ? b.extent : null;
        var c = m(b.className) ? b.className : "ol-zoom-extent", d = Af("BUTTON", {
            type: "button",
            title: m(b.tipLabel) ? b.tipLabel : "Fit to extent"
        }, m(b.label) ? b.label : "E");
        w(d, "click", this.e, !1, this);
        w(d, ["mouseout", uc], function () {
            this.blur()
        }, !1);
        c = Af("DIV", c + " ol-unselectable ol-control", d);
        Ug.call(this, {element: c, target: b.target})
    }

    v(mp, Ug);
    mp.prototype.e = function (b) {
        b.preventDefault();
        var c = this.a;
        b = c.a();
        var d = null === this.c ? b.p.J() : this.c, c = c.f();
        b.ne(d, c)
    };

    function np(b) {
        qd.call(this);
        b = m(b) ? b : {};
        this.a = null;
        w(this, ud("tracking"), this.j, !1, this);
        this.b(m(b.tracking) ? b.tracking : !1)
    }

    v(np, qd);
    np.prototype.P = function () {
        this.b(!1);
        np.T.P.call(this)
    };
    np.prototype.q = function (b) {
        b = b.a;
        if (null != b.alpha) {
            var c = Yb(b.alpha);
            this.set("alpha", c);
            "boolean" == typeof b.absolute && b.absolute ? this.set("heading", c) : null != b.webkitCompassHeading && null != b.webkitCompassAccuracy && -1 != b.webkitCompassAccuracy && this.set("heading", Yb(b.webkitCompassHeading))
        }
        null != b.beta && this.set("beta", Yb(b.beta));
        null != b.gamma && this.set("gamma", Yb(b.gamma));
        this.l()
    };
    np.prototype.f = function () {
        return this.get("alpha")
    };
    np.prototype.getAlpha = np.prototype.f;
    np.prototype.e = function () {
        return this.get("beta")
    };
    np.prototype.getBeta = np.prototype.e;
    np.prototype.g = function () {
        return this.get("gamma")
    };
    np.prototype.getGamma = np.prototype.g;
    np.prototype.i = function () {
        return this.get("heading")
    };
    np.prototype.getHeading = np.prototype.i;
    np.prototype.c = function () {
        return this.get("tracking")
    };
    np.prototype.getTracking = np.prototype.c;
    np.prototype.j = function () {
        if ($f) {
            var b = this.c();
            b && null === this.a ? this.a = w(ba, "deviceorientation", this.q, !1, this) : b || null === this.a || (Wc(this.a), this.a = null)
        }
    };
    np.prototype.b = function (b) {
        this.set("tracking", b)
    };
    np.prototype.setTracking = np.prototype.b;

    function op(b) {
        qd.call(this);
        this.i = b;
        w(this.i, ["change", "input"], this.g, !1, this);
        w(this, ud("value"), this.j, !1, this);
        w(this, ud("checked"), this.e, !1, this)
    }

    v(op, qd);
    op.prototype.a = function () {
        return this.get("checked")
    };
    op.prototype.getChecked = op.prototype.a;
    op.prototype.b = function () {
        return this.get("value")
    };
    op.prototype.getValue = op.prototype.b;
    op.prototype.f = function (b) {
        this.set("value", b)
    };
    op.prototype.setValue = op.prototype.f;
    op.prototype.c = function (b) {
        this.set("checked", b)
    };
    op.prototype.setChecked = op.prototype.c;
    op.prototype.g = function () {
        var b = this.i;
        "checkbox" === b.type || "radio" === b.type ? this.c(b.checked) : this.f(b.value)
    };
    op.prototype.e = function () {
        this.i.checked = this.a()
    };
    op.prototype.j = function () {
        this.i.value = this.b()
    };

    function N(b) {
        qd.call(this);
        this.aa = void 0;
        this.b = "geometry";
        this.g = null;
        this.a = void 0;
        this.e = null;
        w(this, ud(this.b), this.wd, !1, this);
        m(b) && (b instanceof lk || null === b ? this.Sa(b) : this.C(b))
    }

    v(N, qd);
    N.prototype.clone = function () {
        var b = new N(this.I());
        b.f(this.b);
        var c = this.R();
        null != c && b.Sa(c.clone());
        c = this.g;
        null === c || b.i(c);
        return b
    };
    N.prototype.R = function () {
        return this.get(this.b)
    };
    N.prototype.getGeometry = N.prototype.R;
    l = N.prototype;
    l.Hh = function () {
        return this.aa
    };
    l.Gh = function () {
        return this.b
    };
    l.oj = function () {
        return this.g
    };
    l.pj = function () {
        return this.a
    };
    l.ui = function () {
        this.l()
    };
    l.wd = function () {
        null !== this.e && (Wc(this.e), this.e = null);
        var b = this.R();
        null != b && (this.e = w(b, "change", this.ui, !1, this), this.l())
    };
    l.Sa = function (b) {
        this.set(this.b, b)
    };
    N.prototype.setGeometry = N.prototype.Sa;
    N.prototype.i = function (b) {
        this.g = b;
        null === b ? b = void 0 : ka(b) || (b = ga(b) ? b : [b], b = $c(b));
        this.a = b;
        this.l()
    };
    N.prototype.c = function (b) {
        this.aa = b;
        this.l()
    };
    N.prototype.f = function (b) {
        Vc(this, ud(this.b), this.wd, !1, this);
        this.b = b;
        w(this, ud(this.b), this.wd, !1, this);
        this.wd()
    };

    function pp(b) {
        b = m(b) ? b : {};
        this.g = this.f = this.c = this.d = this.b = this.a = null;
        this.e = void 0;
        this.Ff(m(b.style) ? b.style : ul);
        m(b.features) ? ga(b.features) ? this.Sc(new lg(b.features.slice())) : this.Sc(b.features) : this.Sc(new lg);
        m(b.map) && this.setMap(b.map)
    }

    l = pp.prototype;
    l.Df = function (b) {
        this.a.push(b)
    };
    l.ij = function () {
        return this.a
    };
    l.jj = function () {
        return this.c
    };
    l.Ef = function () {
        qp(this)
    };
    l.si = function (b) {
        b = b.element;
        this.d[ma(b).toString()] = w(b, "change", this.Ef, !1, this);
        qp(this)
    };
    l.ti = function (b) {
        b = ma(b.element).toString();
        Wc(this.d[b]);
        delete this.d[b];
        qp(this)
    };
    l.mj = function () {
        qp(this)
    };
    l.nj = function (b) {
        if (null !== this.a) {
            var c = this.e;
            m(c) || (c = ul);
            var d = b.a;
            b = b.frameState;
            var e = b.viewState.resolution, f = Ym(e, b.pixelRatio), g, h, k, n;
            this.a.forEach(function (b) {
                n = b.a;
                k = m(n) ? n.call(b, e) : c(b, e);
                if (null != k) for (h = k.length, g = 0; g < h; ++g) Zm(d, b, k[g], f, this.mj, this)
            }, this)
        }
    };
    l.Dd = function (b) {
        this.a.remove(b)
    };

    function qp(b) {
        null === b.c || b.c.render()
    }

    l.Sc = function (b) {
        null !== this.b && (Pa(this.b, Wc), this.b = null);
        null !== this.d && (Pa(qb(this.d), Wc), this.d = null);
        this.a = b;
        null !== b && (this.b = [w(b, "add", this.si, !1, this), w(b, "remove", this.ti, !1, this)], this.d = {}, b.forEach(function (b) {
            this.d[ma(b).toString()] = w(b, "change", this.Ef, !1, this)
        }, this));
        qp(this)
    };
    l.setMap = function (b) {
        null !== this.f && (Wc(this.f), this.f = null);
        qp(this);
        this.c = b;
        null !== b && (this.f = w(b, "postcompose", this.nj, !1, this), b.render())
    };
    l.Ff = function (b) {
        this.g = b;
        this.e = tl(b);
        qp(this)
    };
    l.kj = function () {
        return this.g
    };
    l.lj = function () {
        return this.e
    };

    function rp() {
        this.defaultDataProjection = null
    }

    function sp(b, c, d) {
        var e;
        m(d) && (e = {
            dataProjection: m(d.dataProjection) ? d.dataProjection : b.Ja(c),
            featureProjection: d.featureProjection
        });
        return tp(b, e)
    }

    function tp(b, c) {
        var d;
        m(c) && (d = {
            featureProjection: c.featureProjection,
            dataProjection: null != c.dataProjection ? c.dataProjection : b.defaultDataProjection
        });
        return d
    }

    function up(b, c, d) {
        var e = m(d) ? Ae(d.featureProjection) : null;
        d = m(d) ? Ae(d.dataProjection) : null;
        return null === e || null === d || Re(e, d) ? b : b instanceof lk ? (c ? b.clone() : b).transform(c ? e : d, c ? d : e) : Ue(c ? b.slice() : b, c ? e : d, c ? d : e)
    };var vp = ba.JSON.parse, wp = ba.JSON.stringify;

    function xp() {
        this.defaultDataProjection = null
    }

    v(xp, rp);

    function yp(b) {
        return la(b) ? b : ia(b) ? (b = vp(b), m(b) ? b : null) : null
    }

    l = xp.prototype;
    l.O = function () {
        return "json"
    };
    l.Nb = function (b, c) {
        return zp(this, yp(b), sp(this, b, c))
    };
    l.ma = function (b, c) {
        return this.b(yp(b), sp(this, b, c))
    };
    l.Qc = function (b, c) {
        var d = yp(b), e = sp(this, b, c);
        return Ap(d, e)
    };
    l.Ja = function (b) {
        b = yp(b).crs;
        return null != b ? "name" == b.type ? Ae(b.properties.name) : "EPSG" == b.type ? Ae("EPSG:" + b.properties.code) : null : this.defaultDataProjection
    };
    l.$d = function (b, c) {
        return wp(this.a(b, c))
    };
    l.Qb = function (b, c) {
        return wp(this.c(b, c))
    };
    l.Wc = function (b, c) {
        return wp(this.f(b, c))
    };

    function Bp(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae(null != b.defaultDataProjection ? b.defaultDataProjection : "EPSG:4326");
        this.d = b.geometryName
    }

    v(Bp, xp);

    function Ap(b, c) {
        return null === b ? null : up((0, Cp[b.type])(b), !1, c)
    }

    var Cp = {
        Point: function (b) {
            return new Jk(b.coordinates)
        }, LineString: function (b) {
            return new Om(b.coordinates)
        }, Polygon: function (b) {
            return new F(b.coordinates)
        }, MultiPoint: function (b) {
            return new Tm(b.coordinates)
        }, MultiLineString: function (b) {
            return new Qm(b.coordinates)
        }, MultiPolygon: function (b) {
            return new Um(b.coordinates)
        }, GeometryCollection: function (b, c) {
            var d = Ra(b.geometries, function (b) {
                return Ap(b, c)
            });
            return new Hm(d)
        }
    }, Dp = {
        Point: function (b) {
            return {type: "Point", coordinates: b.Q()}
        }, LineString: function (b) {
            return {
                type: "LineString",
                coordinates: b.Q()
            }
        }, Polygon: function (b) {
            return {type: "Polygon", coordinates: b.Q()}
        }, MultiPoint: function (b) {
            return {type: "MultiPoint", coordinates: b.Q()}
        }, MultiLineString: function (b) {
            return {type: "MultiLineString", coordinates: b.Q()}
        }, MultiPolygon: function (b) {
            return {type: "MultiPolygon", coordinates: b.Q()}
        }, GeometryCollection: function (b, c) {
            return {
                type: "GeometryCollection", geometries: Ra(b.c, function (b) {
                    return (0, Dp[b.O()])(up(b, !0, c))
                })
            }
        }, Circle: function () {
            return {type: "GeometryCollection", geometries: []}
        }
    };

    function zp(b, c, d) {
        d = Ap(c.geometry, d);
        var e = new N;
        m(b.d) && e.f(b.d);
        e.Sa(d);
        m(c.id) && e.c(c.id);
        m(c.properties) && e.C(c.properties);
        return e
    }

    Bp.prototype.b = function (b, c) {
        if ("Feature" == b.type) return [zp(this, b, c)];
        if ("FeatureCollection" == b.type) {
            var d = [], e = b.features, f, g;
            f = 0;
            for (g = e.length; f < g; ++f) d.push(zp(this, e[f], c));
            return d
        }
        return []
    };
    Bp.prototype.a = function (b, c) {
        c = tp(this, c);
        var d = {type: "Feature"}, e = b.aa;
        null != e && (d.id = e);
        e = b.R();
        null != e && (d.geometry = (0, Dp[e.O()])(up(e, !0, c)));
        e = b.I();
        xb(e, b.b);
        vb(e) || (d.properties = e);
        return d
    };
    Bp.prototype.c = function (b, c) {
        c = tp(this, c);
        var d = [], e, f;
        e = 0;
        for (f = b.length; e < f; ++e) d.push(this.a(b[e], c));
        return {type: "FeatureCollection", features: d}
    };
    Bp.prototype.f = function (b, c) {
        return (0, Dp[b.O()])(up(b, !0, tp(this, c)))
    };

    function Ep(b) {
        if ("undefined" != typeof XMLSerializer) return (new XMLSerializer).serializeToString(b);
        if (b = b.xml) return b;
        throw Error("Your browser does not support serializing XML documents");
    };var Fp;
    a:if (document.implementation && document.implementation.createDocument) Fp = document.implementation.createDocument("", "", null); else {
        if ("undefined" != typeof ActiveXObject) {
            var Gp = new ActiveXObject("MSXML2.DOMDocument");
            if (Gp) {
                Gp.resolveExternals = !1;
                Gp.validateOnParse = !1;
                try {
                    Gp.setProperty("ProhibitDTD", !0), Gp.setProperty("MaxXMLSize", 2048), Gp.setProperty("MaxElementDepth", 256)
                } catch (Hp) {
                }
            }
            if (Gp) {
                Fp = Gp;
                break a
            }
        }
        throw Error("Your browser does not support creating new documents");
    }
    var Ip = Fp;

    function Jp(b, c) {
        return Ip.createElementNS(b, c)
    }

    function Kp(b, c) {
        null === b && (b = "");
        return Ip.createNode(1, c, b)
    }

    var Lp = document.implementation && document.implementation.createDocument ? Jp : Kp;

    function Mp(b, c) {
        return Np(b, c, []).join("")
    }

    function Np(b, c, d) {
        if (4 == b.nodeType || 3 == b.nodeType) c ? d.push(String(b.nodeValue).replace(/(\r\n|\r|\n)/g, "")) : d.push(b.nodeValue); else for (b = b.firstChild; null !== b; b = b.nextSibling) Np(b, c, d);
        return d
    }

    function Op(b) {
        return b.localName
    }

    function Pp(b) {
        var c = b.localName;
        return m(c) ? c : b.baseName
    }

    var Qp = Fb ? Pp : Op;

    function Rp(b) {
        return b instanceof Document
    }

    function Sp(b) {
        return la(b) && 9 == b.nodeType
    }

    var Tp = Fb ? Sp : Rp;

    function Up(b) {
        return b instanceof Node
    }

    function Vp(b) {
        return la(b) && m(b.nodeType)
    }

    var Wp = Fb ? Vp : Up;

    function Xp(b, c, d) {
        return b.getAttributeNS(c, d) || ""
    }

    function Yp(b, c, d) {
        var e = "";
        b = Zp(b, c, d);
        m(b) && (e = b.nodeValue);
        return e
    }

    var $p = document.implementation && document.implementation.createDocument ? Xp : Yp;

    function aq(b, c, d) {
        return b.getAttributeNodeNS(c, d)
    }

    function bq(b, c, d) {
        var e = null;
        b = b.attributes;
        for (var f, g, h = 0, k = b.length; h < k; ++h) if (f = b[h], f.namespaceURI == c && (g = f.prefix ? f.prefix + ":" + d : d, g == f.nodeName)) {
            e = f;
            break
        }
        return e
    }

    var Zp = document.implementation && document.implementation.createDocument ? aq : bq;

    function cq(b, c, d, e) {
        b.setAttributeNS(c, d, e)
    }

    function dq(b, c, d, e) {
        null === c ? b.setAttribute(d, e) : (c = b.ownerDocument.createNode(2, d, c), c.nodeValue = e, b.setAttributeNode(c))
    }

    var eq = document.implementation && document.implementation.createDocument ? cq : dq;

    function fq(b) {
        return (new DOMParser).parseFromString(b, "application/xml")
    }

    function gq(b, c) {
        return function (d, e) {
            var f = b.call(c, d, e);
            m(f) && $a(e[e.length - 1], f)
        }
    }

    function hq(b, c) {
        return function (d, e) {
            var f = b.call(m(c) ? c : this, d, e);
            m(f) && e[e.length - 1].push(f)
        }
    }

    function iq(b, c) {
        return function (d, e) {
            var f = b.call(m(c) ? c : this, d, e);
            m(f) && (e[e.length - 1] = f)
        }
    }

    function jq(b) {
        return function (c, d) {
            var e = b.call(m(void 0) ? void 0 : this, c, d);
            m(e) && zb(d[d.length - 1], m(void 0) ? void 0 : c.localName).push(e)
        }
    }

    function P(b, c) {
        return function (d, e) {
            var f = b.call(m(void 0) ? void 0 : this, d, e);
            m(f) && (e[e.length - 1][m(c) ? c : d.localName] = f)
        }
    }

    function R(b, c, d) {
        return kq(b, c, d)
    }

    function S(b, c) {
        return function (d, e, f) {
            b.call(m(c) ? c : this, d, e, f);
            f[f.length - 1].node.appendChild(d)
        }
    }

    function lq(b) {
        var c, d;
        return function (e, f, g) {
            if (!m(c)) {
                c = {};
                var h = {};
                h[e.localName] = b;
                c[e.namespaceURI] = h;
                d = mq(e.localName)
            }
            nq(c, d, f, g)
        }
    }

    function mq(b, c) {
        return function (d, e, f) {
            d = e[e.length - 1].node;
            e = b;
            m(e) || (e = f);
            f = c;
            m(c) || (f = d.namespaceURI);
            return Lp(f, e)
        }
    }

    var oq = mq();

    function pq(b, c) {
        for (var d = c.length, e = Array(d), f = 0; f < d; ++f) e[f] = b[c[f]];
        return e
    }

    function kq(b, c, d) {
        d = m(d) ? d : {};
        var e, f;
        e = 0;
        for (f = b.length; e < f; ++e) d[b[e]] = c;
        return d
    }

    function qq(b, c, d, e) {
        for (c = c.firstElementChild; null !== c; c = c.nextElementSibling) {
            var f = b[c.namespaceURI];
            m(f) && (f = f[c.localName], m(f) && f.call(e, c, d))
        }
    }

    function T(b, c, d, e, f) {
        e.push(b);
        qq(c, d, e, f);
        return e.pop()
    }

    function nq(b, c, d, e, f, g) {
        for (var h = (m(f) ? f : d).length, k, n, p = 0; p < h; ++p) k = d[p], m(k) && (n = c.call(g, k, e, m(f) ? f[p] : void 0), m(n) && b[n.namespaceURI][n.localName].call(g, n, k, e))
    }

    function rq(b, c, d, e, f, g, h) {
        f.push(b);
        nq(c, d, e, f, g, h);
        f.pop()
    };

    function sq() {
        this.defaultDataProjection = null
    }

    v(sq, rp);
    l = sq.prototype;
    l.O = function () {
        return "xml"
    };
    l.Nb = function (b, c) {
        if (Tp(b)) return tq(this, b, c);
        if (Wp(b)) return this.dg(b, c);
        if (ia(b)) {
            var d = fq(b);
            return tq(this, d, c)
        }
        return null
    };

    function tq(b, c, d) {
        b = uq(b, c, d);
        return 0 < b.length ? b[0] : null
    }

    l.ma = function (b, c) {
        if (Tp(b)) return uq(this, b, c);
        if (Wp(b)) return this.Ob(b, c);
        if (ia(b)) {
            var d = fq(b);
            return uq(this, d, c)
        }
        return []
    };

    function uq(b, c, d) {
        var e = [];
        for (c = c.firstChild; null !== c; c = c.nextSibling) 1 == c.nodeType && $a(e, b.Ob(c, d));
        return e
    }

    l.Qc = function (b, c) {
        if (Tp(b)) return this.j(b, c);
        if (Wp(b)) {
            var d = this.Sd(b, [sp(this, b, m(c) ? c : {})]);
            return m(d) ? d : null
        }
        return ia(b) ? (d = fq(b), this.j(d, c)) : null
    };
    l.Ja = function (b) {
        return Tp(b) ? this.Ke(b) : Wp(b) ? this.Vd(b) : ia(b) ? (b = fq(b), this.Ke(b)) : null
    };
    l.Ke = function () {
        return this.defaultDataProjection
    };
    l.Vd = function () {
        return this.defaultDataProjection
    };
    l.$d = function (b, c) {
        var d = this.o(b, c);
        return Ep(d)
    };
    l.Qb = function (b, c) {
        var d = this.a(b, c);
        return Ep(d)
    };
    l.Wc = function (b, c) {
        var d = this.i(b, c);
        return Ep(d)
    };

    function vq(b) {
        b = m(b) ? b : {};
        this.featureType = b.featureType;
        this.featureNS = b.featureNS;
        this.srsName = b.srsName;
        this.schemaLocation = "";
        this.d = {};
        this.d["http://www.opengis.net/gml"] = {
            featureMember: iq(vq.prototype.Qd),
            featureMembers: iq(vq.prototype.Qd)
        };
        this.defaultDataProjection = null
    }

    v(vq, sq);
    l = vq.prototype;
    l.Qd = function (b, c) {
        var d = Qp(b), e;
        if ("FeatureCollection" == d) e = T(null, this.d, b, c, this); else if ("featureMembers" == d || "featureMember" == d) {
            e = c[0];
            var f = e.featureType;
            if (!m(f) && null !== b.firstElementChild) {
                var g = b.firstElementChild, f = g.nodeName.split(":").pop();
                e.featureType = f;
                e.featureNS = g.namespaceURI
            }
            var g = {}, h = {};
            g[f] = "featureMembers" == d ? hq(this.Ge, this) : iq(this.Ge, this);
            h[e.featureNS] = g;
            e = T([], h, b, c)
        }
        m(e) || (e = []);
        return e
    };
    l.Sd = function (b, c) {
        var d = c[0];
        d.srsName = b.firstElementChild.getAttribute("srsName");
        var e = T(null, this.Ue, b, c, this);
        if (null != e) return up(e, !1, d)
    };
    l.Ge = function (b, c) {
        var d, e = b.getAttribute("fid") || $p(b, "http://www.opengis.net/gml", "id"), f = {}, g;
        for (d = b.firstElementChild; null !== d; d = d.nextElementSibling) {
            var h = Qp(d);
            if (0 === d.childNodes.length || 1 === d.childNodes.length && 3 === d.firstChild.nodeType) {
                var k = Mp(d, !1);
                /^[\s\xa0]*$/.test(k) && (k = void 0);
                f[h] = k
            } else "boundedBy" !== h && (g = h), f[h] = this.Sd(d, c)
        }
        d = new N(f);
        m(g) && d.f(g);
        e && d.c(e);
        return d
    };
    l.jg = function (b, c) {
        var d = this.Rd(b, c);
        if (null != d) {
            var e = new Jk(null);
            Kk(e, "XYZ", d);
            return e
        }
    };
    l.hg = function (b, c) {
        var d = T([], this.Ng, b, c, this);
        if (m(d)) return new Tm(d)
    };
    l.gg = function (b, c) {
        var d = T([], this.Mg, b, c, this);
        if (m(d)) {
            var e = new Qm(null);
            Sm(e, d);
            return e
        }
    };
    l.ig = function (b, c) {
        var d = T([], this.Og, b, c, this);
        if (m(d)) {
            var e = new Um(null);
            Wm(e, d);
            return e
        }
    };
    l.Zf = function (b, c) {
        qq(this.Rg, b, c, this)
    };
    l.zf = function (b, c) {
        qq(this.Kg, b, c, this)
    };
    l.$f = function (b, c) {
        qq(this.Sg, b, c, this)
    };
    l.Td = function (b, c) {
        var d = this.Rd(b, c);
        if (null != d) {
            var e = new Om(null);
            Pm(e, "XYZ", d);
            return e
        }
    };
    l.yl = function (b, c) {
        var d = T(null, this.Yc, b, c, this);
        if (null != d) return d
    };
    l.fg = function (b, c) {
        var d = this.Rd(b, c);
        if (m(d)) {
            var e = new Hk(null);
            Ik(e, "XYZ", d);
            return e
        }
    };
    l.Ud = function (b, c) {
        var d = T([null], this.de, b, c, this);
        if (m(d) && null !== d[0]) {
            var e = new F(null), f = d[0], g = [f.length], h, k;
            h = 1;
            for (k = d.length; h < k; ++h) $a(f, d[h]), g.push(f.length);
            Vk(e, "XYZ", f, g);
            return e
        }
    };
    l.Rd = function (b, c) {
        return T(null, this.Yc, b, c, this)
    };
    l.Ng = Object({
        "http://www.opengis.net/gml": {
            pointMember: hq(vq.prototype.Zf),
            pointMembers: hq(vq.prototype.Zf)
        }
    });
    l.Mg = Object({
        "http://www.opengis.net/gml": {
            lineStringMember: hq(vq.prototype.zf),
            lineStringMembers: hq(vq.prototype.zf)
        }
    });
    l.Og = Object({
        "http://www.opengis.net/gml": {
            polygonMember: hq(vq.prototype.$f),
            polygonMembers: hq(vq.prototype.$f)
        }
    });
    l.Rg = Object({"http://www.opengis.net/gml": {Point: hq(vq.prototype.Rd)}});
    l.Kg = Object({"http://www.opengis.net/gml": {LineString: hq(vq.prototype.Td)}});
    l.Sg = Object({"http://www.opengis.net/gml": {Polygon: hq(vq.prototype.Ud)}});
    l.Zc = Object({"http://www.opengis.net/gml": {LinearRing: iq(vq.prototype.yl)}});
    l.Ob = function (b, c) {
        var d = {featureType: this.featureType, featureNS: this.featureNS};
        m(c) && Cb(d, sp(this, b, c));
        return this.Qd(b, [d])
    };
    l.Vd = function (b) {
        return Ae(m(this.q) ? this.q : b.firstElementChild.getAttribute("srsName"))
    };

    function wq(b) {
        b = Mp(b, !1);
        return xq(b)
    }

    function xq(b) {
        if (b = /^\s*(true|1)|(false|0)\s*$/.exec(b)) return m(b[1]) || !1
    }

    function yq(b) {
        b = Mp(b, !1);
        if (b = /^\s*(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(Z|(?:([+\-])(\d{2})(?::(\d{2}))?))\s*$/.exec(b)) {
            var c = Date.UTC(parseInt(b[1], 10), parseInt(b[2], 10) - 1, parseInt(b[3], 10), parseInt(b[4], 10), parseInt(b[5], 10), parseInt(b[6], 10)) / 1E3;
            if ("Z" != b[7]) {
                var d = "-" == b[8] ? -1 : 1, c = c + 60 * d * parseInt(b[9], 10);
                m(b[10]) && (c += 3600 * d * parseInt(b[10], 10))
            }
            return c
        }
    }

    function zq(b) {
        b = Mp(b, !1);
        return Aq(b)
    }

    function Aq(b) {
        if (b = /^\s*([+\-]?\d*\.?\d+(?:e[+\-]?\d+)?)\s*$/i.exec(b)) return parseFloat(b[1])
    }

    function Bq(b) {
        b = Mp(b, !1);
        return Cq(b)
    }

    function Cq(b) {
        if (b = /^\s*(\d+)\s*$/.exec(b)) return parseInt(b[1], 10)
    }

    function U(b) {
        b = Mp(b, !1);
        return Aa(b)
    }

    function Dq(b, c) {
        Eq(b, c ? "1" : "0")
    }

    function Fq(b, c) {
        b.appendChild(Ip.createTextNode(c.toPrecision()))
    }

    function Gq(b, c) {
        b.appendChild(Ip.createTextNode(c.toString()))
    }

    function Eq(b, c) {
        b.appendChild(Ip.createTextNode(c))
    };

    function Hq(b) {
        b = m(b) ? b : {};
        vq.call(this, b);
        this.n = m(b.surface) ? b.surface : !1;
        this.f = m(b.curve) ? b.curve : !1;
        this.e = m(b.multiCurve) ? b.multiCurve : !0;
        this.g = m(b.multiSurface) ? b.multiSurface : !0;
        this.schemaLocation = m(b.schemaLocation) ? b.schemaLocation : "http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/profiles/gmlsfProfile/1.0.0/gmlsf.xsd"
    }

    v(Hq, vq);
    l = Hq.prototype;
    l.Bl = function (b, c) {
        var d = T([], this.Lg, b, c, this);
        if (m(d)) {
            var e = new Qm(null);
            Sm(e, d);
            return e
        }
    };
    l.Cl = function (b, c) {
        var d = T([], this.Pg, b, c, this);
        if (m(d)) {
            var e = new Um(null);
            Wm(e, d);
            return e
        }
    };
    l.hf = function (b, c) {
        qq(this.Hg, b, c, this)
    };
    l.ug = function (b, c) {
        qq(this.Vg, b, c, this)
    };
    l.Fl = function (b, c) {
        return T([null], this.Qg, b, c, this)
    };
    l.Hl = function (b, c) {
        return T([null], this.Ug, b, c, this)
    };
    l.Gl = function (b, c) {
        return T([null], this.de, b, c, this)
    };
    l.Al = function (b, c) {
        return T([null], this.Yc, b, c, this)
    };
    l.Ri = function (b, c) {
        var d = T(void 0, this.Zc, b, c, this);
        m(d) && c[c.length - 1].push(d)
    };
    l.rh = function (b, c) {
        var d = T(void 0, this.Zc, b, c, this);
        m(d) && (c[c.length - 1][0] = d)
    };
    l.kg = function (b, c) {
        var d = T([null], this.Wg, b, c, this);
        if (m(d) && null !== d[0]) {
            var e = new F(null), f = d[0], g = [f.length], h, k;
            h = 1;
            for (k = d.length; h < k; ++h) $a(f, d[h]), g.push(f.length);
            Vk(e, "XYZ", f, g);
            return e
        }
    };
    l.bg = function (b, c) {
        var d = T([null], this.Ig, b, c, this);
        if (m(d)) {
            var e = new Om(null);
            Pm(e, "XYZ", d);
            return e
        }
    };
    l.xl = function (b, c) {
        var d = T([null], this.Jg, b, c, this);
        return Vd(d[1][0], d[1][1], d[2][0], d[2][1])
    };
    l.zl = function (b, c) {
        for (var d = Mp(b, !1), e = /^\s*([+\-]?\d*\.?\d+(?:[eE][+\-]?\d+)?)\s*/, f = [], g; g = e.exec(d);) f.push(parseFloat(g[1])), d = d.substr(g[0].length);
        if ("" === d) {
            d = c[0].srsName;
            e = "enu";
            null === d || (e = ye(Ae(d)));
            if ("neu" === e) for (d = 0, e = f.length; d < e; d += 3) g = f[d], f[d] = f[d + 1], f[d + 1] = g;
            d = f.length;
            2 == d && f.push(0);
            return 0 === d ? void 0 : f
        }
    };
    l.Ie = function (b, c) {
        var d = Mp(b, !1).replace(/^\s*|\s*$/g, ""), e = c[0].srsName, f = b.parentNode.getAttribute("srsDimension"),
            g = "enu";
        null === e || (g = ye(Ae(e)));
        d = d.split(/\s+/);
        e = 2;
        fa(b.getAttribute("srsDimension")) ? fa(b.getAttribute("dimension")) ? null === f || (e = Cq(f)) : e = Cq(b.getAttribute("dimension")) : e = Cq(b.getAttribute("srsDimension"));
        for (var h, k, n = [], p = 0, q = d.length; p < q; p += e) f = parseFloat(d[p]), h = parseFloat(d[p + 1]), k = 3 === e ? parseFloat(d[p + 2]) : 0, "en" === g.substr(0, 2) ? n.push(f, h, k) : n.push(h, f, k);
        return n
    };
    l.Yc = Object({"http://www.opengis.net/gml": {pos: iq(Hq.prototype.zl), posList: iq(Hq.prototype.Ie)}});
    l.de = Object({"http://www.opengis.net/gml": {interior: Hq.prototype.Ri, exterior: Hq.prototype.rh}});
    l.Ue = Object({
        "http://www.opengis.net/gml": {
            Point: iq(vq.prototype.jg),
            MultiPoint: iq(vq.prototype.hg),
            LineString: iq(vq.prototype.Td),
            MultiLineString: iq(vq.prototype.gg),
            LinearRing: iq(vq.prototype.fg),
            Polygon: iq(vq.prototype.Ud),
            MultiPolygon: iq(vq.prototype.ig),
            Surface: iq(Hq.prototype.kg),
            MultiSurface: iq(Hq.prototype.Cl),
            Curve: iq(Hq.prototype.bg),
            MultiCurve: iq(Hq.prototype.Bl),
            Envelope: iq(Hq.prototype.xl)
        }
    });
    l.Lg = Object({
        "http://www.opengis.net/gml": {
            curveMember: hq(Hq.prototype.hf),
            curveMembers: hq(Hq.prototype.hf)
        }
    });
    l.Pg = Object({
        "http://www.opengis.net/gml": {
            surfaceMember: hq(Hq.prototype.ug),
            surfaceMembers: hq(Hq.prototype.ug)
        }
    });
    l.Hg = Object({"http://www.opengis.net/gml": {LineString: hq(vq.prototype.Td), Curve: hq(Hq.prototype.bg)}});
    l.Vg = Object({"http://www.opengis.net/gml": {Polygon: hq(vq.prototype.Ud), Surface: hq(Hq.prototype.kg)}});
    l.Wg = Object({"http://www.opengis.net/gml": {patches: iq(Hq.prototype.Fl)}});
    l.Ig = Object({"http://www.opengis.net/gml": {segments: iq(Hq.prototype.Hl)}});
    l.Jg = Object({"http://www.opengis.net/gml": {lowerCorner: hq(Hq.prototype.Ie), upperCorner: hq(Hq.prototype.Ie)}});
    l.Qg = Object({"http://www.opengis.net/gml": {PolygonPatch: iq(Hq.prototype.Gl)}});
    l.Ug = Object({"http://www.opengis.net/gml": {LineStringSegment: iq(Hq.prototype.Al)}});

    function Iq(b, c, d) {
        d = d[d.length - 1].srsName;
        c = c.Q();
        for (var e = c.length, f = Array(e), g, h = 0; h < e; ++h) {
            g = c[h];
            var k = h, n = "enu";
            null != d && (n = ye(Ae(d)));
            f[k] = "en" === n.substr(0, 2) ? g[0] + " " + g[1] : g[1] + " " + g[0]
        }
        Eq(b, f.join(" "))
    }

    l.Dg = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        null != e && b.setAttribute("srsName", e);
        e = Lp(b.namespaceURI, "pos");
        b.appendChild(e);
        d = d[d.length - 1].srsName;
        b = "enu";
        null != d && (b = ye(Ae(d)));
        c = c.Q();
        Eq(e, "en" === b.substr(0, 2) ? c[0] + " " + c[1] : c[1] + " " + c[0])
    };
    var Jq = {"http://www.opengis.net/gml": {lowerCorner: S(Eq), upperCorner: S(Eq)}};
    l = Hq.prototype;
    l.rm = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        m(e) && b.setAttribute("srsName", e);
        rq({node: b}, Jq, oq, [c[0] + " " + c[1], c[2] + " " + c[3]], d, ["lowerCorner", "upperCorner"], this)
    };
    l.Ag = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        null != e && b.setAttribute("srsName", e);
        e = Lp(b.namespaceURI, "posList");
        b.appendChild(e);
        Iq(e, c, d)
    };
    l.Tg = function (b, c) {
        var d = c[c.length - 1], e = d.node, f = d.exteriorWritten;
        m(f) || (d.exteriorWritten = !0);
        return Lp(e.namespaceURI, m(f) ? "interior" : "exterior")
    };
    l.ce = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        "PolygonPatch" !== b.nodeName && null != e && b.setAttribute("srsName", e);
        "Polygon" === b.nodeName || "PolygonPatch" === b.nodeName ? (c = c.kd(), rq({
            node: b,
            srsName: e
        }, Kq, this.Tg, c, d, void 0, this)) : "Surface" === b.nodeName && (e = Lp(b.namespaceURI, "patches"), b.appendChild(e), b = Lp(e.namespaceURI, "PolygonPatch"), e.appendChild(b), this.ce(b, c, d))
    };
    l.Zd = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        "LineStringSegment" !== b.nodeName && null != e && b.setAttribute("srsName", e);
        "LineString" === b.nodeName || "LineStringSegment" === b.nodeName ? (e = Lp(b.namespaceURI, "posList"), b.appendChild(e), Iq(e, c, d)) : "Curve" === b.nodeName && (e = Lp(b.namespaceURI, "segments"), b.appendChild(e), b = Lp(e.namespaceURI, "LineStringSegment"), e.appendChild(b), this.Zd(b, c, d))
    };
    l.Cg = function (b, c, d) {
        var e = d[d.length - 1], f = e.srsName, e = e.surface;
        null != f && b.setAttribute("srsName", f);
        c = c.pd();
        rq({node: b, srsName: f, surface: e}, Lq, this.c, c, d, void 0, this)
    };
    l.vm = function (b, c, d) {
        var e = d[d.length - 1].srsName;
        null != e && b.setAttribute("srsName", e);
        c = c.Ed();
        rq({node: b, srsName: e}, Mq, mq("pointMember"), c, d, void 0, this)
    };
    l.Bg = function (b, c, d) {
        var e = d[d.length - 1], f = e.srsName, e = e.curve;
        null != f && b.setAttribute("srsName", f);
        c = c.Lc();
        rq({node: b, srsName: f, curve: e}, Nq, this.c, c, d, void 0, this)
    };
    l.Eg = function (b, c, d) {
        var e = Lp(b.namespaceURI, "LinearRing");
        b.appendChild(e);
        this.Ag(e, c, d)
    };
    l.Fg = function (b, c, d) {
        var e = this.b(c, d);
        m(e) && (b.appendChild(e), this.ce(e, c, d))
    };
    l.ym = function (b, c, d) {
        var e = Lp(b.namespaceURI, "Point");
        b.appendChild(e);
        this.Dg(e, c, d)
    };
    l.zg = function (b, c, d) {
        var e = this.b(c, d);
        m(e) && (b.appendChild(e), this.Zd(e, c, d))
    };
    l.be = function (b, c, d) {
        var e = d[d.length - 1], f = Ab(e);
        f.node = b;
        var g;
        ga(c) ? m(e.dataProjection) ? g = Ue(c, e.featureProjection, e.dataProjection) : g = c : g = up(c, !0, e);
        rq(f, Oq, this.b, [g], d, void 0, this)
    };
    l.wg = function (b, c, d) {
        var e = c.aa;
        m(e) && b.setAttribute("fid", e);
        var e = d[d.length - 1], f = e.featureNS, g = c.b;
        m(e.dc) || (e.dc = {}, e.dc[f] = {});
        var h = c.I();
        c = [];
        var k = [], n;
        for (n in h) {
            var p = h[n];
            null !== p && (c.push(n), k.push(p), n == g ? n in e.dc[f] || (e.dc[f][n] = S(this.be, this)) : n in e.dc[f] || (e.dc[f][n] = S(Eq)))
        }
        n = Ab(e);
        n.node = b;
        rq(n, e.dc, mq(void 0, f), k, d, c)
    };
    var Lq = {"http://www.opengis.net/gml": {surfaceMember: S(Hq.prototype.Fg), polygonMember: S(Hq.prototype.Fg)}},
        Mq = {"http://www.opengis.net/gml": {pointMember: S(Hq.prototype.ym)}},
        Nq = {"http://www.opengis.net/gml": {lineStringMember: S(Hq.prototype.zg), curveMember: S(Hq.prototype.zg)}},
        Kq = {"http://www.opengis.net/gml": {exterior: S(Hq.prototype.Eg), interior: S(Hq.prototype.Eg)}}, Oq = {
            "http://www.opengis.net/gml": {
                Curve: S(Hq.prototype.Zd),
                MultiCurve: S(Hq.prototype.Bg),
                Point: S(Hq.prototype.Dg),
                MultiPoint: S(Hq.prototype.vm),
                LineString: S(Hq.prototype.Zd),
                MultiLineString: S(Hq.prototype.Bg),
                LinearRing: S(Hq.prototype.Ag),
                Polygon: S(Hq.prototype.ce),
                MultiPolygon: S(Hq.prototype.Cg),
                Surface: S(Hq.prototype.ce),
                MultiSurface: S(Hq.prototype.Cg),
                Envelope: S(Hq.prototype.rm)
            }
        }, Pq = {
            MultiLineString: "lineStringMember",
            MultiCurve: "curveMember",
            MultiPolygon: "polygonMember",
            MultiSurface: "surfaceMember"
        };
    Hq.prototype.c = function (b, c) {
        return Lp("http://www.opengis.net/gml", Pq[c[c.length - 1].node.nodeName])
    };
    Hq.prototype.b = function (b, c) {
        var d = c[c.length - 1], e = d.multiSurface, f = d.surface, g = d.curve, d = d.multiCurve, h;
        ga(b) ? h = "Envelope" : (h = b.O(), "MultiPolygon" === h && !0 === e ? h = "MultiSurface" : "Polygon" === h && !0 === f ? h = "Surface" : "LineString" === h && !0 === g ? h = "Curve" : "MultiLineString" === h && !0 === d && (h = "MultiCurve"));
        return Lp("http://www.opengis.net/gml", h)
    };
    Hq.prototype.i = function (b, c) {
        c = tp(this, c);
        var d = Lp("http://www.opengis.net/gml", "geom"), e = {
            node: d,
            srsName: this.srsName,
            curve: this.f,
            surface: this.n,
            multiSurface: this.g,
            multiCurve: this.e
        };
        m(c) && Cb(e, c);
        this.be(d, b, [e]);
        return d
    };
    Hq.prototype.a = function (b, c) {
        c = tp(this, c);
        var d = Lp("http://www.opengis.net/gml", "featureMembers");
        eq(d, "http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", this.schemaLocation);
        var e = {
            srsName: this.srsName,
            curve: this.f,
            surface: this.n,
            multiSurface: this.g,
            multiCurve: this.e,
            featureNS: this.featureNS,
            featureType: this.featureType
        };
        m(c) && Cb(e, c);
        var e = [e], f = e[e.length - 1], g = f.featureType, h = f.featureNS, k = {};
        k[h] = {};
        k[h][g] = S(this.wg, this);
        f = Ab(f);
        f.node = d;
        rq(f, k, mq(g, h), b, e);
        return d
    };

    function Qq(b) {
        b = m(b) ? b : {};
        vq.call(this, b);
        this.schemaLocation = m(b.schemaLocation) ? b.schemaLocation : "http://www.opengis.net/gml http://schemas.opengis.net/gml/2.1.2/feature.xsd"
    }

    v(Qq, vq);
    l = Qq.prototype;
    l.eg = function (b, c) {
        var d = Mp(b, !1).replace(/^\s*|\s*$/g, ""), e = c[0].srsName, f = b.parentNode.getAttribute("srsDimension"),
            g = "enu";
        null === e || (g = ye(Ae(e)));
        d = d.split(/[\s,]+/);
        e = 2;
        fa(b.getAttribute("srsDimension")) ? fa(b.getAttribute("dimension")) ? null === f || (e = Cq(f)) : e = Cq(b.getAttribute("dimension")) : e = Cq(b.getAttribute("srsDimension"));
        for (var h, k, n = [], p = 0, q = d.length; p < q; p += e) f = parseFloat(d[p]), h = parseFloat(d[p + 1]), k = 3 === e ? parseFloat(d[p + 2]) : 0, "en" === g.substr(0, 2) ? n.push(f, h, k) : n.push(h, f, k);
        return n
    };
    l.wl = function (b, c) {
        var d = T([null], this.Gg, b, c, this);
        return Vd(d[1][0], d[1][1], d[1][3], d[1][4])
    };
    l.Pi = function (b, c) {
        var d = T(void 0, this.Zc, b, c, this);
        m(d) && c[c.length - 1].push(d)
    };
    l.jl = function (b, c) {
        var d = T(void 0, this.Zc, b, c, this);
        m(d) && (c[c.length - 1][0] = d)
    };
    l.Yc = Object({"http://www.opengis.net/gml": {coordinates: iq(Qq.prototype.eg)}});
    l.de = Object({"http://www.opengis.net/gml": {innerBoundaryIs: Qq.prototype.Pi, outerBoundaryIs: Qq.prototype.jl}});
    l.Gg = Object({"http://www.opengis.net/gml": {coordinates: hq(Qq.prototype.eg)}});
    l.Ue = Object({
        "http://www.opengis.net/gml": {
            Point: iq(vq.prototype.jg),
            MultiPoint: iq(vq.prototype.hg),
            LineString: iq(vq.prototype.Td),
            MultiLineString: iq(vq.prototype.gg),
            LinearRing: iq(vq.prototype.fg),
            Polygon: iq(vq.prototype.Ud),
            MultiPolygon: iq(vq.prototype.ig),
            Box: iq(Qq.prototype.wl)
        }
    });

    function Rq(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae("EPSG:4326");
        this.d = b.readExtensions
    }

    v(Rq, sq);
    var Sq = [null, "http://www.topografix.com/GPX/1/0", "http://www.topografix.com/GPX/1/1"];

    function Tq(b, c, d) {
        b.push(parseFloat(c.getAttribute("lon")), parseFloat(c.getAttribute("lat")));
        "ele" in d ? (b.push(d.ele), xb(d, "ele")) : b.push(0);
        "time" in d ? (b.push(d.time), xb(d, "time")) : b.push(0);
        return b
    }

    function Uq(b, c) {
        var d = c[c.length - 1], e = b.getAttribute("href");
        null === e || (d.link = e);
        qq(Vq, b, c)
    }

    function Wq(b, c) {
        c[c.length - 1].extensionsNode_ = b
    }

    function Xq(b, c) {
        var d = c[0], e = T({flatCoordinates: []}, Yq, b, c);
        if (m(e)) {
            var f = e.flatCoordinates;
            xb(e, "flatCoordinates");
            var g = new Om(null);
            Pm(g, "XYZM", f);
            up(g, !1, d);
            d = new N(g);
            d.C(e);
            return d
        }
    }

    function Zq(b, c) {
        var d = c[0], e = T({flatCoordinates: [], ends: []}, $q, b, c);
        if (m(e)) {
            var f = e.flatCoordinates;
            xb(e, "flatCoordinates");
            var g = e.ends;
            xb(e, "ends");
            var h = new Qm(null);
            Rm(h, "XYZM", f, g);
            up(h, !1, d);
            d = new N(h);
            d.C(e);
            return d
        }
    }

    function ar(b, c) {
        var d = c[0], e = T({}, br, b, c);
        if (m(e)) {
            var f = Tq([], b, e), f = new Jk(f, "XYZM");
            up(f, !1, d);
            d = new N(f);
            d.C(e);
            return d
        }
    }

    var cr = {rte: Xq, trk: Zq, wpt: ar}, dr = R(Sq, {rte: hq(Xq), trk: hq(Zq), wpt: hq(ar)}),
        Vq = R(Sq, {text: P(U, "linkText"), type: P(U, "linkType")}), Yq = R(Sq, {
            name: P(U),
            cmt: P(U),
            desc: P(U),
            src: P(U),
            link: Uq,
            number: P(Bq),
            extensions: Wq,
            type: P(U),
            rtept: function (b, c) {
                var d = T({}, er, b, c);
                m(d) && Tq(c[c.length - 1].flatCoordinates, b, d)
            }
        }), er = R(Sq, {ele: P(zq), time: P(yq)}), $q = R(Sq, {
            name: P(U),
            cmt: P(U),
            desc: P(U),
            src: P(U),
            link: Uq,
            number: P(Bq),
            type: P(U),
            extensions: Wq,
            trkseg: function (b, c) {
                var d = c[c.length - 1];
                qq(fr, b, c);
                d.ends.push(d.flatCoordinates.length)
            }
        }),
        fr = R(Sq, {
            trkpt: function (b, c) {
                var d = T({}, gr, b, c);
                m(d) && Tq(c[c.length - 1].flatCoordinates, b, d)
            }
        }), gr = R(Sq, {ele: P(zq), time: P(yq)}), br = R(Sq, {
            ele: P(zq),
            time: P(yq),
            magvar: P(zq),
            geoidheight: P(zq),
            name: P(U),
            cmt: P(U),
            desc: P(U),
            src: P(U),
            link: Uq,
            sym: P(U),
            type: P(U),
            fix: P(U),
            sat: P(Bq),
            hdop: P(zq),
            vdop: P(zq),
            pdop: P(zq),
            ageofdgpsdata: P(zq),
            dgpsid: P(Bq),
            extensions: Wq
        });

    function hr(b, c) {
        null === c && (c = []);
        for (var d = 0, e = c.length; d < e; ++d) {
            var f = c[d];
            if (m(b.d)) {
                var g = f.get("extensionsNode_") || null;
                b.d(f, g)
            }
            f.set("extensionsNode_", void 0)
        }
    }

    Rq.prototype.dg = function (b, c) {
        if (!Va(Sq, b.namespaceURI)) return null;
        var d = cr[b.localName];
        if (!m(d)) return null;
        d = d(b, [sp(this, b, c)]);
        if (!m(d)) return null;
        hr(this, [d]);
        return d
    };
    Rq.prototype.Ob = function (b, c) {
        if (!Va(Sq, b.namespaceURI)) return [];
        if ("gpx" == b.localName) {
            var d = T([], dr, b, [sp(this, b, c)]);
            if (m(d)) return hr(this, d), d
        }
        return []
    };

    function ir(b, c, d) {
        b.setAttribute("href", c);
        c = d[d.length - 1].properties;
        rq({node: b}, jr, oq, [c.linkText, c.linkType], d, kr)
    }

    function lr(b, c, d) {
        var e = d[d.length - 1], f = e.node.namespaceURI, g = e.properties;
        eq(b, null, "lat", c[1]);
        eq(b, null, "lon", c[0]);
        switch (e.geometryLayout) {
            case "XYZM":
                0 !== c[3] && (g.time = c[3]);
            case "XYZ":
                0 !== c[2] && (g.ele = c[2]);
                break;
            case "XYM":
                0 !== c[2] && (g.time = c[2])
        }
        c = mr[f];
        e = pq(g, c);
        rq({node: b, properties: g}, nr, oq, e, d, c)
    }

    var kr = ["text", "type"], jr = kq(Sq, {text: S(Eq), type: S(Eq)}),
        or = kq(Sq, "name cmt desc src link number type rtept".split(" ")), pr = kq(Sq, {
            name: S(Eq),
            cmt: S(Eq),
            desc: S(Eq),
            src: S(Eq),
            link: S(ir),
            number: S(Gq),
            type: S(Eq),
            rtept: lq(S(lr))
        }), qr = kq(Sq, "name cmt desc src link number type trkseg".split(" ")), tr = kq(Sq, {
            name: S(Eq),
            cmt: S(Eq),
            desc: S(Eq),
            src: S(Eq),
            link: S(ir),
            number: S(Gq),
            type: S(Eq),
            trkseg: lq(S(function (b, c, d) {
                rq({node: b, geometryLayout: c.a, properties: {}}, rr, sr, c.Q(), d)
            }))
        }), sr = mq("trkpt"), rr = kq(Sq, {trkpt: S(lr)}),
        mr = kq(Sq, "ele time magvar geoidheight name cmt desc src link sym type fix sat hdop vdop pdop ageofdgpsdata dgpsid".split(" ")),
        nr = kq(Sq, {
            ele: S(Fq),
            time: S(function (b, c) {
                var d = new Date(1E3 * c),
                    d = d.getUTCFullYear() + "-" + La(d.getUTCMonth() + 1) + "-" + La(d.getUTCDate()) + "T" + La(d.getUTCHours()) + ":" + La(d.getUTCMinutes()) + ":" + La(d.getUTCSeconds()) + "Z";
                b.appendChild(Ip.createTextNode(d))
            }),
            magvar: S(Fq),
            geoidheight: S(Fq),
            name: S(Eq),
            cmt: S(Eq),
            desc: S(Eq),
            src: S(Eq),
            link: S(ir),
            sym: S(Eq),
            type: S(Eq),
            fix: S(Eq),
            sat: S(Gq),
            hdop: S(Fq),
            vdop: S(Fq),
            pdop: S(Fq),
            ageofdgpsdata: S(Fq),
            dgpsid: S(Gq)
        }), ur = {Point: "wpt", LineString: "rte", MultiLineString: "trk"};

    function vr(b, c) {
        var d = b.R();
        if (m(d)) return Lp(c[c.length - 1].node.namespaceURI, ur[d.O()])
    }

    var wr = kq(Sq, {
        rte: S(function (b, c, d) {
            var e = d[0], f = c.I();
            b = {node: b, properties: f};
            c = c.R();
            m(c) && (c = up(c, !0, e), b.geometryLayout = c.a, f.rtept = c.Q());
            e = or[d[d.length - 1].node.namespaceURI];
            f = pq(f, e);
            rq(b, pr, oq, f, d, e)
        }), trk: S(function (b, c, d) {
            var e = d[0], f = c.I();
            b = {node: b, properties: f};
            c = c.R();
            m(c) && (c = up(c, !0, e), f.trkseg = c.Lc());
            e = qr[d[d.length - 1].node.namespaceURI];
            f = pq(f, e);
            rq(b, tr, oq, f, d, e)
        }), wpt: S(function (b, c, d) {
            var e = d[0], f = d[d.length - 1];
            f.properties = c.I();
            c = c.R();
            m(c) && (c = up(c, !0, e), f.geometryLayout =
                c.a, lr(b, c.Q(), d))
        })
    });
    Rq.prototype.a = function (b, c) {
        c = tp(this, c);
        var d = Lp("http://www.topografix.com/GPX/1/1", "gpx");
        rq({node: d}, wr, vr, b, [c]);
        return d
    };

    function xr(b) {
        b = yr(b);
        return Ra(b, function (b) {
            return b.b.substring(b.d, b.a)
        })
    }

    function zr(b, c, d) {
        this.b = b;
        this.d = c;
        this.a = d
    }

    function yr(b) {
        for (var c = RegExp("\r\n|\r|\n", "g"), d = 0, e, f = []; e = c.exec(b);) d = new zr(b, d, e.index), f.push(d), d = c.lastIndex;
        d < b.length && (d = new zr(b, d, b.length), f.push(d));
        return f
    };

    function Ar() {
        this.defaultDataProjection = null
    }

    v(Ar, rp);
    l = Ar.prototype;
    l.O = function () {
        return "text"
    };
    l.Nb = function (b, c) {
        return this.Pc(ia(b) ? b : "", tp(this, c))
    };
    l.ma = function (b, c) {
        return this.He(ia(b) ? b : "", tp(this, c))
    };
    l.Qc = function (b, c) {
        return this.Rc(ia(b) ? b : "", tp(this, c))
    };
    l.Ja = function () {
        return this.defaultDataProjection
    };
    l.$d = function (b, c) {
        return this.ae(b, tp(this, c))
    };
    l.Qb = function (b, c) {
        return this.xg(b, tp(this, c))
    };
    l.Wc = function (b, c) {
        return this.Xc(b, tp(this, c))
    };

    function Br(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae("EPSG:4326");
        this.a = m(b.altitudeMode) ? b.altitudeMode : "none"
    }

    v(Br, Ar);
    var Cr = /^B(\d{2})(\d{2})(\d{2})(\d{2})(\d{5})([NS])(\d{3})(\d{5})([EW])([AV])(\d{5})(\d{5})/,
        Dr = /^H.([A-Z]{3}).*?:(.*)/, Er = /^HFDTE(\d{2})(\d{2})(\d{2})/;
    Br.prototype.Pc = function (b, c) {
        var d = this.a, e = xr(b), f = {}, g = [], h = 2E3, k = 0, n = 1, p, q;
        p = 0;
        for (q = e.length; p < q; ++p) {
            var r = e[p], s;
            if ("B" == r.charAt(0)) {
                if (s = Cr.exec(r)) {
                    var r = parseInt(s[1], 10), u = parseInt(s[2], 10), z = parseInt(s[3], 10),
                        y = parseInt(s[4], 10) + parseInt(s[5], 10) / 6E4;
                    "S" == s[6] && (y = -y);
                    var A = parseInt(s[7], 10) + parseInt(s[8], 10) / 6E4;
                    "W" == s[9] && (A = -A);
                    g.push(A, y);
                    "none" != d && g.push("gps" == d ? parseInt(s[11], 10) : "barometric" == d ? parseInt(s[12], 10) : 0);
                    g.push(Date.UTC(h, k, n, r, u, z) / 1E3)
                }
            } else if ("H" == r.charAt(0)) if (s =
                Er.exec(r)) n = parseInt(s[1], 10), k = parseInt(s[2], 10) - 1, h = 2E3 + parseInt(s[3], 10); else if (s = Dr.exec(r)) f[s[1]] = Aa(s[2]), Er.exec(r)
        }
        if (0 === g.length) return null;
        e = new Om(null);
        Pm(e, "none" == d ? "XYM" : "XYZM", g);
        d = new N(up(e, !1, c));
        d.C(f);
        return d
    };
    Br.prototype.He = function (b, c) {
        var d = this.Pc(b, c);
        return null === d ? [] : [d]
    };
    var Fr = /^(?:([^:/?#.]+):)?(?:\/\/(?:([^/?#]*)@)?([^/#?]*?)(?::([0-9]+))?(?=[/#?]|$))?([^?#]+)?(?:\?([^#]*))?(?:#(.*))?$/;

    function Gr(b) {
        if (Hr) {
            Hr = !1;
            var c = ba.location;
            if (c) {
                var d = c.href;
                if (d && (d = (d = Gr(d)[3] || null) ? decodeURI(d) : d) && d != c.hostname) throw Hr = !0, Error();
            }
        }
        return b.match(Fr)
    }

    var Hr = Hb;

    function Ir(b, c) {
        for (var d = b.split("&"), e = 0; e < d.length; e++) {
            var f = d[e].indexOf("="), g = null, h = null;
            0 <= f ? (g = d[e].substring(0, f), h = d[e].substring(f + 1)) : g = d[e];
            c(g, h ? decodeURIComponent(h.replace(/\+/g, " ")) : "")
        }
    }

    function Jr(b) {
        if (b[1]) {
            var c = b[0], d = c.indexOf("#");
            0 <= d && (b.push(c.substr(d)), b[0] = c = c.substr(0, d));
            d = c.indexOf("?");
            0 > d ? b[1] = "?" : d == c.length - 1 && (b[1] = void 0)
        }
        return b.join("")
    }

    function Kr(b, c, d) {
        if (ga(c)) for (var e = 0; e < c.length; e++) Kr(b, String(c[e]), d); else null != c && d.push("&", b, "" === c ? "" : "=", encodeURIComponent(String(c)))
    }

    function Lr(b, c) {
        for (var d in c) Kr(d, c[d], b);
        return b
    };

    function Mr(b, c) {
        var d;
        b instanceof Mr ? (this.Xb = m(c) ? c : b.Xb, Nr(this, b.Pb), this.fc = b.fc, this.sb = b.sb, Or(this, b.tc), this.rb = b.rb, Pr(this, b.a.clone()), this.Sb = b.Sb) : b && (d = Gr(String(b))) ? (this.Xb = !!c, Nr(this, d[1] || "", !0), this.fc = Qr(d[2] || ""), this.sb = Qr(d[3] || "", !0), Or(this, d[4]), this.rb = Qr(d[5] || "", !0), Pr(this, d[6] || "", !0), this.Sb = Qr(d[7] || "")) : (this.Xb = !!c, this.a = new Rr(null, 0, this.Xb))
    }

    l = Mr.prototype;
    l.Pb = "";
    l.fc = "";
    l.sb = "";
    l.tc = null;
    l.rb = "";
    l.Sb = "";
    l.Xb = !1;
    l.toString = function () {
        var b = [], c = this.Pb;
        c && b.push(Sr(c, Tr, !0), ":");
        if (c = this.sb) {
            b.push("//");
            var d = this.fc;
            d && b.push(Sr(d, Tr, !0), "@");
            b.push(encodeURIComponent(String(c)).replace(/%25([0-9a-fA-F]{2})/g, "%$1"));
            c = this.tc;
            null != c && b.push(":", String(c))
        }
        if (c = this.rb) this.sb && "/" != c.charAt(0) && b.push("/"), b.push(Sr(c, "/" == c.charAt(0) ? Ur : Vr, !0));
        (c = this.a.toString()) && b.push("?", c);
        (c = this.Sb) && b.push("#", Sr(c, Wr));
        return b.join("")
    };
    l.clone = function () {
        return new Mr(this)
    };

    function Nr(b, c, d) {
        b.Pb = d ? Qr(c, !0) : c;
        b.Pb && (b.Pb = b.Pb.replace(/:$/, ""))
    }

    function Or(b, c) {
        if (c) {
            c = Number(c);
            if (isNaN(c) || 0 > c) throw Error("Bad port number " + c);
            b.tc = c
        } else b.tc = null
    }

    function Pr(b, c, d) {
        c instanceof Rr ? (b.a = c, Xr(b.a, b.Xb)) : (d || (c = Sr(c, Yr)), b.a = new Rr(c, 0, b.Xb))
    }

    function Zr(b) {
        return b instanceof Mr ? b.clone() : new Mr(b, void 0)
    }

    function $r(b, c) {
        b instanceof Mr || (b = Zr(b));
        c instanceof Mr || (c = Zr(c));
        var d = b, e = c, f = d.clone(), g = !!e.Pb;
        g ? Nr(f, e.Pb) : g = !!e.fc;
        g ? f.fc = e.fc : g = !!e.sb;
        g ? f.sb = e.sb : g = null != e.tc;
        var h = e.rb;
        if (g) Or(f, e.tc); else if (g = !!e.rb) if ("/" != h.charAt(0) && (d.sb && !d.rb ? h = "/" + h : (d = f.rb.lastIndexOf("/"), -1 != d && (h = f.rb.substr(0, d + 1) + h))), d = h, ".." == d || "." == d) h = ""; else if (-1 != d.indexOf("./") || -1 != d.indexOf("/.")) {
            for (var h = 0 == d.lastIndexOf("/", 0), d = d.split("/"), k = [], n = 0; n < d.length;) {
                var p = d[n++];
                "." == p ? h && n == d.length && k.push("") :
                    ".." == p ? ((1 < k.length || 1 == k.length && "" != k[0]) && k.pop(), h && n == d.length && k.push("")) : (k.push(p), h = !0)
            }
            h = k.join("/")
        } else h = d;
        g ? f.rb = h : g = "" !== e.a.toString();
        g ? Pr(f, Qr(e.a.toString())) : g = !!e.Sb;
        g && (f.Sb = e.Sb);
        return f
    }

    function Qr(b, c) {
        return b ? c ? decodeURI(b) : decodeURIComponent(b) : ""
    }

    function Sr(b, c, d) {
        return ia(b) ? (b = encodeURI(b).replace(c, as), d && (b = b.replace(/%25([0-9a-fA-F]{2})/g, "%$1")), b) : null
    }

    function as(b) {
        b = b.charCodeAt(0);
        return "%" + (b >> 4 & 15).toString(16) + (b & 15).toString(16)
    }

    var Tr = /[#\/\?@]/g, Vr = /[\#\?:]/g, Ur = /[\#\?]/g, Yr = /[\#\?@]/g, Wr = /#/g;

    function Rr(b, c, d) {
        this.a = b || null;
        this.d = !!d
    }

    function bs(b) {
        b.ga || (b.ga = new uh, b.ya = 0, b.a && Ir(b.a, function (c, d) {
            b.add(decodeURIComponent(c.replace(/\+/g, " ")), d)
        }))
    }

    l = Rr.prototype;
    l.ga = null;
    l.ya = null;
    l.Tb = function () {
        bs(this);
        return this.ya
    };
    l.add = function (b, c) {
        bs(this);
        this.a = null;
        b = cs(this, b);
        var d = this.ga.get(b);
        d || this.ga.set(b, d = []);
        d.push(c);
        this.ya++;
        return this
    };
    l.remove = function (b) {
        bs(this);
        b = cs(this, b);
        return wh(this.ga.d, b) ? (this.a = null, this.ya -= this.ga.get(b).length, this.ga.remove(b)) : !1
    };
    l.clear = function () {
        this.ga = this.a = null;
        this.ya = 0
    };
    l.la = function () {
        bs(this);
        return 0 == this.ya
    };

    function ds(b, c) {
        bs(b);
        c = cs(b, c);
        return wh(b.ga.d, c)
    }

    l.G = function () {
        bs(this);
        for (var b = this.ga.ob(), c = this.ga.G(), d = [], e = 0; e < c.length; e++) for (var f = b[e], g = 0; g < f.length; g++) d.push(c[e]);
        return d
    };
    l.ob = function (b) {
        bs(this);
        var c = [];
        if (ia(b)) ds(this, b) && (c = Xa(c, this.ga.get(cs(this, b)))); else {
            b = this.ga.ob();
            for (var d = 0; d < b.length; d++) c = Xa(c, b[d])
        }
        return c
    };
    l.set = function (b, c) {
        bs(this);
        this.a = null;
        b = cs(this, b);
        ds(this, b) && (this.ya -= this.ga.get(b).length);
        this.ga.set(b, [c]);
        this.ya++;
        return this
    };
    l.get = function (b, c) {
        var d = b ? this.ob(b) : [];
        return 0 < d.length ? String(d[0]) : c
    };

    function es(b, c, d) {
        b.remove(c);
        0 < d.length && (b.a = null, b.ga.set(cs(b, c), Za(d)), b.ya += d.length)
    }

    l.toString = function () {
        if (this.a) return this.a;
        if (!this.ga) return "";
        for (var b = [], c = this.ga.G(), d = 0; d < c.length; d++) for (var e = c[d], f = encodeURIComponent(String(e)), e = this.ob(e), g = 0; g < e.length; g++) {
            var h = f;
            "" !== e[g] && (h += "=" + encodeURIComponent(String(e[g])));
            b.push(h)
        }
        return this.a = b.join("&")
    };
    l.clone = function () {
        var b = new Rr;
        b.a = this.a;
        this.ga && (b.ga = this.ga.clone(), b.ya = this.ya);
        return b
    };

    function cs(b, c) {
        var d = String(c);
        b.d && (d = d.toLowerCase());
        return d
    }

    function Xr(b, c) {
        c && !b.d && (bs(b), b.a = null, b.ga.forEach(function (b, c) {
            var f = c.toLowerCase();
            c != f && (this.remove(c), es(this, f, b))
        }, b));
        b.d = c
    };

    function fs(b) {
        b = m(b) ? b : {};
        this.c = b.font;
        this.f = b.rotation;
        this.d = b.scale;
        this.b = b.text;
        this.g = b.textAlign;
        this.n = b.textBaseline;
        this.a = m(b.fill) ? b.fill : null;
        this.e = m(b.stroke) ? b.stroke : null;
        this.i = m(b.offsetX) ? b.offsetX : 0;
        this.j = m(b.offsetY) ? b.offsetY : 0
    }

    l = fs.prototype;
    l.Dh = function () {
        return this.c
    };
    l.Th = function () {
        return this.i
    };
    l.Uh = function () {
        return this.j
    };
    l.Uk = function () {
        return this.a
    };
    l.Vk = function () {
        return this.f
    };
    l.Wk = function () {
        return this.d
    };
    l.Xk = function () {
        return this.e
    };
    l.Yk = function () {
        return this.b
    };
    l.ai = function () {
        return this.g
    };
    l.bi = function () {
        return this.n
    };
    l.Wl = function (b) {
        this.c = b
    };
    l.Vl = function (b) {
        this.a = b
    };
    l.Zk = function (b) {
        this.f = b
    };
    l.$k = function (b) {
        this.d = b
    };
    l.bm = function (b) {
        this.e = b
    };
    l.cm = function (b) {
        this.b = b
    };
    l.dm = function (b) {
        this.g = b
    };
    l.em = function (b) {
        this.n = b
    };

    function gs(b) {
        function c(b) {
            return ga(b) ? b : ia(b) ? (!(b in e) && "#" + b in e && (b = "#" + b), c(e[b])) : d
        }

        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae("EPSG:4326");
        var d = m(b.defaultStyle) ? b.defaultStyle : hs, e = {};
        this.b = m(b.extractStyles) ? b.extractStyles : !0;
        this.d = e;
        this.c = function () {
            var b = this.get("Style");
            if (m(b)) return b;
            b = this.get("styleUrl");
            return m(b) ? c(b) : d
        }
    }

    v(gs, sq);
    var is = ["http://www.google.com/kml/ext/2.2"],
        js = [null, "http://earth.google.com/kml/2.0", "http://earth.google.com/kml/2.1", "http://earth.google.com/kml/2.2", "http://www.opengis.net/kml/2.2"],
        ks = [255, 255, 255, 1], ls = new pl({color: ks}), ms = [20, 2], ns = [64, 64], os = new wj({
            anchor: ms,
            anchorOrigin: "bottom-left",
            anchorXUnits: "pixels",
            anchorYUnits: "pixels",
            crossOrigin: "anonymous",
            rotation: 0,
            scale: .5,
            size: ns,
            src: "https://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png"
        }), ps = new kl({color: ks, width: 1}), qs = new fs({
            font: "normal 16px Helvetica",
            fill: ls, stroke: ps, scale: 1
        }), hs = [new rl({fill: ls, image: os, text: qs, stroke: ps, zIndex: 0})],
        rs = {fraction: "fraction", pixels: "pixels"};

    function ss(b) {
        b = Mp(b, !1);
        if (b = /^\s*#?\s*([0-9A-Fa-f]{8})\s*$/.exec(b)) return b = b[1], [parseInt(b.substr(6, 2), 16), parseInt(b.substr(4, 2), 16), parseInt(b.substr(2, 2), 16), parseInt(b.substr(0, 2), 16) / 255]
    }

    function ts(b) {
        b = Mp(b, !1);
        for (var c = [], d = /^\s*([+\-]?\d*\.?\d+(?:e[+\-]?\d+)?)\s*,\s*([+\-]?\d*\.?\d+(?:e[+\-]?\d+)?)(?:\s*,\s*([+\-]?\d*\.?\d+(?:e[+\-]?\d+)?))?\s*/i, e; e = d.exec(b);) c.push(parseFloat(e[1]), parseFloat(e[2]), e[3] ? parseFloat(e[3]) : 0), b = b.substr(e[0].length);
        return "" !== b ? void 0 : c
    }

    function us(b) {
        var c = Mp(b, !1);
        return null != b.baseURI ? $r(b.baseURI, Aa(c)).toString() : Aa(c)
    }

    function vs(b) {
        b = zq(b);
        if (m(b)) return Math.sqrt(b)
    }

    function ws(b, c) {
        return T(null, xs, b, c)
    }

    function ys(b, c) {
        var d = T({k: [], vg: []}, zs, b, c);
        if (m(d)) {
            var e = d.k, d = d.vg, f, g;
            f = 0;
            for (g = Math.min(e.length, d.length); f < g; ++f) e[4 * f + 3] = d[f];
            d = new Om(null);
            Pm(d, "XYZM", e);
            return d
        }
    }

    function As(b, c) {
        var d = T(null, Bs, b, c);
        if (m(d)) {
            var e = new Om(null);
            Pm(e, "XYZ", d);
            return e
        }
    }

    function Cs(b, c) {
        var d = T(null, Bs, b, c);
        if (m(d)) {
            var e = new F(null);
            Vk(e, "XYZ", d, [d.length]);
            return e
        }
    }

    function Ds(b, c) {
        var d = T([], Es, b, c);
        if (!m(d)) return null;
        if (0 === d.length) return new Hm(d);
        var e = !0, f = d[0].O(), g, h, k;
        h = 1;
        for (k = d.length; h < k; ++h) if (g = d[h], g.O() != f) {
            e = !1;
            break
        }
        if (e) {
            if ("Point" == f) {
                g = d[0];
                e = g.a;
                f = g.k;
                h = 1;
                for (k = d.length; h < k; ++h) g = d[h], $a(f, g.k);
                d = new Tm(null);
                pk(d, e, f);
                d.l();
                return d
            }
            return "LineString" == f ? (g = new Qm(null), Sm(g, d), g) : "Polygon" == f ? (g = new Um(null), Wm(g, d), g) : "GeometryCollection" == f ? new Hm(d) : null
        }
        return new Hm(d)
    }

    function Fs(b, c) {
        var d = T(null, Bs, b, c);
        if (null != d) {
            var e = new Jk(null);
            Kk(e, "XYZ", d);
            return e
        }
    }

    function Gs(b, c) {
        var d = T([null], Hs, b, c);
        if (null != d && null !== d[0]) {
            var e = new F(null), f = d[0], g = [f.length], h, k;
            h = 1;
            for (k = d.length; h < k; ++h) $a(f, d[h]), g.push(f.length);
            Vk(e, "XYZ", f, g);
            return e
        }
    }

    function Is(b, c) {
        var d = T({}, Js, b, c);
        if (!m(d)) return null;
        var e = yb(d, "fillStyle", ls), f = d.fill;
        m(f) && !f && (e = null);
        var f = yb(d, "imageStyle", os), g = yb(d, "textStyle", qs), h = yb(d, "strokeStyle", ps), d = d.outline;
        m(d) && !d && (h = null);
        return [new rl({fill: e, image: f, stroke: h, text: g, zIndex: void 0})]
    }

    function Ks(b, c) {
        qq(Ls, b, c)
    }

    var Ms = R(js, {value: iq(U)}), Ls = R(js, {
            Data: function (b, c) {
                var d = b.getAttribute("name");
                if (null !== d) {
                    var e = T(void 0, Ms, b, c);
                    m(e) && (c[c.length - 1][d] = e)
                }
            }, SchemaData: function (b, c) {
                qq(Ns, b, c)
            }
        }), xs = R(js, {coordinates: iq(ts)}), Hs = R(js, {
            innerBoundaryIs: function (b, c) {
                var d = T(void 0, Os, b, c);
                m(d) && c[c.length - 1].push(d)
            }, outerBoundaryIs: function (b, c) {
                var d = T(void 0, Ps, b, c);
                m(d) && (c[c.length - 1][0] = d)
            }
        }), zs = R(js, {
            when: function (b, c) {
                var d = c[c.length - 1].vg, e = Mp(b, !1);
                if (e = /^\s*(\d{4})($|-(\d{2})($|-(\d{2})($|T(\d{2}):(\d{2}):(\d{2})(Z|(?:([+\-])(\d{2})(?::(\d{2}))?)))))\s*$/.exec(e)) {
                    var f =
                        Date.UTC(parseInt(e[1], 10), m(e[3]) ? parseInt(e[3], 10) - 1 : 0, m(e[5]) ? parseInt(e[5], 10) : 1, m(e[7]) ? parseInt(e[7], 10) : 0, m(e[8]) ? parseInt(e[8], 10) : 0, m(e[9]) ? parseInt(e[9], 10) : 0);
                    if (m(e[10]) && "Z" != e[10]) {
                        var g = "-" == e[11] ? -1 : 1, f = f + 60 * g * parseInt(e[12], 10);
                        m(e[13]) && (f += 3600 * g * parseInt(e[13], 10))
                    }
                    d.push(f)
                } else d.push(0)
            }
        }, R(is, {
            coord: function (b, c) {
                var d = c[c.length - 1].k, e = Mp(b, !1);
                (e = /^\s*([+\-]?\d+(?:\.\d*)?(?:e[+\-]?\d*)?)\s+([+\-]?\d+(?:\.\d*)?(?:e[+\-]?\d*)?)\s+([+\-]?\d+(?:\.\d*)?(?:e[+\-]?\d*)?)\s*$/i.exec(e)) ?
                    d.push(parseFloat(e[1]), parseFloat(e[2]), parseFloat(e[3]), 0) : d.push(0, 0, 0, 0)
            }
        })), Bs = R(js, {coordinates: iq(ts)}), Qs = R(js, {href: P(us)}, R(is, {x: P(zq), y: P(zq), w: P(zq), h: P(zq)})),
        Rs = R(js, {
            Icon: P(function (b, c) {
                var d = T({}, Qs, b, c);
                return m(d) ? d : null
            }), heading: P(zq), hotSpot: P(function (b) {
                var c = b.getAttribute("xunits"), d = b.getAttribute("yunits");
                return {x: parseFloat(b.getAttribute("x")), Re: rs[c], y: parseFloat(b.getAttribute("y")), Se: rs[d]}
            }), scale: P(vs)
        }), Os = R(js, {LinearRing: iq(ws)}), Ss = R(js, {color: P(ss), scale: P(vs)}),
        Ts = R(js, {color: P(ss), width: P(zq)}),
        Es = R(js, {LineString: hq(As), LinearRing: hq(Cs), MultiGeometry: hq(Ds), Point: hq(Fs), Polygon: hq(Gs)}),
        Us = R(is, {Track: hq(ys)}), Ws = R(js, {
            ExtendedData: Ks, Link: function (b, c) {
                qq(Vs, b, c)
            }, address: P(U), description: P(U), name: P(U), open: P(wq), phoneNumber: P(U), visibility: P(wq)
        }), Vs = R(js, {href: P(us)}), Ps = R(js, {LinearRing: iq(ws)}), Xs = R(js, {
            Style: P(Is), key: P(U), styleUrl: P(function (b) {
                var c = Aa(Mp(b, !1));
                return null != b.baseURI ? $r(b.baseURI, c).toString() : c
            })
        }), Zs = R(js, {
            ExtendedData: Ks,
            MultiGeometry: P(Ds, "geometry"),
            LineString: P(As, "geometry"),
            LinearRing: P(Cs, "geometry"),
            Point: P(Fs, "geometry"),
            Polygon: P(Gs, "geometry"),
            Style: P(Is),
            StyleMap: function (b, c) {
                var d = T(void 0, Ys, b, c);
                if (m(d)) {
                    var e = c[c.length - 1];
                    ga(d) ? e.Style = d : ia(d) && (e.styleUrl = d)
                }
            },
            address: P(U),
            description: P(U),
            name: P(U),
            open: P(wq),
            phoneNumber: P(U),
            styleUrl: P(us),
            visibility: P(wq)
        }, R(is, {
            MultiTrack: P(function (b, c) {
                var d = T([], Us, b, c);
                if (m(d)) {
                    var e = new Qm(null);
                    Sm(e, d);
                    return e
                }
            }, "geometry"), Track: P(ys, "geometry")
        })), $s =
            R(js, {color: P(ss), fill: P(wq), outline: P(wq)}), Ns = R(js, {
            SimpleData: function (b, c) {
                var d = b.getAttribute("name");
                if (null !== d) {
                    var e = U(b);
                    c[c.length - 1][d] = e
                }
            }
        }), Js = R(js, {
            IconStyle: function (b, c) {
                var d = T({}, Rs, b, c);
                if (m(d)) {
                    var e = c[c.length - 1], f = yb(d, "Icon", {}), g;
                    g = f.href;
                    g = m(g) ? g : "https://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
                    var h, k, n, p = d.hotSpot;
                    m(p) ? (h = [p.x, p.y], k = p.Re, n = p.Se) : "https://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png" === g ? (h = ms, n = k = "pixels") : /^http:\/\/maps\.(?:google|gstatic)\.com\//.test(g) &&
                        (h = [.5, 0], n = k = "fraction");
                    var q, p = f.x, r = f.y;
                    m(p) && m(r) && (q = [p, r]);
                    var s, p = f.w, f = f.h;
                    m(p) && m(f) && (s = [p, f]);
                    var u, f = d.heading;
                    m(f) && (u = Yb(f));
                    d = d.scale;
                    "https://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png" == g && (s = ns);
                    h = new wj({
                        anchor: h,
                        anchorOrigin: "bottom-left",
                        anchorXUnits: k,
                        anchorYUnits: n,
                        crossOrigin: "anonymous",
                        offset: q,
                        offsetOrigin: "bottom-left",
                        rotation: u,
                        scale: d,
                        size: s,
                        src: g
                    });
                    e.imageStyle = h
                }
            }, LabelStyle: function (b, c) {
                var d = T({}, Ss, b, c);
                m(d) && (c[c.length - 1].textStyle = new fs({
                    fill: new pl({
                        color: yb(d,
                            "color", ks)
                    }), scale: d.scale
                }))
            }, LineStyle: function (b, c) {
                var d = T({}, Ts, b, c);
                m(d) && (c[c.length - 1].strokeStyle = new kl({color: yb(d, "color", ks), width: yb(d, "width", 1)}))
            }, PolyStyle: function (b, c) {
                var d = T({}, $s, b, c);
                if (m(d)) {
                    var e = c[c.length - 1];
                    e.fillStyle = new pl({color: yb(d, "color", ks)});
                    var f = d.fill;
                    m(f) && (e.fill = f);
                    d = d.outline;
                    m(d) && (e.outline = d)
                }
            }
        }), Ys = R(js, {
            Pair: function (b, c) {
                var d = T({}, Xs, b, c);
                if (m(d)) {
                    var e = d.key;
                    m(e) && "normal" == e && (e = d.styleUrl, m(e) && (c[c.length - 1] = e), d = d.Style, m(d) && (c[c.length -
                    1] = d))
                }
            }
        });
    l = gs.prototype;
    l.cg = function (b, c) {
        Qp(b);
        var d = R(js, {
            Folder: gq(this.cg, this),
            Placemark: hq(this.Je, this),
            Style: ra(this.Jl, this),
            StyleMap: ra(this.Il, this)
        }), d = T([], d, b, c, this);
        if (m(d)) return d
    };
    l.Je = function (b, c) {
        var d = T({geometry: null}, Zs, b, c);
        if (m(d)) {
            var e = new N, f = b.getAttribute("id");
            null === f || e.c(f);
            f = c[0];
            null != d.geometry && up(d.geometry, !1, f);
            e.C(d);
            this.b && e.i(this.c);
            return e
        }
    };
    l.Jl = function (b, c) {
        var d = b.getAttribute("id");
        if (null !== d) {
            var e = Is(b, c);
            m(e) && (d = null != b.baseURI ? $r(b.baseURI, "#" + d).toString() : "#" + d, this.d[d] = e)
        }
    };
    l.Il = function (b, c) {
        var d = b.getAttribute("id");
        if (null !== d) {
            var e = T(void 0, Ys, b, c);
            m(e) && (d = null != b.baseURI ? $r(b.baseURI, "#" + d).toString() : "#" + d, this.d[d] = e)
        }
    };
    l.dg = function (b, c) {
        if (!Va(js, b.namespaceURI)) return null;
        var d = this.Je(b, [sp(this, b, c)]);
        return m(d) ? d : null
    };
    l.Ob = function (b, c) {
        if (!Va(js, b.namespaceURI)) return [];
        var d;
        d = Qp(b);
        if ("Document" == d || "Folder" == d) return d = this.cg(b, [sp(this, b, c)]), m(d) ? d : [];
        if ("Placemark" == d) return d = this.Je(b, [sp(this, b, c)]), m(d) ? [d] : [];
        if ("kml" == d) {
            d = [];
            var e;
            for (e = b.firstElementChild; null !== e; e = e.nextElementSibling) {
                var f = this.Ob(e, c);
                m(f) && $a(d, f)
            }
            return d
        }
        return []
    };
    l.Dl = function (b) {
        if (Tp(b)) return at(this, b);
        if (Wp(b)) return bt(this, b);
        if (ia(b)) return b = fq(b), at(this, b)
    };

    function at(b, c) {
        var d;
        for (d = c.firstChild; null !== d; d = d.nextSibling) if (1 == d.nodeType) {
            var e = bt(b, d);
            if (m(e)) return e
        }
    }

    function bt(b, c) {
        var d;
        for (d = c.firstElementChild; null !== d; d = d.nextElementSibling) if (Va(js, d.namespaceURI) && "name" == d.localName) return U(d);
        for (d = c.firstElementChild; null !== d; d = d.nextElementSibling) {
            var e = Qp(d);
            if (Va(js, d.namespaceURI) && ("Document" == e || "Folder" == e || "Placemark" == e || "kml" == e) && (e = bt(b, d), m(e))) return e
        }
    }

    l.El = function (b) {
        var c = [];
        Tp(b) ? $a(c, ct(this, b)) : Wp(b) ? $a(c, dt(this, b)) : ia(b) && (b = fq(b), $a(c, ct(this, b)));
        return c
    };

    function ct(b, c) {
        var d, e = [];
        for (d = c.firstChild; null !== d; d = d.nextSibling) 1 == d.nodeType && $a(e, dt(b, d));
        return e
    }

    function dt(b, c) {
        var d, e = [];
        for (d = c.firstElementChild; null !== d; d = d.nextElementSibling) if (Va(js, d.namespaceURI) && "NetworkLink" == d.localName) {
            var f = T({}, Ws, d, []);
            e.push(f)
        }
        for (d = c.firstElementChild; null !== d; d = d.nextElementSibling) f = Qp(d), !Va(js, d.namespaceURI) || "Document" != f && "Folder" != f && "kml" != f || $a(e, dt(b, d));
        return e
    }

    function et(b, c) {
        var d = qg(c), d = [255 * (4 == d.length ? d[3] : 1), d[2], d[1], d[0]], e;
        for (e = 0; 4 > e; ++e) {
            var f = parseInt(d[e], 10).toString(16);
            d[e] = 1 == f.length ? "0" + f : f
        }
        Eq(b, d.join(""))
    }

    function ft(b, c, d) {
        rq({node: b}, gt, ht, [c], d)
    }

    function it(b, c, d) {
        var e = {node: b};
        null != c.aa && b.setAttribute("id", c.aa);
        b = c.I();
        var f = c.a;
        m(f) && (f = f.call(c, 0), null !== f && 0 < f.length && (b.Style = f[0], f = f[0].d, null === f || (b.name = f.b)));
        f = jt[d[d.length - 1].node.namespaceURI];
        b = pq(b, f);
        rq(e, kt, oq, b, d, f);
        b = d[0];
        c = c.R();
        null != c && (c = up(c, !0, b));
        rq(e, kt, lt, [c], d)
    }

    function mt(b, c, d) {
        var e = c.k;
        b = {node: b};
        b.layout = c.a;
        b.stride = c.B;
        rq(b, nt, ot, [e], d)
    }

    function pt(b, c, d) {
        c = c.kd();
        var e = c.shift();
        b = {node: b};
        rq(b, qt, rt, c, d);
        rq(b, qt, st, [e], d)
    }

    function tt(b, c) {
        Fq(b, c * c)
    }

    var ut = kq(js, ["Document", "Placemark"]), xt = kq(js, {
            Document: S(function (b, c, d) {
                rq({node: b}, vt, wt, c, d)
            }), Placemark: S(it)
        }), vt = kq(js, {Placemark: S(it)}), yt = {
            Point: "Point",
            LineString: "LineString",
            LinearRing: "LinearRing",
            Polygon: "Polygon",
            MultiPoint: "MultiGeometry",
            MultiLineString: "MultiGeometry",
            MultiPolygon: "MultiGeometry"
        }, zt = kq(js, ["href"], kq(is, ["x", "y", "w", "h"])),
        At = kq(js, {href: S(Eq)}, kq(is, {x: S(Fq), y: S(Fq), w: S(Fq), h: S(Fq)})),
        Bt = kq(js, ["scale", "heading", "Icon", "hotSpot"]), Dt = kq(js, {
            Icon: S(function (b,
                              c, d) {
                b = {node: b};
                var e = zt[d[d.length - 1].node.namespaceURI], f = pq(c, e);
                rq(b, At, oq, f, d, e);
                e = zt[is[0]];
                f = pq(c, e);
                rq(b, At, Ct, f, d, e)
            }), heading: S(Fq), hotSpot: S(function (b, c) {
                b.setAttribute("x", c.x);
                b.setAttribute("y", c.y);
                b.setAttribute("xunits", c.Re);
                b.setAttribute("yunits", c.Se)
            }), scale: S(tt)
        }), Et = kq(js, ["color", "scale"]), Ft = kq(js, {color: S(et), scale: S(tt)}), Gt = kq(js, ["color", "width"]),
        Ht = kq(js, {color: S(et), width: S(Fq)}), gt = kq(js, {LinearRing: S(mt)}),
        It = kq(js, {LineString: S(mt), Point: S(mt), Polygon: S(pt)}),
        jt = kq(js, "name open visibility address phoneNumber description styleUrl Style".split(" ")), kt = kq(js, {
            MultiGeometry: S(function (b, c, d) {
                b = {node: b};
                var e = c.O(), f, g;
                "MultiPoint" == e ? (f = c.Ed(), g = Jt) : "MultiLineString" == e ? (f = c.Lc(), g = Kt) : "MultiPolygon" == e && (f = c.pd(), g = Lt);
                rq(b, It, g, f, d)
            }),
            LineString: S(mt),
            LinearRing: S(mt),
            Point: S(mt),
            Polygon: S(pt),
            Style: S(function (b, c, d) {
                b = {node: b};
                var e = {}, f = c.f, g = c.b, h = c.e;
                c = c.d;
                null === h || (e.IconStyle = h);
                null === c || (e.LabelStyle = c);
                null === g || (e.LineStyle = g);
                null === f || (e.PolyStyle =
                    f);
                c = Mt[d[d.length - 1].node.namespaceURI];
                e = pq(e, c);
                rq(b, Nt, oq, e, d, c)
            }),
            address: S(Eq),
            description: S(Eq),
            name: S(Eq),
            open: S(Dq),
            phoneNumber: S(Eq),
            styleUrl: S(Eq),
            visibility: S(Dq)
        }), nt = kq(js, {
            coordinates: S(function (b, c, d) {
                d = d[d.length - 1];
                var e = d.layout;
                d = d.stride;
                var f;
                "XY" == e || "XYM" == e ? f = 2 : ("XYZ" == e || "XYZM" == e) && (f = 3);
                var g, h = c.length, k = "";
                if (0 < h) {
                    k += c[0];
                    for (e = 1; e < f; ++e) k += "," + c[e];
                    for (g = d; g < h; g += d) for (k += " " + c[g], e = 1; e < f; ++e) k += "," + c[g + e]
                }
                Eq(b, k)
            })
        }), qt = kq(js, {outerBoundaryIs: S(ft), innerBoundaryIs: S(ft)}),
        Ot = kq(js, {color: S(et)}), Mt = kq(js, ["IconStyle", "LabelStyle", "LineStyle", "PolyStyle"]), Nt = kq(js, {
            IconStyle: S(function (b, c, d) {
                b = {node: b};
                var e = {}, f = c.gb(), g = c.jd(), h = {href: c.a.e};
                if (null !== f) {
                    h.w = f[0];
                    h.h = f[1];
                    var k = c.wb(), n = c.Cb();
                    null !== n && null !== g && 0 !== n[0] && n[1] !== f[1] && (h.x = n[0], h.y = g[1] - (n[1] + f[1]));
                    null === k || 0 === k[0] || k[1] === f[1] || (e.hotSpot = {
                        x: k[0],
                        Re: "pixels",
                        y: f[1] - k[1],
                        Se: "pixels"
                    })
                }
                e.Icon = h;
                f = c.j;
                1 !== f && (e.scale = f);
                c = c.i;
                0 !== c && (e.heading = c);
                c = Bt[d[d.length - 1].node.namespaceURI];
                e = pq(e, c);
                rq(b, Dt, oq, e, d, c)
            }), LabelStyle: S(function (b, c, d) {
                b = {node: b};
                var e = {}, f = c.a;
                null === f || (e.color = f.a);
                c = c.d;
                m(c) && 1 !== c && (e.scale = c);
                c = Et[d[d.length - 1].node.namespaceURI];
                e = pq(e, c);
                rq(b, Ft, oq, e, d, c)
            }), LineStyle: S(function (b, c, d) {
                b = {node: b};
                var e = Gt[d[d.length - 1].node.namespaceURI];
                c = pq({color: c.a, width: c.d}, e);
                rq(b, Ht, oq, c, d, e)
            }), PolyStyle: S(function (b, c, d) {
                rq({node: b}, Ot, Pt, [c.a], d)
            })
        });

    function Ct(b, c, d) {
        return Lp(is[0], "gx:" + d)
    }

    function wt(b, c) {
        return Lp(c[c.length - 1].node.namespaceURI, "Placemark")
    }

    function lt(b, c) {
        if (null != b) return Lp(c[c.length - 1].node.namespaceURI, yt[b.O()])
    }

    var Pt = mq("color"), ot = mq("coordinates"), rt = mq("innerBoundaryIs"), Jt = mq("Point"), Kt = mq("LineString"),
        ht = mq("LinearRing"), Lt = mq("Polygon"), st = mq("outerBoundaryIs");
    gs.prototype.a = function (b, c) {
        c = tp(this, c);
        var d = Lp(js[4], "kml");
        eq(d, "http://www.w3.org/2000/xmlns/", "xmlns:gx", is[0]);
        eq(d, "http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        eq(d, "http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "http://www.opengis.net/kml/2.2 https://developers.google.com/kml/schema/kml22gx.xsd");
        var e = {node: d}, f = {};
        1 < b.length ? f.Document = b : 1 == b.length && (f.Placemark = b[0]);
        var g = ut[d.namespaceURI], f = pq(f, g);
        rq(e, xt, oq, f, [c], g);
        return d
    };

    function Qt() {
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae("EPSG:4326")
    }

    v(Qt, sq);

    function Rt(b, c) {
        c[c.length - 1].Uc[b.getAttribute("k")] = b.getAttribute("v")
    }

    var St = [null], Tt = R(St, {
        nd: function (b, c) {
            c[c.length - 1].qc.push(b.getAttribute("ref"))
        }, tag: Rt
    }), Vt = R(St, {
        node: function (b, c) {
            var d = c[0], e = c[c.length - 1], f = b.getAttribute("id"),
                g = [parseFloat(b.getAttribute("lon")), parseFloat(b.getAttribute("lat"))];
            e.Cf[f] = g;
            var h = T({Uc: {}}, Ut, b, c);
            vb(h.Uc) || (g = new Jk(g), up(g, !1, d), d = new N(g), d.c(f), d.C(h.Uc), e.features.push(d))
        }, way: function (b, c) {
            for (var d = c[0], e = b.getAttribute("id"), f = T({
                qc: [],
                Uc: {}
            }, Tt, b, c), g = c[c.length - 1], h = [], k = 0, n = f.qc.length; k < n; k++) $a(h, g.Cf[f.qc[k]]);
            f.qc[0] == f.qc[f.qc.length - 1] ? (k = new F(null), Vk(k, "XY", h, [h.length])) : (k = new Om(null), Pm(k, "XY", h));
            up(k, !1, d);
            d = new N(k);
            d.c(e);
            d.C(f.Uc);
            g.features.push(d)
        }
    }), Ut = R(St, {tag: Rt});
    Qt.prototype.Ob = function (b, c) {
        var d = sp(this, b, c);
        return "osm" == b.localName && (d = T({Cf: {}, features: []}, Vt, b, [d]), m(d.features)) ? d.features : []
    };

    function Wt(b) {
        return b.getAttributeNS("http://www.w3.org/1999/xlink", "href")
    };

    function Xt() {
    }

    Xt.prototype.b = function (b) {
        return Tp(b) ? this.d(b) : Wp(b) ? this.a(b) : ia(b) ? (b = fq(b), this.d(b)) : null
    };

    function Yt() {
    }

    v(Yt, Xt);
    Yt.prototype.d = function (b) {
        for (b = b.firstChild; null !== b; b = b.nextSibling) if (1 == b.nodeType) return this.a(b);
        return null
    };
    Yt.prototype.a = function (b) {
        b = T({}, Zt, b, []);
        return m(b) ? b : null
    };
    var $t = [null, "http://www.opengis.net/ows/1.1"], Zt = R($t, {
        ServiceIdentification: P(function (b, c) {
            return T({}, au, b, c)
        }), ServiceProvider: P(function (b, c) {
            return T({}, bu, b, c)
        }), OperationsMetadata: P(function (b, c) {
            return T({}, cu, b, c)
        })
    }), du = R($t, {
        DeliveryPoint: P(U),
        City: P(U),
        AdministrativeArea: P(U),
        PostalCode: P(U),
        Country: P(U),
        ElectronicMailAddress: P(U)
    }), eu = R($t, {
        Value: jq(function (b) {
            return U(b)
        })
    }), fu = R($t, {
        AllowedValues: P(function (b, c) {
            return T({}, eu, b, c)
        })
    }), hu = R($t, {
        Phone: P(function (b, c) {
            return T({},
                gu, b, c)
        }), Address: P(function (b, c) {
            return T({}, du, b, c)
        })
    }), ju = R($t, {
        HTTP: P(function (b, c) {
            return T({}, iu, b, c)
        })
    }), iu = R($t, {
        Get: jq(function (b, c) {
            var d = Wt(b);
            return m(d) ? T({href: d}, ku, b, c) : void 0
        }), Post: void 0
    }), lu = R($t, {
        DCP: P(function (b, c) {
            return T({}, ju, b, c)
        })
    }), cu = R($t, {
        Operation: function (b, c) {
            var d = b.getAttribute("name"), e = T({}, lu, b, c);
            m(e) && (c[c.length - 1][d] = e)
        }
    }), gu = R($t, {Voice: P(U), Facsimile: P(U)}), ku = R($t, {
        Constraint: jq(function (b, c) {
            var d = b.getAttribute("name");
            return m(d) ? T({name: d}, fu, b, c) :
                void 0
        })
    }), mu = R($t, {
        IndividualName: P(U), PositionName: P(U), ContactInfo: P(function (b, c) {
            return T({}, hu, b, c)
        })
    }), au = R($t, {Title: P(U), ServiceTypeVersion: P(U), ServiceType: P(U)}), bu = R($t, {
        ProviderName: P(U), ProviderSite: P(Wt), ServiceContact: P(function (b, c) {
            return T({}, mu, b, c)
        })
    });

    function nu(b, c, d, e) {
        var f;
        m(e) ? f = m(void 0) ? void 0 : 0 : (e = [], f = 0);
        var g, h;
        for (g = 0; g < c;) for (h = b[g++], e[f++] = b[g++], e[f++] = h, h = 2; h < d; ++h) e[f++] = b[g++];
        e.length = f
    };

    function ou(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae("EPSG:4326");
        this.a = m(b.factor) ? b.factor : 1E5
    }

    v(ou, Ar);

    function pu(b, c, d) {
        d = m(d) ? d : 1E5;
        var e, f = Array(c);
        for (e = 0; e < c; ++e) f[e] = 0;
        var g, h;
        g = 0;
        for (h = b.length; g < h;) for (e = 0; e < c; ++e, ++g) {
            var k = b[g], n = k - f[e];
            f[e] = k;
            b[g] = n
        }
        return qu(b, d)
    }

    function ru(b, c, d) {
        var e = m(d) ? d : 1E5, f = Array(c);
        for (d = 0; d < c; ++d) f[d] = 0;
        b = su(b, e);
        var g, e = 0;
        for (g = b.length; e < g;) for (d = 0; d < c; ++d, ++e) f[d] += b[e], b[e] = f[d];
        return b
    }

    function qu(b, c) {
        var d = m(c) ? c : 1E5, e, f;
        e = 0;
        for (f = b.length; e < f; ++e) b[e] = Math.round(b[e] * d);
        d = 0;
        for (e = b.length; d < e; ++d) f = b[d], b[d] = 0 > f ? ~(f << 1) : f << 1;
        d = "";
        e = 0;
        for (f = b.length; e < f; ++e) {
            for (var g = b[e], h = void 0, k = ""; 32 <= g;) h = (32 | g & 31) + 63, k += String.fromCharCode(h), g >>= 5;
            h = g + 63;
            k += String.fromCharCode(h);
            d += k
        }
        return d
    }

    function su(b, c) {
        var d = m(c) ? c : 1E5, e = [], f = 0, g = 0, h, k;
        h = 0;
        for (k = b.length; h < k; ++h) {
            var n = b.charCodeAt(h) - 63, f = f | (n & 31) << g;
            32 > n ? (e.push(f), g = f = 0) : g += 5
        }
        f = 0;
        for (g = e.length; f < g; ++f) h = e[f], e[f] = h & 1 ? ~(h >> 1) : h >> 1;
        f = 0;
        for (g = e.length; f < g; ++f) e[f] /= d;
        return e
    }

    l = ou.prototype;
    l.Pc = function (b, c) {
        var d = this.Rc(b, c);
        return new N(d)
    };
    l.He = function (b, c) {
        return [this.Pc(b, c)]
    };
    l.Rc = function (b, c) {
        var d = ru(b, 2, this.a);
        nu(d, d.length, 2, d);
        d = Dk(d, 0, d.length, 2);
        return up(new Om(d), !1, tp(this, c))
    };
    l.ae = function (b, c) {
        var d = b.R();
        return null != d ? this.Xc(d, c) : ""
    };
    l.xg = function (b, c) {
        return this.ae(b[0], c)
    };
    l.Xc = function (b, c) {
        b = up(b, !0, tp(this, c));
        var d = b.k, e = b.B;
        nu(d, d.length, e, d);
        return pu(d, e, this.a)
    };

    function tu(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.defaultDataProjection = Ae(null != b.defaultDataProjection ? b.defaultDataProjection : "EPSG:4326")
    }

    v(tu, xp);

    function uu(b, c) {
        var d = [], e, f, g, h;
        g = 0;
        for (h = b.length; g < h; ++g) e = b[g], 0 < g && d.pop(), 0 <= e ? f = c[e] : f = c[~e].slice().reverse(), d.push.apply(d, f);
        e = 0;
        for (f = d.length; e < f; ++e) d[e] = d[e].slice();
        return d
    }

    function vu(b, c, d, e, f) {
        b = b.geometries;
        var g = [], h, k;
        h = 0;
        for (k = b.length; h < k; ++h) g[h] = wu(b[h], c, d, e, f);
        return g
    }

    function wu(b, c, d, e, f) {
        var g = b.type, h = xu[g];
        c = "Point" === g || "MultiPoint" === g ? h(b, d, e) : h(b, c);
        d = new N;
        d.Sa(up(c, !1, f));
        m(b.id) && d.c(b.id);
        m(b.properties) && d.C(b.properties);
        return d
    }

    tu.prototype.b = function (b, c) {
        if ("Topology" == b.type) {
            var d, e = null, f = null;
            m(b.transform) && (d = b.transform, e = d.scale, f = d.translate);
            var g = b.arcs;
            if (m(d)) {
                d = e;
                var h = f, k, n;
                k = 0;
                for (n = g.length; k < n; ++k) for (var p = g[k], q = d, r = h, s = 0, u = 0, z = void 0, y = void 0, A = void 0, y = 0, A = p.length; y < A; ++y) z = p[y], s += z[0], u += z[1], z[0] = s, z[1] = u, yu(z, q, r)
            }
            d = [];
            h = qb(b.objects);
            k = 0;
            for (n = h.length; k < n; ++k) "GeometryCollection" === h[k].type ? (p = h[k], d.push.apply(d, vu(p, g, e, f, c))) : (p = h[k], d.push(wu(p, g, e, f, c)));
            return d
        }
        return []
    };

    function yu(b, c, d) {
        b[0] = b[0] * c[0] + d[0];
        b[1] = b[1] * c[1] + d[1]
    }

    tu.prototype.Ja = function () {
        return this.defaultDataProjection
    };
    var xu = {
        Point: function (b, c, d) {
            b = b.coordinates;
            null === c || null === d || yu(b, c, d);
            return new Jk(b)
        }, LineString: function (b, c) {
            var d = uu(b.arcs, c);
            return new Om(d)
        }, Polygon: function (b, c) {
            var d = [], e, f;
            e = 0;
            for (f = b.arcs.length; e < f; ++e) d[e] = uu(b.arcs[e], c);
            return new F(d)
        }, MultiPoint: function (b, c, d) {
            b = b.coordinates;
            var e, f;
            if (null !== c && null !== d) for (e = 0, f = b.length; e < f; ++e) yu(b[e], c, d);
            return new Tm(b)
        }, MultiLineString: function (b, c) {
            var d = [], e, f;
            e = 0;
            for (f = b.arcs.length; e < f; ++e) d[e] = uu(b.arcs[e], c);
            return new Qm(d)
        },
        MultiPolygon: function (b, c) {
            var d = [], e, f, g, h, k, n;
            k = 0;
            for (n = b.arcs.length; k < n; ++k) {
                e = b.arcs[k];
                f = [];
                g = 0;
                for (h = e.length; g < h; ++g) f[g] = uu(e[g], c);
                d[k] = f
            }
            return new Um(d)
        }
    };

    function zu(b) {
        b = m(b) ? b : {};
        this.f = b.featureType;
        this.b = b.featureNS;
        this.d = m(b.gmlFormat) ? b.gmlFormat : new Hq;
        this.c = m(b.schemaLocation) ? b.schemaLocation : "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd";
        this.defaultDataProjection = null
    }

    v(zu, sq);
    zu.prototype.Ob = function (b, c) {
        var d = {featureType: this.f, featureNS: this.b};
        Cb(d, sp(this, b, m(c) ? c : {}));
        d = [d];
        this.d.d["http://www.opengis.net/gml"].featureMember = hq(vq.prototype.Qd);
        d = T([], this.d.d, b, d, this.d);
        m(d) || (d = []);
        return d
    };
    zu.prototype.g = function (b) {
        if (Tp(b)) return Au(b);
        if (Wp(b)) return T({}, Bu, b, []);
        if (ia(b)) return b = fq(b), Au(b)
    };
    zu.prototype.e = function (b) {
        if (Tp(b)) return Cu(this, b);
        if (Wp(b)) return Du(this, b);
        if (ia(b)) return b = fq(b), Cu(this, b)
    };

    function Cu(b, c) {
        for (var d = c.firstChild; null !== d; d = d.nextSibling) if (1 == d.nodeType) return Du(b, d)
    }

    var Eu = {"http://www.opengis.net/gml": {boundedBy: P(vq.prototype.Sd, "bounds")}};

    function Du(b, c) {
        var d = {}, e = Cq(c.getAttribute("numberOfFeatures"));
        d.numberOfFeatures = e;
        return T(d, Eu, c, [], b.d)
    }

    var Fu = {"http://www.opengis.net/wfs": {totalInserted: P(Bq), totalUpdated: P(Bq), totalDeleted: P(Bq)}}, Gu = {
        "http://www.opengis.net/ogc": {
            FeatureId: hq(function (b) {
                return b.getAttribute("fid")
            })
        }
    }, Hu = {
        "http://www.opengis.net/wfs": {
            Feature: function (b, c) {
                qq(Gu, b, c)
            }
        }
    }, Bu = {
        "http://www.opengis.net/wfs": {
            TransactionSummary: P(function (b, c) {
                return T({}, Fu, b, c)
            }, "transactionSummary"), InsertResults: P(function (b, c) {
                return T([], Hu, b, c)
            }, "insertIds")
        }
    };

    function Au(b) {
        for (b = b.firstChild; null !== b; b = b.nextSibling) if (1 == b.nodeType) return T({}, Bu, b, [])
    }

    var Iu = {"http://www.opengis.net/wfs": {PropertyName: S(Eq)}};

    function Ju(b, c) {
        var d = Lp("http://www.opengis.net/ogc", "Filter"), e = Lp("http://www.opengis.net/ogc", "FeatureId");
        d.appendChild(e);
        e.setAttribute("fid", c);
        b.appendChild(d)
    }

    var Ku = {
        "http://www.opengis.net/wfs": {
            Insert: S(function (b, c, d) {
                var e = d[d.length - 1], e = Lp(e.featureNS, e.featureType);
                b.appendChild(e);
                Hq.prototype.wg(e, c, d)
            }), Update: S(function (b, c, d) {
                var e = d[d.length - 1], f = e.featureType, g = e.featurePrefix, g = m(g) ? g : "feature",
                    h = e.featureNS;
                b.setAttribute("typeName", g + ":" + f);
                eq(b, "http://www.w3.org/2000/xmlns/", "xmlns:" + g, h);
                f = c.aa;
                if (m(f)) {
                    for (var g = c.G(), h = [], k = 0, n = g.length; k < n; k++) {
                        var p = c.get(g[k]);
                        m(p) && h.push({name: g[k], value: p})
                    }
                    rq({node: b, srsName: e.srsName}, Ku,
                        mq("Property"), h, d);
                    Ju(b, f)
                }
            }), Delete: S(function (b, c, d) {
                var e = d[d.length - 1];
                d = e.featureType;
                var f = e.featurePrefix, f = m(f) ? f : "feature", e = e.featureNS;
                b.setAttribute("typeName", f + ":" + d);
                eq(b, "http://www.w3.org/2000/xmlns/", "xmlns:" + f, e);
                c = c.aa;
                m(c) && Ju(b, c)
            }), Property: S(function (b, c, d) {
                var e = Lp("http://www.opengis.net/wfs", "Name");
                b.appendChild(e);
                Eq(e, c.name);
                null != c.value && (e = Lp("http://www.opengis.net/wfs", "Value"), b.appendChild(e), c.value instanceof lk ? Hq.prototype.be(e, c.value, d) : Eq(e, c.value))
            }),
            Native: S(function (b, c) {
                m(c.qm) && b.setAttribute("vendorId", c.qm);
                m(c.Tl) && b.setAttribute("safeToIgnore", c.Tl);
                m(c.value) && Eq(b, c.value)
            })
        }
    }, Lu = {
        "http://www.opengis.net/wfs": {
            Query: S(function (b, c, d) {
                var e = d[d.length - 1], f = e.featurePrefix, g = e.featureNS, h = e.propertyNames, k = e.srsName;
                b.setAttribute("typeName", (m(f) ? f + ":" : "") + c);
                m(k) && b.setAttribute("srsName", k);
                m(g) && eq(b, "http://www.w3.org/2000/xmlns/", "xmlns:" + f, g);
                c = Ab(e);
                c.node = b;
                rq(c, Iu, mq("PropertyName"), h, d);
                e = e.bbox;
                m(e) && (h = Lp("http://www.opengis.net/ogc",
                    "Filter"), c = d[d.length - 1].geometryName, f = Lp("http://www.opengis.net/ogc", "BBOX"), h.appendChild(f), g = Lp("http://www.opengis.net/ogc", "PropertyName"), Eq(g, c), f.appendChild(g), Hq.prototype.be(f, e, d), b.appendChild(h))
            })
        }
    };
    zu.prototype.n = function (b) {
        var c = Lp("http://www.opengis.net/wfs", "GetFeature");
        c.setAttribute("service", "WFS");
        c.setAttribute("version", "1.1.0");
        m(b) && (m(b.handle) && c.setAttribute("handle", b.handle), m(b.outputFormat) && c.setAttribute("outputFormat", b.outputFormat), m(b.maxFeatures) && c.setAttribute("maxFeatures", b.maxFeatures), m(b.resultType) && c.setAttribute("resultType", b.resultType), m(b.im) && c.setAttribute("startIndex", b.im), m(b.count) && c.setAttribute("count", b.count));
        eq(c, "http://www.w3.org/2001/XMLSchema-instance",
            "xsi:schemaLocation", this.c);
        var d = b.featureTypes;
        b = [{
            node: c,
            srsName: b.srsName,
            featureNS: m(b.featureNS) ? b.featureNS : this.b,
            featurePrefix: b.featurePrefix,
            geometryName: b.geometryName,
            bbox: b.bbox,
            ag: m(b.ag) ? b.ag : []
        }];
        var e = Ab(b[b.length - 1]);
        e.node = c;
        rq(e, Lu, mq("Query"), d, b);
        return c
    };
    zu.prototype.q = function (b, c, d, e) {
        var f = [], g = Lp("http://www.opengis.net/wfs", "Transaction");
        g.setAttribute("service", "WFS");
        g.setAttribute("version", "1.1.0");
        var h, k;
        m(e) && (h = m(e.gmlOptions) ? e.gmlOptions : {}, m(e.handle) && g.setAttribute("handle", e.handle));
        eq(g, "http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", this.c);
        null != b && (k = {
            node: g,
            featureNS: e.featureNS,
            featureType: e.featureType,
            featurePrefix: e.featurePrefix
        }, Cb(k, h), rq(k, Ku, mq("Insert"), b, f));
        null != c && (k = {
            node: g, featureNS: e.featureNS,
            featureType: e.featureType, featurePrefix: e.featurePrefix
        }, Cb(k, h), rq(k, Ku, mq("Update"), c, f));
        null != d && rq({
            node: g,
            featureNS: e.featureNS,
            featureType: e.featureType,
            featurePrefix: e.featurePrefix
        }, Ku, mq("Delete"), d, f);
        m(e.nativeElements) && rq({
            node: g,
            featureNS: e.featureNS,
            featureType: e.featureType,
            featurePrefix: e.featurePrefix
        }, Ku, mq("Native"), e.nativeElements, f);
        return g
    };
    zu.prototype.Ke = function (b) {
        for (b = b.firstChild; null !== b; b = b.nextSibling) if (1 == b.nodeType) return this.Vd(b);
        return null
    };
    zu.prototype.Vd = function (b) {
        if (null != b.firstElementChild && null != b.firstElementChild.firstElementChild) for (b = b.firstElementChild.firstElementChild, b = b.firstElementChild; null !== b; b = b.nextElementSibling) if (0 !== b.childNodes.length && (1 !== b.childNodes.length || 3 !== b.firstChild.nodeType)) {
            var c = [{}];
            this.d.Sd(b, c);
            return Ae(c.pop().srsName)
        }
        return null
    };

    function Mu(b) {
        b = m(b) ? b : {};
        this.defaultDataProjection = null;
        this.a = m(b.splitCollection) ? b.splitCollection : !1
    }

    v(Mu, Ar);

    function Nu(b) {
        b = b.Q();
        return 0 == b.length ? "" : b[0] + " " + b[1]
    }

    function Ou(b) {
        b = b.Q();
        for (var c = [], d = 0, e = b.length; d < e; ++d) c.push(b[d][0] + " " + b[d][1]);
        return c.join(",")
    }

    function Pu(b) {
        var c = [];
        b = b.kd();
        for (var d = 0, e = b.length; d < e; ++d) c.push("(" + Ou(b[d]) + ")");
        return c.join(",")
    }

    function Qu(b) {
        var c = b.O();
        b = (0, Ru[c])(b);
        c = c.toUpperCase();
        return 0 === b.length ? c + " EMPTY" : c + "(" + b + ")"
    }

    var Ru = {
        Point: Nu, LineString: Ou, Polygon: Pu, MultiPoint: function (b) {
            var c = [];
            b = b.Ed();
            for (var d = 0, e = b.length; d < e; ++d) c.push("(" + Nu(b[d]) + ")");
            return c.join(",")
        }, MultiLineString: function (b) {
            var c = [];
            b = b.Lc();
            for (var d = 0, e = b.length; d < e; ++d) c.push("(" + Ou(b[d]) + ")");
            return c.join(",")
        }, MultiPolygon: function (b) {
            var c = [];
            b = b.pd();
            for (var d = 0, e = b.length; d < e; ++d) c.push("(" + Pu(b[d]) + ")");
            return c.join(",")
        }, GeometryCollection: function (b) {
            var c = [];
            b = b.nf();
            for (var d = 0, e = b.length; d < e; ++d) c.push(Qu(b[d]));
            return c.join(",")
        }
    };
    l = Mu.prototype;
    l.Pc = function (b, c) {
        var d = this.Rc(b, c);
        if (m(d)) {
            var e = new N;
            e.Sa(d);
            return e
        }
        return null
    };
    l.He = function (b, c) {
        var d = [], e = this.Rc(b, c);
        this.a && "GeometryCollection" == e.O() ? d = e.c : d = [e];
        for (var f = [], g = 0, h = d.length; g < h; ++g) e = new N, e.Sa(d[g]), f.push(e);
        return f
    };
    l.Rc = function (b, c) {
        var d;
        d = new Su(new Tu(b));
        d.a = Uu(d.d);
        d = Vu(d);
        return m(d) ? up(d, !1, c) : null
    };
    l.ae = function (b, c) {
        var d = b.R();
        return m(d) ? this.Xc(d, c) : ""
    };
    l.xg = function (b, c) {
        if (1 == b.length) return this.ae(b[0], c);
        for (var d = [], e = 0, f = b.length; e < f; ++e) d.push(b[e].R());
        d = new Hm(d);
        return this.Xc(d, c)
    };
    l.Xc = function (b, c) {
        return Qu(up(b, !0, c))
    };

    function Tu(b) {
        this.d = b;
        this.a = -1
    }

    function Wu(b, c) {
        var d = m(c) ? c : !1;
        return "0" <= b && "9" >= b || "." == b && !d
    }

    function Uu(b) {
        var c = b.d.charAt(++b.a), d = {position: b.a, value: c};
        if ("(" == c) d.type = 2; else if ("," == c) d.type = 5; else if (")" == c) d.type = 3; else if (Wu(c) || "-" == c) {
            d.type = 4;
            var e, c = b.a, f = !1;
            do "." == e && (f = !0), e = b.d.charAt(++b.a); while (Wu(e, f));
            b = parseFloat(b.d.substring(c, b.a--));
            d.value = b
        } else if ("a" <= c && "z" >= c || "A" <= c && "Z" >= c) {
            d.type = 1;
            c = b.a;
            do e = b.d.charAt(++b.a); while ("a" <= e && "z" >= e || "A" <= e && "Z" >= e);
            b = b.d.substring(c, b.a--).toUpperCase();
            d.value = b
        } else {
            if (" " == c || "\t" == c || "\r" == c || "\n" == c) return Uu(b);
            if ("" ===
                c) d.type = 6; else throw Error("Unexpected character: " + c);
        }
        return d
    }

    function Su(b) {
        this.d = b
    }

    l = Su.prototype;
    l.match = function (b) {
        if (b = this.a.type == b) this.a = Uu(this.d);
        return b
    };

    function Vu(b) {
        var c = b.a;
        if (b.match(1)) {
            var d = c.value;
            if ("GEOMETRYCOLLECTION" == d) {
                a:{
                    if (b.match(2)) {
                        c = [];
                        do c.push(Vu(b)); while (b.match(5));
                        if (b.match(3)) {
                            b = c;
                            break a
                        }
                    } else if (Xu(b)) {
                        b = [];
                        break a
                    }
                    throw Error(Yu(b));
                }
                return new Hm(b)
            }
            var e = Zu[d], c = $u[d];
            if (!m(e) || !m(c)) throw Error("Invalid geometry type: " + d);
            b = e.call(b);
            return new c(b)
        }
        throw Error(Yu(b));
    }

    l.Ee = function () {
        if (this.match(2)) {
            var b = av(this);
            if (this.match(3)) return b
        } else if (Xu(this)) return null;
        throw Error(Yu(this));
    };
    l.De = function () {
        if (this.match(2)) {
            var b = bv(this);
            if (this.match(3)) return b
        } else if (Xu(this)) return [];
        throw Error(Yu(this));
    };
    l.Fe = function () {
        if (this.match(2)) {
            var b = cv(this);
            if (this.match(3)) return b
        } else if (Xu(this)) return [];
        throw Error(Yu(this));
    };
    l.ml = function () {
        if (this.match(2)) {
            var b;
            if (2 == this.a.type) for (b = [this.Ee()]; this.match(5);) b.push(this.Ee()); else b = bv(this);
            if (this.match(3)) return b
        } else if (Xu(this)) return [];
        throw Error(Yu(this));
    };
    l.ll = function () {
        if (this.match(2)) {
            var b = cv(this);
            if (this.match(3)) return b
        } else if (Xu(this)) return [];
        throw Error(Yu(this));
    };
    l.nl = function () {
        if (this.match(2)) {
            for (var b = [this.Fe()]; this.match(5);) b.push(this.Fe());
            if (this.match(3)) return b
        } else if (Xu(this)) return [];
        throw Error(Yu(this));
    };

    function av(b) {
        for (var c = [], d = 0; 2 > d; ++d) {
            var e = b.a;
            if (b.match(4)) c.push(e.value); else break
        }
        if (2 == c.length) return c;
        throw Error(Yu(b));
    }

    function bv(b) {
        for (var c = [av(b)]; b.match(5);) c.push(av(b));
        return c
    }

    function cv(b) {
        for (var c = [b.De()]; b.match(5);) c.push(b.De());
        return c
    }

    function Xu(b) {
        var c = 1 == b.a.type && "EMPTY" == b.a.value;
        c && (b.a = Uu(b.d));
        return c
    }

    function Yu(b) {
        return "Unexpected `" + b.a.value + "` at position " + b.a.position + " in `" + b.d.d + "`"
    }

    var $u = {POINT: Jk, LINESTRING: Om, POLYGON: F, MULTIPOINT: Tm, MULTILINESTRING: Qm, MULTIPOLYGON: Um}, Zu = {
        POINT: Su.prototype.Ee,
        LINESTRING: Su.prototype.De,
        POLYGON: Su.prototype.Fe,
        MULTIPOINT: Su.prototype.ml,
        MULTILINESTRING: Su.prototype.ll,
        MULTIPOLYGON: Su.prototype.nl
    };

    function dv() {
        this.version = void 0
    }

    v(dv, Xt);
    dv.prototype.d = function (b) {
        for (b = b.firstChild; null !== b; b = b.nextSibling) if (1 == b.nodeType) return this.a(b);
        return null
    };
    dv.prototype.a = function (b) {
        this.version = Aa(b.getAttribute("version"));
        b = T({version: this.version}, ev, b, []);
        return m(b) ? b : null
    };

    function fv(b, c) {
        return T({}, gv, b, c)
    }

    function hv(b, c) {
        return T({}, iv, b, c)
    }

    function jv(b, c) {
        var d = fv(b, c);
        if (m(d)) {
            var e = [Cq(b.getAttribute("width")), Cq(b.getAttribute("height"))];
            d.size = e;
            return d
        }
    }

    function kv(b, c) {
        return T([], lv, b, c)
    }

    var mv = [null, "http://www.opengis.net/wms"], ev = R(mv, {
            Service: P(function (b, c) {
                return T({}, nv, b, c)
            }), Capability: P(function (b, c) {
                return T({}, ov, b, c)
            })
        }), ov = R(mv, {
            Request: P(function (b, c) {
                return T({}, pv, b, c)
            }), Exception: P(function (b, c) {
                return T([], qv, b, c)
            }), Layer: P(function (b, c) {
                return T({}, rv, b, c)
            })
        }), nv = R(mv, {
            Name: P(U),
            Title: P(U),
            Abstract: P(U),
            KeywordList: P(kv),
            OnlineResource: P(Wt),
            ContactInformation: P(function (b, c) {
                return T({}, sv, b, c)
            }),
            Fees: P(U),
            AccessConstraints: P(U),
            LayerLimit: P(Bq),
            MaxWidth: P(Bq),
            MaxHeight: P(Bq)
        }), sv = R(mv, {
            ContactPersonPrimary: P(function (b, c) {
                return T({}, tv, b, c)
            }), ContactPosition: P(U), ContactAddress: P(function (b, c) {
                return T({}, uv, b, c)
            }), ContactVoiceTelephone: P(U), ContactFacsimileTelephone: P(U), ContactElectronicMailAddress: P(U)
        }), tv = R(mv, {ContactPerson: P(U), ContactOrganization: P(U)}), uv = R(mv, {
            AddressType: P(U),
            Address: P(U),
            City: P(U),
            StateOrProvince: P(U),
            PostCode: P(U),
            Country: P(U)
        }), qv = R(mv, {Format: hq(U)}), rv = R(mv, {
            Name: P(U), Title: P(U), Abstract: P(U), KeywordList: P(kv), CRS: jq(U),
            EX_GeographicBoundingBox: P(function (b, c) {
                var d = T({}, vv, b, c);
                if (m(d)) {
                    var e = d.westBoundLongitude, f = d.southBoundLatitude, g = d.eastBoundLongitude,
                        d = d.northBoundLatitude;
                    return m(e) && m(f) && m(g) && m(d) ? [e, f, g, d] : void 0
                }
            }), BoundingBox: jq(function (b) {
                var c = [Aq(b.getAttribute("minx")), Aq(b.getAttribute("miny")), Aq(b.getAttribute("maxx")), Aq(b.getAttribute("maxy"))],
                    d = [Aq(b.getAttribute("resx")), Aq(b.getAttribute("resy"))];
                return {crs: b.getAttribute("CRS"), extent: c, res: d}
            }), Dimension: jq(function (b) {
                return {
                    name: b.getAttribute("name"),
                    units: b.getAttribute("units"),
                    unitSymbol: b.getAttribute("unitSymbol"),
                    "default": b.getAttribute("default"),
                    multipleValues: xq(b.getAttribute("multipleValues")),
                    nearestValue: xq(b.getAttribute("nearestValue")),
                    current: xq(b.getAttribute("current")),
                    values: U(b)
                }
            }), Attribution: P(function (b, c) {
                return T({}, wv, b, c)
            }), AuthorityURL: jq(function (b, c) {
                var d = fv(b, c);
                if (m(d)) return d.name = b.getAttribute("name"), d
            }), Identifier: jq(U), MetadataURL: jq(function (b, c) {
                var d = fv(b, c);
                if (m(d)) return d.type = b.getAttribute("type"),
                    d
            }), DataURL: jq(fv), FeatureListURL: jq(fv), Style: jq(function (b, c) {
                return T({}, xv, b, c)
            }), MinScaleDenominator: P(zq), MaxScaleDenominator: P(zq), Layer: jq(function (b, c) {
                var d = c[c.length - 1], e = T({}, rv, b, c);
                if (m(e)) {
                    var f = xq(b.getAttribute("queryable"));
                    m(f) || (f = d.queryable);
                    e.queryable = m(f) ? f : !1;
                    f = Cq(b.getAttribute("cascaded"));
                    m(f) || (f = d.cascaded);
                    e.cascaded = f;
                    f = xq(b.getAttribute("opaque"));
                    m(f) || (f = d.opaque);
                    e.opaque = m(f) ? f : !1;
                    f = xq(b.getAttribute("noSubsets"));
                    m(f) || (f = d.noSubsets);
                    e.noSubsets = m(f) ? f : !1;
                    f = Aq(b.getAttribute("fixedWidth"));
                    m(f) || (f = d.fixedWidth);
                    e.fixedWidth = f;
                    f = Aq(b.getAttribute("fixedHeight"));
                    m(f) || (f = d.fixedHeight);
                    e.fixedHeight = f;
                    Pa(["Style", "CRS", "AuthorityURL"], function (b) {
                        var c = d[b];
                        if (m(c)) {
                            var f = zb(e, b), f = f.concat(c);
                            e[b] = f
                        }
                    });
                    Pa("EX_GeographicBoundingBox BoundingBox Dimension Attribution MinScaleDenominator MaxScaleDenominator".split(" "), function (b) {
                        m(e[b]) || (e[b] = d[b])
                    });
                    return e
                }
            })
        }), wv = R(mv, {Title: P(U), OnlineResource: P(Wt), LogoURL: P(jv)}), vv = R(mv, {
            westBoundLongitude: P(zq),
            eastBoundLongitude: P(zq), southBoundLatitude: P(zq), northBoundLatitude: P(zq)
        }), pv = R(mv, {GetCapabilities: P(hv), GetMap: P(hv), GetFeatureInfo: P(hv)}), iv = R(mv, {
            Format: jq(U), DCPType: jq(function (b, c) {
                return T({}, yv, b, c)
            })
        }), yv = R(mv, {
            HTTP: P(function (b, c) {
                return T({}, zv, b, c)
            })
        }), zv = R(mv, {Get: P(fv), Post: P(fv)}),
        xv = R(mv, {Name: P(U), Title: P(U), Abstract: P(U), LegendURL: jq(jv), StyleSheetURL: P(fv), StyleURL: P(fv)}),
        gv = R(mv, {Format: P(U), OnlineResource: P(Wt)}), lv = R(mv, {Keyword: hq(U)});

    function Av() {
        this.b = "http://mapserver.gis.umn.edu/mapserver";
        this.d = new Qq;
        this.defaultDataProjection = null
    }

    v(Av, sq);

    function Bv(b, c, d) {
        c.namespaceURI = b.b;
        var e = Qp(c), f = [];
        if (0 === c.childNodes.length) return f;
        "msGMLOutput" == e && Pa(c.childNodes, function (b) {
            if (1 === b.nodeType) {
                var c = d[0], e = b.localName, n = RegExp, p;
                p = "_layer".replace(/([-()\[\]{}+?*.$\^|,:#<!\\])/g, "\\$1").replace(/\x08/g, "\\x08");
                n = new n(p, "");
                e = e.replace(n, "") + "_feature";
                c.featureType = e;
                c.featureNS = this.b;
                n = {};
                n[e] = hq(this.d.Ge, this.d);
                c = R([c.featureNS, null], n);
                b.namespaceURI = this.b;
                b = T([], c, b, d, this.d);
                m(b) && $a(f, b)
            }
        }, b);
        "FeatureCollection" == e && (b = T([],
            b.d.d, c, [{}], b.d), m(b) && (f = b));
        return f
    }

    Av.prototype.Ob = function (b, c) {
        var d = {featureType: this.featureType, featureNS: this.featureNS};
        m(c) && Cb(d, sp(this, b, c));
        return Bv(this, b, [d])
    };

    function Cv() {
        this.c = new Yt
    }

    v(Cv, Xt);
    Cv.prototype.d = function (b) {
        for (b = b.firstChild; null !== b; b = b.nextSibling) if (1 == b.nodeType) return this.a(b);
        return null
    };
    Cv.prototype.a = function (b) {
        this.version = Aa(b.getAttribute("version"));
        var c = this.c.a(b);
        if (!m(c)) return null;
        c.version = this.version;
        c = T(c, Dv, b, []);
        return m(c) ? c : null
    };

    function Ev(b) {
        var c = U(b).split(" ");
        if (m(c) && 2 == c.length) return b = +c[0], c = +c[1], isNaN(b) || isNaN(c) ? void 0 : [b, c]
    }

    var Fv = [null, "http://www.opengis.net/wmts/1.0"], Gv = [null, "http://www.opengis.net/ows/1.1"], Dv = R(Fv, {
            Contents: P(function (b, c) {
                return T({}, Hv, b, c)
            })
        }), Hv = R(Fv, {
            Layer: jq(function (b, c) {
                return T({}, Iv, b, c)
            }), TileMatrixSet: jq(function (b, c) {
                return T({}, Jv, b, c)
            })
        }), Iv = R(Fv, {
            Style: jq(function (b, c) {
                var d = T({}, Kv, b, c);
                if (m(d)) {
                    var e = "true" === b.getAttribute("isDefault");
                    d.isDefault = e;
                    return d
                }
            }), Format: jq(U), TileMatrixSetLink: jq(function (b, c) {
                return T({}, Lv, b, c)
            }), ResourceURL: jq(function (b) {
                var c = b.getAttribute("format"),
                    d = b.getAttribute("template");
                b = b.getAttribute("resourceType");
                var e = {};
                m(c) && (e.format = c);
                m(d) && (e.template = d);
                m(b) && (e.resourceType = b);
                return e
            })
        }, R(Gv, {
            Title: P(U), Abstract: P(U), WGS84BoundingBox: P(function (b, c) {
                var d = T([], Mv, b, c);
                return 2 != d.length ? void 0 : Rd(d)
            }), Identifier: P(U)
        })), Kv = R(Fv, {
            LegendURL: jq(function (b) {
                var c = {};
                c.format = b.getAttribute("format");
                c.href = Wt(b);
                return c
            })
        }, R(Gv, {Title: P(U), Identifier: P(U)})), Lv = R(Fv, {TileMatrixSet: P(U)}),
        Mv = R(Gv, {LowerCorner: hq(Ev), UpperCorner: hq(Ev)}),
        Jv = R(Fv, {
            WellKnownScaleSet: P(U), TileMatrix: jq(function (b, c) {
                return T({}, Nv, b, c)
            })
        }, R(Gv, {SupportedCRS: P(U), Identifier: P(U)})), Nv = R(Fv, {
            TopLeftCorner: P(Ev),
            ScaleDenominator: P(zq),
            TileWidth: P(Bq),
            TileHeight: P(Bq),
            MatrixWidth: P(Bq),
            MatrixHeight: P(Bq)
        }, R(Gv, {Identifier: P(U)}));
    var Ov = new ue(6378137);

    function V(b) {
        qd.call(this);
        b = m(b) ? b : {};
        this.a = null;
        this.f = Te;
        this.c = void 0;
        w(this, ud("projection"), this.qj, !1, this);
        w(this, ud("tracking"), this.rj, !1, this);
        m(b.projection) && this.j(Ae(b.projection));
        m(b.trackingOptions) && this.q(b.trackingOptions);
        this.b(m(b.tracking) ? b.tracking : !1)
    }

    v(V, qd);
    l = V.prototype;
    l.P = function () {
        this.b(!1);
        V.T.P.call(this)
    };
    l.qj = function () {
        var b = this.g();
        null != b && (this.f = ze(Ae("EPSG:4326"), b), null === this.a || this.set("position", this.f(this.a)))
    };
    l.rj = function () {
        if (ag) {
            var b = this.i();
            b && !m(this.c) ? this.c = ba.navigator.geolocation.watchPosition(ra(this.ul, this), ra(this.vl, this), this.e()) : !b && m(this.c) && (ba.navigator.geolocation.clearWatch(this.c), this.c = void 0)
        }
    };
    l.ul = function (b) {
        b = b.coords;
        this.set("accuracy", b.accuracy);
        this.set("altitude", null === b.altitude ? void 0 : b.altitude);
        this.set("altitudeAccuracy", null === b.altitudeAccuracy ? void 0 : b.altitudeAccuracy);
        this.set("heading", null === b.heading ? void 0 : Yb(b.heading));
        null === this.a ? this.a = [b.longitude, b.latitude] : (this.a[0] = b.longitude, this.a[1] = b.latitude);
        var c = this.f(this.a);
        this.set("position", c);
        this.set("speed", null === b.speed ? void 0 : b.speed);
        b = Yk(Ov, this.a, b.accuracy);
        b.qa(this.f);
        this.set("accuracyGeometry",
            b);
        this.l()
    };
    l.vl = function (b) {
        b.type = "error";
        this.b(!1);
        this.dispatchEvent(b)
    };
    l.mf = function () {
        return this.get("accuracy")
    };
    V.prototype.getAccuracy = V.prototype.mf;
    V.prototype.o = function () {
        return this.get("accuracyGeometry") || null
    };
    V.prototype.getAccuracyGeometry = V.prototype.o;
    V.prototype.p = function () {
        return this.get("altitude")
    };
    V.prototype.getAltitude = V.prototype.p;
    V.prototype.r = function () {
        return this.get("altitudeAccuracy")
    };
    V.prototype.getAltitudeAccuracy = V.prototype.r;
    V.prototype.H = function () {
        return this.get("heading")
    };
    V.prototype.getHeading = V.prototype.H;
    V.prototype.N = function () {
        return this.get("position")
    };
    V.prototype.getPosition = V.prototype.N;
    V.prototype.g = function () {
        return this.get("projection")
    };
    V.prototype.getProjection = V.prototype.g;
    V.prototype.D = function () {
        return this.get("speed")
    };
    V.prototype.getSpeed = V.prototype.D;
    V.prototype.i = function () {
        return this.get("tracking")
    };
    V.prototype.getTracking = V.prototype.i;
    V.prototype.e = function () {
        return this.get("trackingOptions")
    };
    V.prototype.getTrackingOptions = V.prototype.e;
    V.prototype.j = function (b) {
        this.set("projection", b)
    };
    V.prototype.setProjection = V.prototype.j;
    V.prototype.b = function (b) {
        this.set("tracking", b)
    };
    V.prototype.setTracking = V.prototype.b;
    V.prototype.q = function (b) {
        this.set("trackingOptions", b)
    };
    V.prototype.setTrackingOptions = V.prototype.q;

    function Pv(b, c, d) {
        for (var e = [], f = b(0), g = b(1), h = c(f), k = c(g), n = [g, f], p = [k, h], q = [1, 0], r = {}, s = 1E5, u, z, y, A, D; 0 < --s && 0 < q.length;) y = q.pop(), f = n.pop(), h = p.pop(), g = y.toString(), g in r || (e.push(h[0], h[1]), r[g] = !0), A = q.pop(), g = n.pop(), k = p.pop(), D = (y + A) / 2, u = b(D), z = c(u), tk(z[0], z[1], h[0], h[1], k[0], k[1]) < d ? (e.push(k[0], k[1]), g = A.toString(), r[g] = !0) : (q.push(A, D, D, y), p.push(k, z, z, h), n.push(g, u, u, f));
        return e
    }

    function Qv(b, c, d, e, f) {
        var g = Ae("EPSG:4326");
        return Pv(function (e) {
            return [b, c + (d - c) * e]
        }, Se(g, e), f)
    }

    function Rv(b, c, d, e, f) {
        var g = Ae("EPSG:4326");
        return Pv(function (e) {
            return [c + (d - c) * e, b]
        }, Se(g, e), f)
    };

    function Sv(b) {
        b = m(b) ? b : {};
        this.n = this.g = null;
        this.c = this.b = Infinity;
        this.e = this.f = -Infinity;
        this.r = m(b.targetSize) ? b.targetSize : 100;
        this.o = m(b.maxLines) ? b.maxLines : 100;
        this.a = [];
        this.d = [];
        this.p = m(b.strokeStyle) ? b.strokeStyle : Tv;
        this.q = this.i = void 0;
        this.j = null;
        this.setMap(m(b.map) ? b.map : null)
    }

    var Tv = new kl({color: "rgba(0,0,0,0.2)"}),
        Uv = [90, 45, 30, 20, 10, 5, 2, 1, .5, .2, .1, .05, .01, .005, .002, .001];

    function Vv(b, c, d, e, f) {
        var g = f;
        c = Qv(c, b.f, b.b, b.n, d);
        g = m(b.a[g]) ? b.a[g] : new Om(null);
        Pm(g, "XY", c);
        pe(g.J(), e) && (b.a[f++] = g);
        return f
    }

    function Wv(b, c, d, e, f) {
        var g = f;
        c = Rv(c, b.e, b.c, b.n, d);
        g = m(b.d[g]) ? b.d[g] : new Om(null);
        Pm(g, "XY", c);
        pe(g.J(), e) && (b.d[f++] = g);
        return f
    }

    l = Sv.prototype;
    l.sj = function () {
        return this.g
    };
    l.Rh = function () {
        return this.a
    };
    l.Wh = function () {
        return this.d
    };
    l.wf = function (b) {
        var c = b.vectorContext, d = b.frameState;
        b = d.extent;
        var e = d.viewState, f = e.center, g = e.projection, e = e.resolution, d = d.pixelRatio,
            d = e * e / (4 * d * d);
        if (null === this.n || !Re(this.n, g)) {
            var h = g.J(), k = g.c, n = k[2], p = k[1], q = k[0];
            this.b = k[3];
            this.c = n;
            this.f = p;
            this.e = q;
            k = Ae("EPSG:4326");
            this.i = Se(k, g);
            this.q = Se(g, k);
            this.j = this.q(ke(h));
            this.n = g
        }
        for (var g = this.j[0], h = this.j[1], k = -1, r, p = Math.pow(this.r * e, 2), q = [], s = [], e = 0, n = Uv.length; e < n; ++e) {
            r = Uv[e] / 2;
            q[0] = g - r;
            q[1] = h - r;
            s[0] = g + r;
            s[1] = h + r;
            this.i(q, q);
            this.i(s,
                s);
            r = Math.pow(s[0] - q[0], 2) + Math.pow(s[1] - q[1], 2);
            if (r <= p) break;
            k = Uv[e]
        }
        e = k;
        if (-1 == e) this.a.length = this.d.length = 0; else {
            g = this.q(f);
            f = g[0];
            g = g[1];
            h = this.o;
            f = Math.floor(f / e) * e;
            p = Vb(f, this.e, this.c);
            n = Vv(this, p, d, b, 0);
            for (k = 0; p != this.e && k++ < h;) p = Math.max(p - e, this.e), n = Vv(this, p, d, b, n);
            p = Vb(f, this.e, this.c);
            for (k = 0; p != this.c && k++ < h;) p = Math.min(p + e, this.c), n = Vv(this, p, d, b, n);
            this.a.length = n;
            g = Math.floor(g / e) * e;
            f = Vb(g, this.f, this.b);
            n = Wv(this, f, d, b, 0);
            for (k = 0; f != this.f && k++ < h;) f = Math.max(f - e, this.f), n =
                Wv(this, f, d, b, n);
            f = Vb(g, this.f, this.b);
            for (k = 0; f != this.b && k++ < h;) f = Math.min(f + e, this.b), n = Wv(this, f, d, b, n);
            this.d.length = n
        }
        c.Ba(null, this.p);
        b = 0;
        for (d = this.a.length; b < d; ++b) f = this.a[b], c.Eb(f, null);
        b = 0;
        for (d = this.d.length; b < d; ++b) f = this.d[b], c.Eb(f, null)
    };
    l.setMap = function (b) {
        null !== this.g && (this.g.t("postcompose", this.wf, this), this.g.render());
        null !== b && (b.s("postcompose", this.wf, this), b.render());
        this.g = b
    };

    function Xv(b, c, d, e, f, g, h) {
        Oi.call(this, b, c, d, 0, e);
        this.n = f;
        this.d = new Image;
        null !== g && (this.d.crossOrigin = g);
        this.c = {};
        this.b = null;
        this.state = 0;
        this.g = h
    }

    v(Xv, Oi);
    Xv.prototype.a = function (b) {
        if (m(b)) {
            var c = ma(b);
            if (c in this.c) return this.c[c];
            b = vb(this.c) ? this.d : this.d.cloneNode(!1);
            return this.c[c] = b
        }
        return this.d
    };
    Xv.prototype.i = function () {
        this.state = 3;
        Pa(this.b, Wc);
        this.b = null;
        this.dispatchEvent("change")
    };
    Xv.prototype.j = function () {
        m(this.resolution) || (this.resolution = ne(this.extent) / this.d.height);
        this.state = 2;
        Pa(this.b, Wc);
        this.b = null;
        this.dispatchEvent("change")
    };
    Xv.prototype.load = function () {
        0 == this.state && (this.state = 1, this.b = [Uc(this.d, "error", this.i, !1, this), Uc(this.d, "load", this.j, !1, this)], this.g(this, this.n))
    };

    function Yv(b, c, d, e, f) {
        Pi.call(this, b, c);
        this.g = d;
        this.d = new Image;
        null !== e && (this.d.crossOrigin = e);
        this.b = {};
        this.f = null;
        this.n = f
    }

    v(Yv, Pi);
    l = Yv.prototype;
    l.P = function () {
        Zv(this);
        Yv.T.P.call(this)
    };
    l.Ta = function (b) {
        if (m(b)) {
            var c = ma(b);
            if (c in this.b) return this.b[c];
            b = vb(this.b) ? this.d : this.d.cloneNode(!1);
            return this.b[c] = b
        }
        return this.d
    };
    l.qb = function () {
        return this.g
    };
    l.tj = function () {
        this.state = 3;
        Zv(this);
        Qi(this)
    };
    l.uj = function () {
        this.state = this.d.naturalWidth && this.d.naturalHeight ? 2 : 4;
        Zv(this);
        Qi(this)
    };
    l.load = function () {
        0 == this.state && (this.state = 1, Qi(this), this.f = [Uc(this.d, "error", this.tj, !1, this), Uc(this.d, "load", this.uj, !1, this)], this.n(this, this.g))
    };

    function Zv(b) {
        Pa(b.f, Wc);
        b.f = null
    };

    function $v(b, c, d) {
        return function (e, f, g) {
            return d(b, c, e, f, g)
        }
    }

    function aw() {
    };

    function bw(b, c) {
        hd.call(this);
        this.a = new Xo(this);
        var d = b;
        c && (d = vf(b));
        this.a.Ra(d, "dragenter", this.dl);
        d != b && this.a.Ra(d, "dragover", this.el);
        this.a.Ra(b, "dragover", this.fl);
        this.a.Ra(b, "drop", this.gl)
    }

    v(bw, hd);
    l = bw.prototype;
    l.Kc = !1;
    l.P = function () {
        bw.T.P.call(this);
        this.a.Jc()
    };
    l.dl = function (b) {
        var c = b.a.dataTransfer;
        (this.Kc = !(!c || !(c.types && (Va(c.types, "Files") || Va(c.types, "public.file-url")) || c.files && 0 < c.files.length))) && b.preventDefault()
    };
    l.el = function (b) {
        this.Kc && (b.preventDefault(), b.a.dataTransfer.dropEffect = "none")
    };
    l.fl = function (b) {
        this.Kc && (b.preventDefault(), b.pb(), b = b.a.dataTransfer, b.effectAllowed = "all", b.dropEffect = "copy")
    };
    l.gl = function (b) {
        this.Kc && (b.preventDefault(), b.pb(), b = new wc(b.a), b.type = "drop", this.dispatchEvent(b))
    };

    function cw(b) {
        b.prototype.then = b.prototype.then;
        b.prototype.$goog_Thenable = !0
    }

    function dw(b) {
        if (!b) return !1;
        try {
            return !!b.$goog_Thenable
        } catch (c) {
            return !1
        }
    };

    function ew(b, c) {
        fw || gw();
        hw || (fw(), hw = !0);
        iw.push(new jw(b, c))
    }

    var fw;

    function gw() {
        if (ba.Promise && ba.Promise.resolve) {
            var b = ba.Promise.resolve();
            fw = function () {
                b.then(kw)
            }
        } else fw = function () {
            oh(kw)
        }
    }

    var hw = !1, iw = [];

    function kw() {
        for (; iw.length;) {
            var b = iw;
            iw = [];
            for (var c = 0; c < b.length; c++) {
                var d = b[c];
                try {
                    d.a.call(d.d)
                } catch (e) {
                    nh(e)
                }
            }
        }
        hw = !1
    }

    function jw(b, c) {
        this.a = b;
        this.d = c
    };

    function lw(b, c) {
        this.d = mw;
        this.e = void 0;
        this.a = this.b = null;
        this.c = this.f = !1;
        try {
            var d = this;
            b.call(c, function (b) {
                nw(d, ow, b)
            }, function (b) {
                nw(d, pw, b)
            })
        } catch (e) {
            nw(this, pw, e)
        }
    }

    var mw = 0, ow = 2, pw = 3;
    lw.prototype.then = function (b, c, d) {
        return qw(this, ka(b) ? b : null, ka(c) ? c : null, d)
    };
    cw(lw);
    lw.prototype.cancel = function (b) {
        this.d == mw && ew(function () {
            var c = new rw(b);
            sw(this, c)
        }, this)
    };

    function sw(b, c) {
        if (b.d == mw) if (b.b) {
            var d = b.b;
            if (d.a) {
                for (var e = 0, f = -1, g = 0, h; h = d.a[g]; g++) if (h = h.Cc) if (e++, h == b && (f = g), 0 <= f && 1 < e) break;
                0 <= f && (d.d == mw && 1 == e ? sw(d, c) : (e = d.a.splice(f, 1)[0], tw(d, e, pw, c)))
            }
        } else nw(b, pw, c)
    }

    function uw(b, c) {
        b.a && b.a.length || b.d != ow && b.d != pw || vw(b);
        b.a || (b.a = []);
        b.a.push(c)
    }

    function qw(b, c, d, e) {
        var f = {Cc: null, Vf: null, Xf: null};
        f.Cc = new lw(function (b, h) {
            f.Vf = c ? function (d) {
                try {
                    var f = c.call(e, d);
                    b(f)
                } catch (p) {
                    h(p)
                }
            } : b;
            f.Xf = d ? function (c) {
                try {
                    var f = d.call(e, c);
                    !m(f) && c instanceof rw ? h(c) : b(f)
                } catch (p) {
                    h(p)
                }
            } : h
        });
        f.Cc.b = b;
        uw(b, f);
        return f.Cc
    }

    lw.prototype.g = function (b) {
        this.d = mw;
        nw(this, ow, b)
    };
    lw.prototype.n = function (b) {
        this.d = mw;
        nw(this, pw, b)
    };

    function nw(b, c, d) {
        if (b.d == mw) {
            if (b == d) c = pw, d = new TypeError("Promise cannot resolve to itself"); else {
                if (dw(d)) {
                    b.d = 1;
                    d.then(b.g, b.n, b);
                    return
                }
                if (la(d)) try {
                    var e = d.then;
                    if (ka(e)) {
                        ww(b, d, e);
                        return
                    }
                } catch (f) {
                    c = pw, d = f
                }
            }
            b.e = d;
            b.d = c;
            vw(b);
            c != pw || d instanceof rw || xw(b, d)
        }
    }

    function ww(b, c, d) {
        function e(c) {
            g || (g = !0, b.n(c))
        }

        function f(c) {
            g || (g = !0, b.g(c))
        }

        b.d = 1;
        var g = !1;
        try {
            d.call(c, f, e)
        } catch (h) {
            e(h)
        }
    }

    function vw(b) {
        b.f || (b.f = !0, ew(b.i, b))
    }

    lw.prototype.i = function () {
        for (; this.a && this.a.length;) {
            var b = this.a;
            this.a = [];
            for (var c = 0; c < b.length; c++) tw(this, b[c], this.d, this.e)
        }
        this.f = !1
    };

    function tw(b, c, d, e) {
        if (d == ow) c.Vf(e); else {
            if (c.Cc) for (; b && b.c; b = b.b) b.c = !1;
            c.Xf(e)
        }
    }

    function xw(b, c) {
        b.c = !0;
        ew(function () {
            b.c && yw.call(null, c)
        })
    }

    var yw = nh;

    function rw(b) {
        wa.call(this, b)
    }

    v(rw, wa);
    rw.prototype.name = "cancel";/*
 Portions of this code are from MochiKit, received by
 The Closure Authors under the MIT license. All other code is Copyright
 2005-2009 The Closure Authors. All Rights Reserved.
*/
    function zw(b, c) {
        this.f = [];
        this.o = b;
        this.q = c || null;
        this.c = this.a = !1;
        this.b = void 0;
        this.i = this.p = this.g = !1;
        this.e = 0;
        this.d = null;
        this.n = 0
    }

    zw.prototype.cancel = function (b) {
        if (this.a) this.b instanceof zw && this.b.cancel(); else {
            if (this.d) {
                var c = this.d;
                delete this.d;
                b ? c.cancel(b) : (c.n--, 0 >= c.n && c.cancel())
            }
            this.o ? this.o.call(this.q, this) : this.i = !0;
            this.a || (b = new Aw, Bw(this), Cw(this, !1, b))
        }
    };
    zw.prototype.j = function (b, c) {
        this.g = !1;
        Cw(this, b, c)
    };

    function Cw(b, c, d) {
        b.a = !0;
        b.b = d;
        b.c = !c;
        Dw(b)
    }

    function Bw(b) {
        if (b.a) {
            if (!b.i) throw new Ew;
            b.i = !1
        }
    }

    function Fw(b, c, d, e) {
        b.f.push([c, d, e]);
        b.a && Dw(b)
    }

    zw.prototype.then = function (b, c, d) {
        var e, f, g = new lw(function (b, c) {
            e = b;
            f = c
        });
        Fw(this, e, function (b) {
            b instanceof Aw ? g.cancel() : f(b)
        });
        return g.then(b, c, d)
    };
    cw(zw);

    function Gw(b) {
        return Sa(b.f, function (b) {
            return ka(b[1])
        })
    }

    function Dw(b) {
        if (b.e && b.a && Gw(b)) {
            var c = b.e, d = Hw[c];
            d && (ba.clearTimeout(d.aa), delete Hw[c]);
            b.e = 0
        }
        b.d && (b.d.n--, delete b.d);
        for (var c = b.b, e = d = !1; b.f.length && !b.g;) {
            var f = b.f.shift(), g = f[0], h = f[1], f = f[2];
            if (g = b.c ? h : g) try {
                var k = g.call(f || b.q, c);
                m(k) && (b.c = b.c && (k == c || k instanceof Error), b.b = c = k);
                dw(c) && (e = !0, b.g = !0)
            } catch (n) {
                c = n, b.c = !0, Gw(b) || (d = !0)
            }
        }
        b.b = c;
        e && (k = ra(b.j, b, !0), e = ra(b.j, b, !1), c instanceof zw ? (Fw(c, k, e), c.p = !0) : c.then(k, e));
        d && (c = new Iw(c), Hw[c.aa] = c, b.e = c.aa)
    }

    function Ew() {
        wa.call(this)
    }

    v(Ew, wa);
    Ew.prototype.message = "Deferred has already fired";
    Ew.prototype.name = "AlreadyCalledError";

    function Aw() {
        wa.call(this)
    }

    v(Aw, wa);
    Aw.prototype.message = "Deferred was canceled";
    Aw.prototype.name = "CanceledError";

    function Iw(b) {
        this.aa = ba.setTimeout(ra(this.d, this), 0);
        this.a = b
    }

    Iw.prototype.d = function () {
        delete Hw[this.aa];
        throw this.a;
    };
    var Hw = {};

    function Jw(b, c) {
        m(b.name) ? (this.name = b.name, this.code = ub[b.name]) : (this.code = b.code, this.name = Kw(b.code));
        wa.call(this, za("%s %s", this.name, c))
    }

    v(Jw, wa);

    function Kw(b) {
        var c = tb(function (c) {
            return b == c
        });
        if (!m(c)) throw Error("Invalid code: " + b);
        return c
    }

    var ub = {
        AbortError: 3,
        EncodingError: 5,
        InvalidModificationError: 9,
        InvalidStateError: 7,
        NotFoundError: 1,
        NotReadableError: 4,
        NoModificationAllowedError: 6,
        PathExistsError: 12,
        QuotaExceededError: 10,
        SecurityError: 2,
        SyntaxError: 8,
        TypeMismatchError: 11
    };

    function Lw(b, c) {
        qc.call(this, b.type, c)
    }

    v(Lw, qc);

    function Mw() {
        hd.call(this);
        this.hb = new FileReader;
        this.hb.onloadstart = ra(this.a, this);
        this.hb.onprogress = ra(this.a, this);
        this.hb.onload = ra(this.a, this);
        this.hb.onabort = ra(this.a, this);
        this.hb.onerror = ra(this.a, this);
        this.hb.onloadend = ra(this.a, this)
    }

    v(Mw, hd);
    Mw.prototype.getError = function () {
        return this.hb.error && new Jw(this.hb.error, "reading file")
    };
    Mw.prototype.a = function (b) {
        this.dispatchEvent(new Lw(b, this))
    };
    Mw.prototype.P = function () {
        Mw.T.P.call(this);
        delete this.hb
    };

    function Nw(b) {
        var c = new zw;
        b.Ra("loadend", sa(function (b, c) {
            var f = c.hb.result, g = c.getError();
            null == f || g ? (Bw(b), Cw(b, !1, g)) : (Bw(b), Cw(b, !0, f));
            c.Jc()
        }, c, b));
        return c
    };

    function Ow(b) {
        b = m(b) ? b : {};
        Nj.call(this, {handleEvent: bd});
        this.e = m(b.formatConstructors) ? b.formatConstructors : [];
        this.q = m(b.projection) ? Ae(b.projection) : null;
        this.f = null;
        this.c = void 0
    }

    v(Ow, Nj);
    Ow.prototype.P = function () {
        m(this.c) && Wc(this.c);
        Ow.T.P.call(this)
    };
    Ow.prototype.g = function (b) {
        b = b.a.dataTransfer.files;
        var c, d, e;
        c = 0;
        for (d = b.length; c < d; ++c) {
            var f = e = b[c], g = new Mw, h = Nw(g);
            g.hb.readAsText(f, "");
            Fw(h, sa(this.i, e), null, this)
        }
    };
    Ow.prototype.i = function (b, c) {
        var d = this.j, e = this.q;
        null === e && (e = d.a().p);
        var d = this.e, f = [], g, h;
        g = 0;
        for (h = d.length; g < h; ++g) {
            var k = new d[g], n;
            try {
                n = k.ma(c)
            } catch (p) {
                n = null
            }
            if (null !== n) {
                var k = k.Ja(c), k = Se(k, e), q, r;
                q = 0;
                for (r = n.length; q < r; ++q) {
                    var s = n[q], u = s.R();
                    null != u && u.qa(k);
                    f.push(s)
                }
            }
        }
        this.dispatchEvent(new Pw(Qw, this, b, f, e))
    };
    Ow.prototype.setMap = function (b) {
        m(this.c) && (Wc(this.c), this.c = void 0);
        null !== this.f && (pc(this.f), this.f = null);
        Ow.T.setMap.call(this, b);
        null !== b && (this.f = new bw(b.b), this.c = w(this.f, "drop", this.g, !1, this))
    };
    var Qw = "addfeatures";

    function Pw(b, c, d, e, f) {
        qc.call(this, b, c);
        this.features = e;
        this.file = d;
        this.projection = f
    }

    v(Pw, qc);

    function Rw(b, c) {
        this.x = b;
        this.y = c
    }

    v(Rw, rf);
    Rw.prototype.clone = function () {
        return new Rw(this.x, this.y)
    };
    Rw.prototype.scale = rf.prototype.scale;
    Rw.prototype.add = function (b) {
        this.x += b.x;
        this.y += b.y;
        return this
    };
    Rw.prototype.rotate = function (b) {
        var c = Math.cos(b);
        b = Math.sin(b);
        var d = this.y * c + this.x * b;
        this.x = this.x * c - this.y * b;
        this.y = d;
        return this
    };

    function Sw(b) {
        b = m(b) ? b : {};
        ak.call(this, {handleDownEvent: Tw, handleDragEvent: Uw, handleUpEvent: Vw});
        this.i = m(b.condition) ? b.condition : Xj;
        this.c = this.e = void 0;
        this.g = 0
    }

    v(Sw, ak);

    function Uw(b) {
        if (Zj(b)) {
            var c = b.map, d = c.f();
            b = b.pixel;
            b = new Rw(b[0] - d[0] / 2, d[1] / 2 - b[1]);
            d = Math.atan2(b.y, b.x);
            b = Math.sqrt(b.x * b.x + b.y * b.y);
            var e = c.a(), f = Xe(e);
            c.render();
            m(this.e) && Oj(c, e, f.rotation - (d - this.e));
            this.e = d;
            m(this.c) && Qj(c, e, f.resolution / b * this.c);
            m(this.c) && (this.g = this.c / b);
            this.c = b
        }
    }

    function Vw(b) {
        if (!Zj(b)) return !0;
        b = b.map;
        var c = b.a();
        Ze(c, -1);
        var d = Xe(c), e = this.g - 1, f = d.rotation, f = c.constrainRotation(f, 0);
        Oj(b, c, f, void 0, void 0);
        d = d.resolution;
        d = c.constrainResolution(d, 0, e);
        Qj(b, c, d, void 0, 400);
        this.g = 0;
        return !1
    }

    function Tw(b) {
        return Zj(b) && this.i(b) ? (Ze(b.map.a(), 1), this.c = this.e = void 0, !0) : !1
    };

    function Ww(b, c) {
        qc.call(this, b);
        this.feature = c
    }

    v(Ww, qc);

    function Xw(b) {
        ak.call(this, {handleDownEvent: Yw, handleEvent: Zw, handleUpEvent: $w});
        this.S = null;
        this.fa = m(b.source) ? b.source : null;
        this.ca = m(b.features) ? b.features : null;
        this.kb = m(b.snapTolerance) ? b.snapTolerance : 12;
        this.Fa = m(b.minPointsPerRing) ? b.minPointsPerRing : 3;
        var c = this.H = b.type, d;
        if ("Point" === c || "MultiPoint" === c) d = ax; else if ("LineString" === c || "MultiLineString" === c) d = bx; else if ("Polygon" === c || "MultiPolygon" === c) d = cx;
        this.c = d;
        this.e = this.r = this.q = this.g = this.i = null;
        this.N = new pp({
            style: m(b.style) ?
                b.style : dx()
        });
        this.da = b.geometryName;
        this.Xa = m(b.condition) ? b.condition : Wj;
        w(this, ud("active"), this.ea, !1, this)
    }

    v(Xw, ak);

    function dx() {
        var b = vl();
        return function (c) {
            return b[c.R().O()]
        }
    }

    Xw.prototype.setMap = function (b) {
        Xw.T.setMap.call(this, b);
        this.ea()
    };

    function Zw(b) {
        var c;
        c = b.map;
        if (Kf(document, c.b) && "none" != c.b.style.display) {
            var d = c.f();
            null == d || 0 >= d[0] || 0 >= d[1] ? c = !1 : (c = c.a(), c = null !== c && Ye(c) ? !0 : !1)
        } else c = !1;
        if (!c) return !0;
        c = !0;
        b.type === Hi ? c = ex(this, b) : b.type === Bi && (c = !1);
        return bk.call(this, b) && c
    }

    function Yw(b) {
        return this.Xa(b) ? (this.S = b.pixel, !0) : !1
    }

    function $w(b) {
        var c = this.S, d = b.pixel, e = c[0] - d[0], c = c[1] - d[1], d = !0;
        4 >= e * e + c * c && (ex(this, b), null === this.i ? fx(this, b) : this.c === ax || gx(this, b) ? this.W() : (b = b.coordinate, e = this.g.R(), this.c === bx ? (this.i = b.slice(), c = e.Q(), c.push(b.slice()), e.V(c)) : this.c === cx && (this.e[0].push(b.slice()), e.V(this.e)), hx(this)), d = !1);
        return d
    }

    function ex(b, c) {
        if (b.c === ax && null === b.i) fx(b, c); else if (null === b.i) {
            var d = c.coordinate.slice();
            null === b.q ? (b.q = new N(new Jk(d)), hx(b)) : b.q.R().V(d)
        } else {
            var d = c.coordinate, e = b.g.R(), f, g;
            b.c === ax ? (g = e.Q(), g[0] = d[0], g[1] = d[1], e.V(g)) : (b.c === bx ? f = e.Q() : b.c === cx && (f = b.e[0]), gx(b, c) && (d = b.i.slice()), b.q.R().V(d), g = f[f.length - 1], g[0] = d[0], g[1] = d[1], b.c === bx ? e.V(f) : b.c === cx && (b.r.R().V(f), e.V(b.e)));
            hx(b)
        }
        return !0
    }

    function gx(b, c) {
        var d = !1;
        if (null !== b.g) {
            var e = b.g.R(), f = !1, g = [b.i];
            b.c === bx ? f = 2 < e.Q().length : b.c === cx && (f = e.Q()[0].length > b.Fa, g = [b.e[0][0], b.e[0][b.e[0].length - 2]]);
            if (f) for (var e = c.map, f = 0, h = g.length; f < h; f++) {
                var k = g[f], n = e.e(k), p = c.pixel, d = p[0] - n[0], n = p[1] - n[1];
                if (d = Math.sqrt(d * d + n * n) <= b.kb) {
                    b.i = k;
                    break
                }
            }
        }
        return d
    }

    function fx(b, c) {
        var d = c.coordinate;
        b.i = d;
        var e;
        b.c === ax ? e = new Jk(d.slice()) : b.c === bx ? e = new Om([d.slice(), d.slice()]) : b.c === cx && (b.r = new N(new Om([d.slice(), d.slice()])), b.e = [[d.slice(), d.slice()]], e = new F(b.e));
        b.g = new N;
        m(b.da) && b.g.f(b.da);
        b.g.Sa(e);
        hx(b);
        b.dispatchEvent(new Ww("drawstart", b.g))
    }

    Xw.prototype.W = function () {
        var b = ix(this), c, d = b.R();
        this.c === ax ? c = d.Q() : this.c === bx ? (c = d.Q(), c.pop(), d.V(c)) : this.c === cx && (this.e[0].pop(), this.e[0].push(this.e[0][0]), d.V(this.e), c = d.Q());
        "MultiPoint" === this.H ? b.Sa(new Tm([c])) : "MultiLineString" === this.H ? b.Sa(new Qm([c])) : "MultiPolygon" === this.H && b.Sa(new Um([c]));
        null === this.ca || this.ca.push(b);
        null === this.fa || this.fa.Va(b);
        this.dispatchEvent(new Ww("drawend", b))
    };

    function ix(b) {
        b.i = null;
        var c = b.g;
        null !== c && (b.g = null, b.q = null, b.r = null, b.N.a.clear());
        return c
    }

    Xw.prototype.p = ad;

    function hx(b) {
        var c = [];
        null === b.g || c.push(b.g);
        null === b.r || c.push(b.r);
        null === b.q || c.push(b.q);
        b.N.Sc(new lg(c))
    }

    Xw.prototype.ea = function () {
        var b = this.j, c = this.a();
        null !== b && c || ix(this);
        this.N.setMap(c ? b : null)
    };
    var ax = "Point", bx = "LineString", cx = "Polygon";

    function jx(b) {
        ak.call(this, {handleDownEvent: kx, handleDragEvent: lx, handleEvent: mx, handleUpEvent: nx});
        this.ca = m(b.deleteCondition) ? b.deleteCondition : gd(Wj, Vj);
        this.W = this.e = null;
        this.S = [0, 0];
        this.c = new gn;
        this.i = m(b.pixelTolerance) ? b.pixelTolerance : 10;
        this.N = !1;
        this.g = null;
        this.q = new pp({style: m(b.style) ? b.style : ox()});
        this.H = {
            Point: this.xm,
            LineString: this.yg,
            LinearRing: this.yg,
            Polygon: this.zm,
            MultiPoint: this.um,
            MultiLineString: this.tm,
            MultiPolygon: this.wm,
            GeometryCollection: this.sm
        };
        this.r = b.features;
        this.r.forEach(this.If, this);
        w(this.r, "add", this.qi, !1, this);
        w(this.r, "remove", this.ri, !1, this)
    }

    v(jx, ak);
    l = jx.prototype;
    l.If = function (b) {
        var c = b.R();
        m(this.H[c.O()]) && this.H[c.O()].call(this, b, c);
        b = this.j;
        null === b || px(this, this.S, b)
    };
    l.setMap = function (b) {
        this.q.setMap(b);
        jx.T.setMap.call(this, b)
    };
    l.qi = function (b) {
        this.If(b.element)
    };
    l.ri = function (b) {
        var c = b.element;
        b = this.c;
        var d, e = [];
        ln(b, c.R().J(), function (b) {
            c === b.feature && e.push(b)
        });
        for (d = e.length - 1; 0 <= d; --d) b.remove(e[d]);
        null !== this.e && 0 === this.r.Ib() && (this.q.Dd(this.e), this.e = null)
    };
    l.xm = function (b, c) {
        var d = c.Q(), d = {feature: b, geometry: c, ha: [d, d]};
        this.c.sa(c.J(), d)
    };
    l.um = function (b, c) {
        var d = c.Q(), e, f, g;
        f = 0;
        for (g = d.length; f < g; ++f) e = d[f], e = {
            feature: b,
            geometry: c,
            depth: [f],
            index: f,
            ha: [e, e]
        }, this.c.sa(c.J(), e)
    };
    l.yg = function (b, c) {
        var d = c.Q(), e, f, g, h;
        e = 0;
        for (f = d.length - 1; e < f; ++e) g = d.slice(e, e + 2), h = {
            feature: b,
            geometry: c,
            index: e,
            ha: g
        }, this.c.sa(Rd(g), h)
    };
    l.tm = function (b, c) {
        var d = c.Q(), e, f, g, h, k, n, p;
        h = 0;
        for (k = d.length; h < k; ++h) for (e = d[h], f = 0, g = e.length - 1; f < g; ++f) n = e.slice(f, f + 2), p = {
            feature: b,
            geometry: c,
            depth: [h],
            index: f,
            ha: n
        }, this.c.sa(Rd(n), p)
    };
    l.zm = function (b, c) {
        var d = c.Q(), e, f, g, h, k, n, p;
        h = 0;
        for (k = d.length; h < k; ++h) for (e = d[h], f = 0, g = e.length - 1; f < g; ++f) n = e.slice(f, f + 2), p = {
            feature: b,
            geometry: c,
            depth: [h],
            index: f,
            ha: n
        }, this.c.sa(Rd(n), p)
    };
    l.wm = function (b, c) {
        var d = c.Q(), e, f, g, h, k, n, p, q, r, s;
        n = 0;
        for (p = d.length; n < p; ++n) for (q = d[n], h = 0, k = q.length; h < k; ++h) for (e = q[h], f = 0, g = e.length - 1; f < g; ++f) r = e.slice(f, f + 2), s = {
            feature: b,
            geometry: c,
            depth: [h, n],
            index: f,
            ha: r
        }, this.c.sa(Rd(r), s)
    };
    l.sm = function (b, c) {
        var d, e = c.c;
        for (d = 0; d < e.length; ++d) this.H[e[d].O()].call(this, b, e[d])
    };

    function qx(b, c) {
        var d = b.e;
        null === d ? (d = new N(new Jk(c)), b.e = d, b.q.Df(d)) : d.R().V(c)
    }

    function kx(b) {
        px(this, b.pixel, b.map);
        this.g = [];
        var c = this.e;
        if (null !== c) {
            b = [];
            for (var c = c.R().Q(), d = Rd([c]), d = jn(this.c, d), e = 0, f = d.length; e < f; ++e) {
                var g = d[e], h = g.ha;
                zd(h[0], c) ? this.g.push([g, 0]) : zd(h[1], c) ? this.g.push([g, 1]) : ma(h) in this.W && b.push([g, c])
            }
            for (e = b.length - 1; 0 <= e; --e) this.Qi.apply(this, b[e])
        }
        return null !== this.e
    }

    function lx(b) {
        b = b.coordinate;
        for (var c = 0, d = this.g.length; c < d; ++c) {
            for (var e = this.g[c], f = e[0], g = f.depth, h = f.geometry, k = h.Q(), n = f.ha, e = e[1]; b.length < h.B;) b.push(0);
            switch (h.O()) {
                case "Point":
                    k = b;
                    n[0] = n[1] = b;
                    break;
                case "MultiPoint":
                    k[f.index] = b;
                    n[0] = n[1] = b;
                    break;
                case "LineString":
                    k[f.index + e] = b;
                    n[e] = b;
                    break;
                case "MultiLineString":
                    k[g[0]][f.index + e] = b;
                    n[e] = b;
                    break;
                case "Polygon":
                    k[g[0]][f.index + e] = b;
                    n[e] = b;
                    break;
                case "MultiPolygon":
                    k[g[1]][g[0]][f.index + e] = b, n[e] = b
            }
            h.V(k);
            qx(this, b)
        }
    }

    function nx() {
        for (var b, c = this.g.length - 1; 0 <= c; --c) b = this.g[c][0], this.c.update(Rd(b.ha), b);
        return !1
    }

    function mx(b) {
        var c;
        b.map.a().q.slice()[1] || b.type != Hi || (this.S = b.pixel, px(this, b.pixel, b.map));
        if (null !== this.e && this.N && this.ca(b)) {
            this.e.R();
            c = this.g;
            var d = {}, e = !1, f, g, h, k, n, p, q, r, s;
            for (n = c.length - 1; 0 <= n; --n) if (h = c[n], r = h[0], k = r.geometry, g = k.Q(), s = ma(r.feature), f = q = p = void 0, 0 === h[1] ? (q = r, p = r.index) : 1 == h[1] && (f = r, p = r.index + 1), s in d || (d[s] = [f, q, p]), h = d[s], m(f) && (h[0] = f), m(q) && (h[1] = q), m(h[0]) && m(h[1])) {
                f = g;
                e = !1;
                q = p - 1;
                switch (k.O()) {
                    case "MultiLineString":
                        g[r.depth[0]].splice(p, 1);
                        e = !0;
                        break;
                    case "LineString":
                        g.splice(p,
                            1);
                        e = !0;
                        break;
                    case "MultiPolygon":
                        f = f[r.depth[1]];
                    case "Polygon":
                        f = f[r.depth[0]], 4 < f.length && (p == f.length - 1 && (p = 0), f.splice(p, 1), e = !0, 0 === p && (f.pop(), f.push(f[0]), q = f.length - 1))
                }
                e && (this.c.remove(h[0]), this.c.remove(h[1]), k.V(g), g = {
                    depth: r.depth,
                    feature: r.feature,
                    geometry: r.geometry,
                    index: q,
                    ha: [h[0].ha[0], h[1].ha[1]]
                }, this.c.sa(Rd(g.ha), g), rx(this, k, p, r.depth, -1), this.q.Dd(this.e), this.e = null)
            }
            c = e
        }
        return bk.call(this, b) && !c
    }

    function px(b, c, d) {
        function e(b, c) {
            return Bd(f, wd(f, b.ha)) - Bd(f, wd(f, c.ha))
        }

        var f = d.ra(c), g = d.ra([c[0] - b.i, c[1] + b.i]), h = d.ra([c[0] + b.i, c[1] - b.i]), g = Rd([g, h]),
            g = jn(b.c, g);
        if (0 < g.length) {
            g.sort(e);
            var h = g[0].ha, k = wd(f, h), n = d.e(k);
            if (Math.sqrt(Bd(c, n)) <= b.i) {
                c = d.e(h[0]);
                d = d.e(h[1]);
                c = Bd(n, c);
                d = Bd(n, d);
                b.N = Math.sqrt(Math.min(c, d)) <= b.i;
                b.N && (k = c > d ? h[1] : h[0]);
                qx(b, k);
                d = {};
                d[ma(h)] = !0;
                c = 1;
                for (n = g.length; c < n; ++c) if (k = g[c].ha, zd(h[0], k[0]) && zd(h[1], k[1]) || zd(h[0], k[1]) && zd(h[1], k[0])) d[ma(k)] = !0; else break;
                b.W = d;
                return
            }
        }
        null !== b.e && (b.q.Dd(b.e), b.e = null)
    }

    l.Qi = function (b, c) {
        for (var d = b.ha, e = b.feature, f = b.geometry, g = b.depth, h = b.index, k; c.length < f.B;) c.push(0);
        switch (f.O()) {
            case "MultiLineString":
                k = f.Q();
                k[g[0]].splice(h + 1, 0, c);
                break;
            case "Polygon":
                k = f.Q();
                k[g[0]].splice(h + 1, 0, c);
                break;
            case "MultiPolygon":
                k = f.Q();
                k[g[1]][g[0]].splice(h + 1, 0, c);
                break;
            case "LineString":
                k = f.Q();
                k.splice(h + 1, 0, c);
                break;
            default:
                return
        }
        f.V(k);
        k = this.c;
        k.remove(b);
        rx(this, f, h, g, 1);
        var n = {ha: [d[0], c], feature: e, geometry: f, depth: g, index: h};
        k.sa(Rd(n.ha), n);
        this.g.push([n, 1]);
        d =
            {ha: [c, d[1]], feature: e, geometry: f, depth: g, index: h + 1};
        k.sa(Rd(d.ha), d);
        this.g.push([d, 0])
    };

    function rx(b, c, d, e, f) {
        ln(b.c, c.J(), function (b) {
            b.geometry === c && (!m(e) || eb(b.depth, e)) && b.index > d && (b.index += f)
        })
    }

    function ox() {
        var b = vl();
        return function () {
            return b.Point
        }
    };

    function sx(b, c, d) {
        qc.call(this, b);
        this.d = c;
        this.a = d
    }

    v(sx, qc);

    function tx(b) {
        Nj.call(this, {handleEvent: ux});
        b = m(b) ? b : {};
        this.i = m(b.condition) ? b.condition : Vj;
        this.e = m(b.addCondition) ? b.addCondition : ad;
        this.p = m(b.removeCondition) ? b.removeCondition : ad;
        this.D = m(b.toggleCondition) ? b.toggleCondition : Xj;
        this.g = m(b.multi) ? b.multi : !1;
        var c;
        if (m(b.layers)) if (ka(b.layers)) c = b.layers; else {
            var d = b.layers;
            c = function (b) {
                return Va(d, b)
            }
        } else c = bd;
        this.f = c;
        this.c = new pp({style: m(b.style) ? b.style : vx()});
        b = this.c.a;
        w(b, "add", this.q, !1, this);
        w(b, "remove", this.r, !1, this)
    }

    v(tx, Nj);
    tx.prototype.o = function () {
        return this.c.a
    };

    function ux(b) {
        if (!this.i(b)) return !0;
        var c = this.e(b), d = this.p(b), e = this.D(b), f = b.map, g = this.c.a, h = [], k = [], n = !1;
        if (c || d || e) {
            f.oe(b.pixel, function (b) {
                -1 == Oa.indexOf.call(g.a, b, void 0) ? (c || e) && k.push(b) : (d || e) && h.push(b)
            }, void 0, this.f);
            for (f = h.length - 1; 0 <= f; --f) g.remove(h[f]);
            g.xe(k);
            if (0 < k.length || 0 < h.length) n = !0
        } else f.oe(b.pixel, function (b) {
            k.push(b)
        }, void 0, this.f), 0 < k.length && 1 == g.Ib() && g.item(0) == k[0] || (n = !0, 0 !== g.Ib() && (h = Array.prototype.concat(g.a), g.clear()), this.g ? g.xe(k) : 0 < k.length && g.push(k[0]));
        n && this.dispatchEvent(new sx("select", k, h));
        return Uj(b)
    }

    tx.prototype.setMap = function (b) {
        var c = this.j, d = this.c.a;
        null === c || d.forEach(c.hc, c);
        tx.T.setMap.call(this, b);
        this.c.setMap(b);
        null === b || d.forEach(b.Xa, b)
    };

    function vx() {
        var b = vl();
        $a(b.Polygon, b.LineString);
        $a(b.GeometryCollection, b.LineString);
        return function (c) {
            return b[c.R().O()]
        }
    }

    tx.prototype.q = function (b) {
        b = b.element;
        var c = this.j;
        null === c || c.Xa(b)
    };
    tx.prototype.r = function (b) {
        b = b.element;
        var c = this.j;
        null === c || c.hc(b)
    };

    function X(b) {
        b = m(b) ? b : {};
        var c = Ab(b);
        delete c.gradient;
        delete c.radius;
        delete c.blur;
        delete c.shadow;
        delete c.weight;
        J.call(this, c);
        this.ia = null;
        this.ef = m(b.shadow) ? b.shadow : 250;
        this.$c = void 0;
        this.zc = null;
        w(this, ud("gradient"), this.ph, !1, this);
        this.xc(m(b.gradient) ? b.gradient : wx);
        this.wc(m(b.blur) ? b.blur : 15);
        this.ic(m(b.radius) ? b.radius : 8);
        w(this, [ud("blur"), ud("radius")], this.Te, !1, this);
        this.Te();
        var d = m(b.weight) ? b.weight : "weight", e;
        ia(d) ? e = function (b) {
            return b.get(d)
        } : e = d;
        this.ka(ra(function (b) {
            b =
                e(b);
            b = m(b) ? Vb(b, 0, 1) : 1;
            var c = 255 * b | 0, d = this.zc[c];
            m(d) || (d = [new rl({image: new wj({opacity: b, src: this.$c})})], this.zc[c] = d);
            return d
        }, this));
        this.set("renderOrder", null);
        w(this, "render", this.sh, !1, this)
    }

    v(X, J);
    var wx = ["#00f", "#0ff", "#0f0", "#ff0", "#f00"];
    X.prototype.Ea = function () {
        return this.get("blur")
    };
    X.prototype.getBlur = X.prototype.Ea;
    X.prototype.Fa = function () {
        return this.get("gradient")
    };
    X.prototype.getGradient = X.prototype.Fa;
    X.prototype.hc = function () {
        return this.get("radius")
    };
    X.prototype.getRadius = X.prototype.hc;
    X.prototype.ph = function () {
        for (var b = this.Fa(), c = Nf(1, 256), d = c.createLinearGradient(0, 0, 1, 256), e = 1 / (b.length - 1), f = 0, g = b.length; f < g; ++f) d.addColorStop(f * e, b[f]);
        c.fillStyle = d;
        c.fillRect(0, 0, 1, 256);
        this.ia = c.getImageData(0, 0, 1, 256).data
    };
    X.prototype.Te = function () {
        var b = this.hc(), c = this.Ea(), d = b + c + 1, e = 2 * d, e = Nf(e, e);
        e.shadowOffsetX = e.shadowOffsetY = this.ef;
        e.shadowBlur = c;
        e.shadowColor = "#000";
        e.beginPath();
        c = d - this.ef;
        e.arc(c, c, b, 0, 2 * Math.PI, !0);
        e.fill();
        this.$c = e.canvas.toDataURL();
        this.zc = Array(256);
        this.l()
    };
    X.prototype.sh = function (b) {
        b = b.context;
        var c = b.canvas, c = b.getImageData(0, 0, c.width, c.height), d = c.data, e, f, g;
        e = 0;
        for (f = d.length; e < f; e += 4) if (g = 4 * d[e + 3]) d[e] = this.ia[g], d[e + 1] = this.ia[g + 1], d[e + 2] = this.ia[g + 2];
        b.putImageData(c, 0, 0)
    };
    X.prototype.wc = function (b) {
        this.set("blur", b)
    };
    X.prototype.setBlur = X.prototype.wc;
    X.prototype.xc = function (b) {
        this.set("gradient", b)
    };
    X.prototype.setGradient = X.prototype.xc;
    X.prototype.ic = function (b) {
        this.set("radius", b)
    };
    X.prototype.setRadius = X.prototype.ic;

    function xx(b) {
        return [b]
    };

    function yx(b, c) {
        var d = c || {}, e = d.document || document, f = Df("SCRIPT"), g = {og: f, ec: void 0}, h = new zw(zx, g),
            k = null, n = null != d.timeout ? d.timeout : 5E3;
        0 < n && (k = window.setTimeout(function () {
            Ax(f, !0);
            var c = new Bx(Cx, "Timeout reached for loading script " + b);
            Bw(h);
            Cw(h, !1, c)
        }, n), g.ec = k);
        f.onload = f.onreadystatechange = function () {
            f.readyState && "loaded" != f.readyState && "complete" != f.readyState || (Ax(f, d.nh || !1, k), Bw(h), Cw(h, !0, null))
        };
        f.onerror = function () {
            Ax(f, !0, k);
            var c = new Bx(Dx, "Error while loading script " + b);
            Bw(h);
            Cw(h, !1, c)
        };
        xf(f, {type: "text/javascript", charset: "UTF-8", src: b});
        Ex(e).appendChild(f);
        return h
    }

    function Ex(b) {
        var c = b.getElementsByTagName("HEAD");
        return c && 0 != c.length ? c[0] : b.documentElement
    }

    function zx() {
        if (this && this.og) {
            var b = this.og;
            b && "SCRIPT" == b.tagName && Ax(b, !0, this.ec)
        }
    }

    function Ax(b, c, d) {
        null != d && ba.clearTimeout(d);
        b.onload = ca;
        b.onerror = ca;
        b.onreadystatechange = ca;
        c && window.setTimeout(function () {
            Hf(b)
        }, 0)
    }

    var Dx = 0, Cx = 1;

    function Bx(b, c) {
        var d = "Jsloader error (code #" + b + ")";
        c && (d += ": " + c);
        wa.call(this, d);
        this.code = b
    }

    v(Bx, wa);

    function Fx(b, c) {
        this.d = new Mr(b);
        this.a = c ? c : "callback";
        this.ec = 5E3
    }

    var Gx = 0;
    Fx.prototype.send = function (b, c, d, e) {
        b = b || null;
        e = e || "_" + (Gx++).toString(36) + ta().toString(36);
        ba._callbacks_ || (ba._callbacks_ = {});
        var f = this.d.clone();
        if (b) for (var g in b) if (!b.hasOwnProperty || b.hasOwnProperty(g)) {
            var h = f, k = g, n = b[g];
            ga(n) || (n = [String(n)]);
            es(h.a, k, n)
        }
        c && (ba._callbacks_[e] = Hx(e, c), c = this.a, g = "_callbacks_." + e, ga(g) || (g = [String(g)]), es(f.a, c, g));
        c = yx(f.toString(), {timeout: this.ec, nh: !0});
        Fw(c, null, Ix(e, b, d), void 0);
        return {aa: e, jf: c}
    };
    Fx.prototype.cancel = function (b) {
        b && (b.jf && b.jf.cancel(), b.aa && Jx(b.aa, !1))
    };

    function Ix(b, c, d) {
        return function () {
            Jx(b, !1);
            d && d(c)
        }
    }

    function Hx(b, c) {
        return function (d) {
            Jx(b, !0);
            c.apply(void 0, arguments)
        }
    }

    function Jx(b, c) {
        ba._callbacks_[b] && (c ? delete ba._callbacks_[b] : ba._callbacks_[b] = ca)
    };

    function Kx(b) {
        var c = /\{z\}/g, d = /\{x\}/g, e = /\{y\}/g, f = /\{-y\}/g;
        return function (g) {
            return null === g ? void 0 : b.replace(c, g[0].toString()).replace(d, g[1].toString()).replace(e, g[2].toString()).replace(f, function () {
                return ((1 << g[0]) - g[2] - 1).toString()
            })
        }
    }

    function Lx(b) {
        return Mx(Ra(b, Kx))
    }

    function Mx(b) {
        return 1 === b.length ? b[0] : function (c, d, e) {
            return null === c ? void 0 : b[Wb((c[1] << c[0]) + c[2], b.length)](c, d, e)
        }
    }

    function Nx() {
    }

    function Ox(b, c) {
        var d = [0, 0, 0];
        return function (e, f, g) {
            return null === e ? void 0 : c(b(e, g, d), f, g)
        }
    }

    function Px(b) {
        var c = [], d = /\{(\d)-(\d)\}/.exec(b) || /\{([a-z])-([a-z])\}/.exec(b);
        if (d) {
            var e = d[2].charCodeAt(0), f;
            for (f = d[1].charCodeAt(0); f <= e; ++f) c.push(b.replace(d[0], String.fromCharCode(f)))
        } else c.push(b);
        return c
    };

    function Qx(b) {
        dj.call(this, {
            attributions: b.attributions,
            extent: b.extent,
            logo: b.logo,
            opaque: b.opaque,
            projection: b.projection,
            state: m(b.state) ? b.state : void 0,
            tileGrid: b.tileGrid,
            tilePixelRatio: b.tilePixelRatio
        });
        this.tileUrlFunction = m(b.tileUrlFunction) ? b.tileUrlFunction : Nx;
        this.crossOrigin = m(b.crossOrigin) ? b.crossOrigin : null;
        this.tileLoadFunction = m(b.tileLoadFunction) ? b.tileLoadFunction : Rx;
        this.tileClass = m(b.tileClass) ? b.tileClass : Yv
    }

    v(Qx, dj);

    function Rx(b, c) {
        b.Ta().src = c
    }

    l = Qx.prototype;
    l.Vb = function (b, c, d, e, f) {
        var g = this.nb(b, c, d);
        if (Si(this.a, g)) return this.a.get(g);
        b = [b, c, d];
        e = this.tileUrlFunction(b, e, f);
        e = new this.tileClass(b, m(e) ? 0 : 4, m(e) ? e : "", this.crossOrigin, this.tileLoadFunction);
        w(e, "change", this.sk, !1, this);
        this.a.set(g, e);
        return e
    };
    l.bb = function () {
        return this.tileLoadFunction
    };
    l.cb = function () {
        return this.tileUrlFunction
    };
    l.sk = function (b) {
        b = b.target;
        switch (b.state) {
            case 1:
                this.dispatchEvent(new gj("tileloadstart", b));
                break;
            case 2:
                this.dispatchEvent(new gj("tileloadend", b));
                break;
            case 3:
                this.dispatchEvent(new gj("tileloaderror", b))
        }
    };
    l.jb = function (b) {
        this.a.clear();
        this.tileLoadFunction = b;
        this.l()
    };
    l.ta = function (b) {
        this.a.clear();
        this.tileUrlFunction = b;
        this.l()
    };
    l.Pe = function (b, c, d) {
        b = this.nb(b, c, d);
        Si(this.a, b) && this.a.get(b)
    };

    function Sx(b) {
        var c = m(b.extent) ? b.extent : Nl, d = bj(c, b.maxZoom, b.tileSize);
        Vi.call(this, {minZoom: b.minZoom, origin: le(c, "top-left"), resolutions: d, tileSize: b.tileSize})
    }

    v(Sx, Vi);
    Sx.prototype.Db = function (b) {
        b = m(b) ? b : {};
        var c = this.minZoom, d = this.maxZoom, e = m(b.wrapX) ? b.wrapX : !0, f = null;
        if (m(b.extent)) {
            var f = Array(d + 1), g;
            for (g = 0; g <= d; ++g) f[g] = g < c ? null : Yi(this, b.extent, g)
        }
        return function (b, g, n) {
            g = b[0];
            if (g < c || d < g) return null;
            var p = Math.pow(2, g), q = b[1];
            if (e) q = Wb(q, p); else if (0 > q || p <= q) return null;
            b = b[2];
            return b < -p || -1 < b || null !== f && !nf(f[g], q, b) ? null : gf(g, q, -b - 1, n)
        }
    };
    Sx.prototype.sd = function (b, c) {
        if (b[0] < this.maxZoom) {
            var d = 2 * b[1], e = 2 * b[2];
            return mf(d, d + 1, e, e + 1, c)
        }
        return null
    };
    Sx.prototype.fd = function (b, c, d, e) {
        e = mf(0, b[1], 0, b[2], e);
        for (b = b[0] - 1; b >= this.minZoom; --b) if (e.a = e.c >>= 1, e.b = e.d >>= 1, c.call(d, b, e)) return !0;
        return !1
    };

    function Tx(b) {
        Qx.call(this, {
            crossOrigin: "anonymous",
            opaque: !0,
            projection: Ae("EPSG:3857"),
            state: "loading",
            tileLoadFunction: b.tileLoadFunction
        });
        this.c = m(b.culture) ? b.culture : "en-us";
        this.b = m(b.maxZoom) ? b.maxZoom : -1;
        this.i = m(b.wrapX) ? b.wrapX : !0;
        var c = new Mr((Sb ? "https:" : "http:") + "//dev.virtualearth.net/REST/v1/Imagery/Metadata/" + b.imagerySet);
        (new Fx(c, "jsonp")).send({
            include: "ImageryProviders",
            uriScheme: Sb ? "https" : "http",
            key: b.key
        }, ra(this.e, this))
    }

    v(Tx, Qx);
    var Ux = new pf({html: '<a class="ol-attribution-bing-tos" href="http://www.microsoft.com/maps/product/terms.html">Terms of Use</a>'});
    Tx.prototype.e = function (b) {
        if (200 != b.statusCode || "OK" != b.statusDescription || "ValidCredentials" != b.authenticationResultCode || 1 != b.resourceSets.length || 1 != b.resourceSets[0].resources.length) Li(this, "error"); else {
            var c = b.brandLogoUri;
            Sb && -1 == c.indexOf("https") && (c = c.replace("http", "https"));
            var d = b.resourceSets[0].resources[0], e = -1 == this.b ? d.zoomMax : this.b,
                f = new Sx({extent: cj(this.g), minZoom: d.zoomMin, maxZoom: e, tileSize: d.imageWidth});
            this.tileGrid = f;
            var g = this.c;
            this.tileUrlFunction = Ox(f.Db({wrapX: this.i}),
                Mx(Ra(d.imageUrlSubdomains, function (b) {
                    var c = d.imageUrl.replace("{subdomain}", b).replace("{culture}", g);
                    return function (b) {
                        return null === b ? void 0 : c.replace("{quadkey}", jf(b))
                    }
                })));
            if (d.imageryProviders) {
                var h = ze(Ae("EPSG:4326"), this.g);
                b = Ra(d.imageryProviders, function (b) {
                    var c = b.attribution, d = {};
                    Pa(b.coverageAreas, function (b) {
                        var c = b.zoomMin, g = Math.min(b.zoomMax, e);
                        b = b.bbox;
                        b = te([b[1], b[0], b[3], b[2]], h);
                        var k, n;
                        for (k = c; k <= g; ++k) n = k.toString(), c = Yi(f, b, k), n in d ? d[n].push(c) : d[n] = [c]
                    });
                    return new pf({
                        html: c,
                        tileRanges: d
                    })
                });
                b.push(Ux);
                this.f = b
            }
            this.D = c;
            Li(this, "ready")
        }
    };

    function Vx(b) {
        mn.call(this, {attributions: b.attributions, extent: b.extent, logo: b.logo, projection: b.projection});
        this.p = void 0;
        this.r = m(b.distance) ? b.distance : 20;
        this.o = [];
        this.a = b.source;
        this.a.s("change", Vx.prototype.N, this)
    }

    v(Vx, mn);
    Vx.prototype.H = function () {
        return this.a
    };
    Vx.prototype.Hb = function (b, c, d) {
        c !== this.p && (this.clear(), this.p = c, this.a.Hb(b, c, d), Wx(this), this.Ga(this.o))
    };
    Vx.prototype.N = function () {
        this.clear();
        Wx(this);
        this.Ga(this.o);
        this.l()
    };

    function Wx(b) {
        if (m(b.p)) {
            b.o.length = 0;
            for (var c = Sd(), d = b.r * b.p, e = b.a.Aa(), f = {}, g = 0, h = e.length; g < h; g++) {
                var k = e[g];
                sb(f, ma(k).toString()) || (k = k.R().Q(), be(k, c), Wd(c, d, c), k = jn(b.a.b, c), k = Qa(k, function (b) {
                    b = ma(b).toString();
                    return b in f ? !1 : f[b] = !0
                }), b.o.push(Xx(k)))
            }
        }
    }

    function Xx(b) {
        for (var c = b.length, d = [0, 0], e = 0; e < c; e++) {
            var f = b[e].R().Q();
            vd(d, f)
        }
        c = 1 / c;
        d[0] *= c;
        d[1] *= c;
        d = new N(new Jk(d));
        d.set("features", b);
        return d
    };

    function Yx(b, c, d) {
        if (ka(b)) d && (b = ra(b, d)); else if (b && "function" == typeof b.handleEvent) b = ra(b.handleEvent, b); else throw Error("Invalid listener argument");
        return 2147483647 < c ? -1 : ba.setTimeout(b, c || 0)
    };

    function Zx() {
    }

    Zx.prototype.a = null;

    function $x(b) {
        var c;
        (c = b.a) || (c = {}, ay(b) && (c[0] = !0, c[1] = !0), c = b.a = c);
        return c
    };var by;

    function cy() {
    }

    v(cy, Zx);

    function dy(b) {
        return (b = ay(b)) ? new ActiveXObject(b) : new XMLHttpRequest
    }

    function ay(b) {
        if (!b.d && "undefined" == typeof XMLHttpRequest && "undefined" != typeof ActiveXObject) {
            for (var c = ["MSXML2.XMLHTTP.6.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"], d = 0; d < c.length; d++) {
                var e = c[d];
                try {
                    return new ActiveXObject(e), b.d = e
                } catch (f) {
                }
            }
            throw Error("Could not create ActiveXObject. ActiveX might be disabled, or MSXML might not be installed");
        }
        return b.d
    }

    by = new cy;

    function ey(b) {
        hd.call(this);
        this.r = new uh;
        this.i = b || null;
        this.a = !1;
        this.n = this.U = null;
        this.f = this.o = "";
        this.d = this.q = this.c = this.j = !1;
        this.g = 0;
        this.b = null;
        this.e = fy;
        this.p = this.D = !1
    }

    v(ey, hd);
    var fy = "", gy = /^https?$/i, hy = ["POST", "PUT"];
    l = ey.prototype;
    l.send = function (b, c, d, e) {
        if (this.U) throw Error("[goog.net.XhrIo] Object is active with another request=" + this.o + "; newUri=" + b);
        c = c ? c.toUpperCase() : "GET";
        this.o = b;
        this.f = "";
        this.j = !1;
        this.a = !0;
        this.U = this.i ? dy(this.i) : dy(by);
        this.n = this.i ? $x(this.i) : $x(by);
        this.U.onreadystatechange = ra(this.Wf, this);
        try {
            this.q = !0, this.U.open(c, String(b), !0), this.q = !1
        } catch (f) {
            iy(this, f);
            return
        }
        b = d || "";
        var g = this.r.clone();
        e && th(e, function (b, c) {
            g.set(c, b)
        });
        e = Ta(g.G(), jy);
        d = ba.FormData && b instanceof ba.FormData;
        !Va(hy,
            c) || e || d || g.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        g.forEach(function (b, c) {
            this.U.setRequestHeader(c, b)
        }, this);
        this.e && (this.U.responseType = this.e);
        "withCredentials" in this.U && (this.U.withCredentials = this.D);
        try {
            ky(this), 0 < this.g && ((this.p = ly(this.U)) ? (this.U.timeout = this.g, this.U.ontimeout = ra(this.ec, this)) : this.b = Yx(this.ec, this.g, this)), this.c = !0, this.U.send(b), this.c = !1
        } catch (h) {
            iy(this, h)
        }
    };

    function ly(b) {
        return Fb && Ob(9) && ja(b.timeout) && m(b.ontimeout)
    }

    function jy(b) {
        return "content-type" == b.toLowerCase()
    }

    l.ec = function () {
        "undefined" != typeof aa && this.U && (this.f = "Timed out after " + this.g + "ms, aborting", this.dispatchEvent("timeout"), this.U && this.a && (this.a = !1, this.d = !0, this.U.abort(), this.d = !1, this.dispatchEvent("complete"), this.dispatchEvent("abort"), my(this)))
    };

    function iy(b, c) {
        b.a = !1;
        b.U && (b.d = !0, b.U.abort(), b.d = !1);
        b.f = c;
        ny(b);
        my(b)
    }

    function ny(b) {
        b.j || (b.j = !0, b.dispatchEvent("complete"), b.dispatchEvent("error"))
    }

    l.P = function () {
        this.U && (this.a && (this.a = !1, this.d = !0, this.U.abort(), this.d = !1), my(this, !0));
        ey.T.P.call(this)
    };
    l.Wf = function () {
        this.oa || (this.q || this.c || this.d ? oy(this) : this.hl())
    };
    l.hl = function () {
        oy(this)
    };

    function oy(b) {
        if (b.a && "undefined" != typeof aa && (!b.n[1] || 4 != py(b) || 2 != qy(b))) if (b.c && 4 == py(b)) Yx(b.Wf, 0, b); else if (b.dispatchEvent("readystatechange"), 4 == py(b)) {
            b.a = !1;
            try {
                if (ry(b)) b.dispatchEvent("complete"), b.dispatchEvent("success"); else {
                    var c;
                    try {
                        c = 2 < py(b) ? b.U.statusText : ""
                    } catch (d) {
                        c = ""
                    }
                    b.f = c + " [" + qy(b) + "]";
                    ny(b)
                }
            } finally {
                my(b)
            }
        }
    }

    function my(b, c) {
        if (b.U) {
            ky(b);
            var d = b.U, e = b.n[0] ? ca : null;
            b.U = null;
            b.n = null;
            c || b.dispatchEvent("ready");
            try {
                d.onreadystatechange = e
            } catch (f) {
            }
        }
    }

    function ky(b) {
        b.U && b.p && (b.U.ontimeout = null);
        ja(b.b) && (ba.clearTimeout(b.b), b.b = null)
    }

    function ry(b) {
        var c = qy(b), d;
        a:switch (c) {
            case 200:
            case 201:
            case 202:
            case 204:
            case 206:
            case 304:
            case 1223:
                d = !0;
                break a;
            default:
                d = !1
        }
        if (!d) {
            if (c = 0 === c) b = Gr(String(b.o))[1] || null, !b && self.location && (b = self.location.protocol, b = b.substr(0, b.length - 1)), c = !gy.test(b ? b.toLowerCase() : "");
            d = c
        }
        return d
    }

    function py(b) {
        return b.U ? b.U.readyState : 0
    }

    function qy(b) {
        try {
            return 2 < py(b) ? b.U.status : -1
        } catch (c) {
            return -1
        }
    }

    function sy(b) {
        try {
            return b.U ? b.U.responseText : ""
        } catch (c) {
            return ""
        }
    }

    function ty(b) {
        try {
            if (!b.U) return null;
            if ("response" in b.U) return b.U.response;
            switch (b.e) {
                case fy:
                case "text":
                    return b.U.responseText;
                case "arraybuffer":
                    if ("mozResponseArrayBuffer" in b.U) return b.U.mozResponseArrayBuffer
            }
            return null
        } catch (c) {
            return null
        }
    };

    function Y(b) {
        mn.call(this, {attributions: b.attributions, logo: b.logo, projection: b.projection});
        this.format = b.format
    }

    v(Y, mn);

    function uy(b, c, d, e, f) {
        var g = new ey;
        g.e = "binary" == b.format.O() && Xf ? "arraybuffer" : "text";
        w(g, "complete", function (b) {
            b = b.target;
            if (ry(b)) {
                var c = this.format.O(), g;
                if ("binary" == c && Xf) g = ty(b); else if ("json" == c) g = sy(b); else if ("text" == c) g = sy(b); else if ("xml" == c) {
                    if (!Fb) try {
                        g = b.U ? b.U.responseXML : null
                    } catch (p) {
                        g = null
                    }
                    null != g || (g = fq(sy(b)))
                }
                null != g ? d.call(f, this.a(g)) : Li(this, "error")
            } else e.call(f);
            pc(b)
        }, !1, b);
        g.send(c)
    }

    Y.prototype.a = function (b) {
        return this.format.ma(b, {featureProjection: this.g})
    };

    function Z(b) {
        Y.call(this, {attributions: b.attributions, format: b.format, logo: b.logo, projection: b.projection});
        m(b.arrayBuffer) && this.lb(this.a(b.arrayBuffer));
        m(b.doc) && this.lb(this.a(b.doc));
        m(b.node) && this.lb(this.a(b.node));
        m(b.object) && this.lb(this.a(b.object));
        m(b.text) && this.lb(this.a(b.text));
        if (m(b.url) || m(b.urls)) if (Li(this, "loading"), m(b.url) && uy(this, b.url, this.p, this.o, this), m(b.urls)) {
            b = b.urls;
            var c, d;
            c = 0;
            for (d = b.length; c < d; ++c) uy(this, b[c], this.p, this.o, this)
        }
    }

    v(Z, Y);
    Z.prototype.o = function () {
        Li(this, "error")
    };
    Z.prototype.p = function (b) {
        this.lb(b);
        Li(this, "ready")
    };

    function vy(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            attributions: b.attributions,
            extent: b.extent,
            format: new Bp({defaultDataProjection: b.defaultProjection}),
            logo: b.logo,
            object: b.object,
            projection: b.projection,
            text: b.text,
            url: b.url,
            urls: b.urls
        })
    }

    v(vy, Z);

    function wy(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            attributions: b.attributions,
            doc: b.doc,
            extent: b.extent,
            format: new Rq,
            logo: b.logo,
            node: b.node,
            projection: b.projection,
            text: b.text,
            url: b.url,
            urls: b.urls
        })
    }

    v(wy, Z);

    function xy(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            format: new Br({altitudeMode: b.altitudeMode}),
            projection: b.projection,
            text: b.text,
            url: b.url,
            urls: b.urls
        })
    }

    v(xy, Z);

    function yy(b) {
        bn.call(this, {projection: b.projection, resolutions: b.resolutions});
        this.H = m(b.crossOrigin) ? b.crossOrigin : null;
        this.e = m(b.displayDpi) ? b.displayDpi : 96;
        this.c = m(b.params) ? b.params : {};
        var c;
        m(b.url) ? c = $v(b.url, this.c, ra(this.Zj, this)) : c = aw;
        this.p = c;
        this.a = m(b.imageLoadFunction) ? b.imageLoadFunction : dn;
        this.N = m(b.hidpi) ? b.hidpi : !0;
        this.r = m(b.metersPerUnit) ? b.metersPerUnit : 1;
        this.j = m(b.ratio) ? b.ratio : 1;
        this.S = m(b.useOverlay) ? b.useOverlay : !1;
        this.b = null;
        this.o = 0
    }

    v(yy, bn);
    l = yy.prototype;
    l.Yj = function () {
        return this.c
    };
    l.sc = function (b, c, d, e) {
        c = cn(this, c);
        d = this.N ? d : 1;
        var f = this.b;
        if (null !== f && this.o == this.d && f.resolution == c && f.f == d && Zd(f.J(), b)) return f;
        1 != this.j && (b = b.slice(), se(b, this.j));
        e = this.p(b, [qe(b) / c * d, ne(b) / c * d], e);
        m(e) ? f = new Xv(b, c, d, this.f, e, this.H, this.a) : f = null;
        this.b = f;
        this.o = this.d;
        return f
    };
    l.Xj = function () {
        return this.a
    };
    l.ak = function (b) {
        Cb(this.c, b);
        this.l()
    };
    l.Zj = function (b, c, d, e) {
        var f;
        f = this.r;
        var g = qe(d), h = ne(d), k = e[0], n = e[1], p = .0254 / this.e;
        f = n * g > k * h ? g * f / (k * p) : h * f / (n * p);
        d = ke(d);
        e = {
            OPERATION: this.S ? "GETDYNAMICMAPOVERLAYIMAGE" : "GETMAPIMAGE",
            VERSION: "2.0.0",
            LOCALE: "en",
            CLIENTAGENT: "ol.source.ImageMapGuide source",
            CLIP: "1",
            SETDISPLAYDPI: this.e,
            SETDISPLAYWIDTH: Math.round(e[0]),
            SETDISPLAYHEIGHT: Math.round(e[1]),
            SETVIEWSCALE: f,
            SETVIEWCENTERX: d[0],
            SETVIEWCENTERY: d[1]
        };
        Cb(e, c);
        return Jr(Lr([b], e))
    };
    l.$j = function (b) {
        this.b = null;
        this.a = b;
        this.l()
    };

    function zy(b) {
        var c = m(b.attributions) ? b.attributions : null, d = b.imageExtent, e, f;
        m(b.imageSize) && (e = ne(d) / b.imageSize[1], f = [e]);
        var g = m(b.crossOrigin) ? b.crossOrigin : null, h = m(b.imageLoadFunction) ? b.imageLoadFunction : dn;
        bn.call(this, {attributions: c, logo: b.logo, projection: Ae(b.projection), resolutions: f});
        this.a = new Xv(d, e, 1, c, b.url, g, h)
    }

    v(zy, bn);
    zy.prototype.sc = function (b) {
        return pe(b, this.a.J()) ? this.a : null
    };

    function Ay(b) {
        b = m(b) ? b : {};
        bn.call(this, {
            attributions: b.attributions,
            logo: b.logo,
            projection: b.projection,
            resolutions: b.resolutions
        });
        this.N = m(b.crossOrigin) ? b.crossOrigin : null;
        this.c = b.url;
        this.j = m(b.imageLoadFunction) ? b.imageLoadFunction : dn;
        this.a = b.params;
        this.e = !0;
        By(this);
        this.H = b.serverType;
        this.S = m(b.hidpi) ? b.hidpi : !0;
        this.b = null;
        this.o = [0, 0];
        this.r = 0;
        this.p = m(b.ratio) ? b.ratio : 1.5
    }

    v(Ay, bn);
    var Cy = [101, 101];
    l = Ay.prototype;
    l.gk = function (b, c, d, e) {
        if (m(this.c)) {
            var f = me(b, c, 0, Cy), g = {
                SERVICE: "WMS",
                VERSION: "1.3.0",
                REQUEST: "GetFeatureInfo",
                FORMAT: "image/png",
                TRANSPARENT: !0,
                QUERY_LAYERS: this.a.LAYERS
            };
            Cb(g, this.a, e);
            e = Math.floor((f[3] - b[1]) / c);
            g[this.e ? "I" : "X"] = Math.floor((b[0] - f[0]) / c);
            g[this.e ? "J" : "Y"] = e;
            return Dy(this, f, Cy, 1, Ae(d), g)
        }
    };
    l.ik = function () {
        return this.a
    };
    l.sc = function (b, c, d, e) {
        if (!m(this.c)) return null;
        c = cn(this, c);
        1 == d || this.S && m(this.H) || (d = 1);
        var f = this.b;
        if (null !== f && this.r == this.d && f.resolution == c && f.f == d && Zd(f.J(), b)) return f;
        f = {SERVICE: "WMS", VERSION: "1.3.0", REQUEST: "GetMap", FORMAT: "image/png", TRANSPARENT: !0};
        Cb(f, this.a);
        b = b.slice();
        var g = (b[0] + b[2]) / 2, h = (b[1] + b[3]) / 2;
        if (1 != this.p) {
            var k = this.p * qe(b) / 2, n = this.p * ne(b) / 2;
            b[0] = g - k;
            b[1] = h - n;
            b[2] = g + k;
            b[3] = h + n
        }
        var k = c / d, n = Math.ceil(qe(b) / k), p = Math.ceil(ne(b) / k);
        b[0] = g - k * n / 2;
        b[2] = g + k * n / 2;
        b[1] = h - k *
            p / 2;
        b[3] = h + k * p / 2;
        this.o[0] = n;
        this.o[1] = p;
        e = Dy(this, b, this.o, d, e, f);
        this.b = new Xv(b, c, d, this.f, e, this.N, this.j);
        this.r = this.d;
        return this.b
    };
    l.hk = function () {
        return this.j
    };

    function Dy(b, c, d, e, f, g) {
        g[b.e ? "CRS" : "SRS"] = f.a;
        "STYLES" in b.a || (g.STYLES = new String(""));
        if (1 != e) switch (b.H) {
            case "geoserver":
                g.FORMAT_OPTIONS = "dpi:" + (90 * e + .5 | 0);
                break;
            case "mapserver":
                g.MAP_RESOLUTION = 90 * e;
                break;
            case "carmentaserver":
            case "qgis":
                g.DPI = 90 * e
        }
        g.WIDTH = d[0];
        g.HEIGHT = d[1];
        d = f.b;
        var h;
        b.e && "ne" == d.substr(0, 2) ? h = [c[1], c[0], c[3], c[2]] : h = c;
        g.BBOX = h.join(",");
        return Jr(Lr([b.c], g))
    }

    l.jk = function () {
        return this.c
    };
    l.kk = function (b) {
        this.b = null;
        this.j = b;
        this.l()
    };
    l.lk = function (b) {
        b != this.c && (this.c = b, this.b = null, this.l())
    };
    l.mk = function (b) {
        Cb(this.a, b);
        By(this);
        this.b = null;
        this.l()
    };

    function By(b) {
        b.e = 0 <= Ma(yb(b.a, "VERSION", "1.3.0"), "1.3")
    };

    function Ey(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            attributions: b.attributions,
            doc: b.doc,
            format: new gs({extractStyles: b.extractStyles, defaultStyle: b.defaultStyle}),
            logo: b.logo,
            node: b.node,
            projection: b.projection,
            text: b.text,
            url: b.url,
            urls: b.urls
        })
    }

    v(Ey, Z);

    function Fy(b) {
        var c = m(b.projection) ? b.projection : "EPSG:3857",
            d = new Sx({extent: cj(c), maxZoom: b.maxZoom, tileSize: b.tileSize});
        Qx.call(this, {
            attributions: b.attributions,
            crossOrigin: b.crossOrigin,
            logo: b.logo,
            projection: c,
            tileGrid: d,
            tileLoadFunction: b.tileLoadFunction,
            tilePixelRatio: b.tilePixelRatio,
            tileUrlFunction: Nx
        });
        this.i = d.Db({wrapX: b.wrapX});
        m(b.tileUrlFunction) ? this.ta(b.tileUrlFunction) : m(b.urls) ? this.ta(Lx(b.urls)) : m(b.url) && this.b(b.url)
    }

    v(Fy, Qx);
    Fy.prototype.ta = function (b) {
        Fy.T.ta.call(this, Ox(this.i, b))
    };
    Fy.prototype.b = function (b) {
        this.ta(Lx(Px(b)))
    };

    function Gy(b) {
        b = m(b) ? b : {};
        var c;
        m(b.attributions) ? c = b.attributions : c = [Hy];
        var d = Sb ? "https:" : "http:";
        Fy.call(this, {
            attributions: c,
            crossOrigin: m(b.crossOrigin) ? b.crossOrigin : "anonymous",
            opaque: !0,
            maxZoom: m(b.maxZoom) ? b.maxZoom : 19,
            tileLoadFunction: b.tileLoadFunction,
            url: m(b.url) ? b.url : d + "//{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            wrapX: b.wrapX
        })
    }

    v(Gy, Fy);
    var Hy = new pf({html: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors.'});

    function Iy(b) {
        b = m(b) ? b : {};
        var c = Jy[b.layer];
        this.c = b.layer;
        var d = Sb ? "https:" : "http:";
        Fy.call(this, {
            attributions: c.attributions,
            crossOrigin: "anonymous",
            logo: "//developer.mapquest.com/content/osm/mq_logo.png",
            maxZoom: c.maxZoom,
            opaque: !0,
            tileLoadFunction: b.tileLoadFunction,
            url: m(b.url) ? b.url : d + "//otile{1-4}-s.mqcdn.com/tiles/1.0.0/" + this.c + "/{z}/{x}/{y}.jpg"
        })
    }

    v(Iy, Fy);
    var Ky = new pf({html: 'Tiles Courtesy of <a href="http://www.mapquest.com/">MapQuest</a>'}), Jy = {
        osm: {maxZoom: 19, attributions: [Ky, Hy]},
        sat: {
            maxZoom: 18,
            attributions: [Ky, new pf({html: "Portions Courtesy NASA/JPL-Caltech and U.S. Depart. of Agriculture, Farm Service Agency"})]
        },
        hyb: {maxZoom: 18, attributions: [Ky, Hy]}
    };
    Iy.prototype.e = function () {
        return this.c
    };

    function Ly(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            attributions: b.attributions,
            doc: b.doc,
            format: new Qt,
            logo: b.logo,
            node: b.node,
            projection: b.projection,
            text: b.text,
            url: b.url,
            urls: b.urls
        })
    }

    v(Ly, Z);

    function $(b) {
        Y.call(this, {attributions: b.attributions, format: b.format, logo: b.logo, projection: b.projection});
        this.p = new gn;
        this.r = b.loader;
        this.H = m(b.strategy) ? b.strategy : xx;
        this.o = {}
    }

    v($, Y);
    $.prototype.lb = function (b) {
        var c = [], d, e;
        d = 0;
        for (e = b.length; d < e; ++d) {
            var f = b[d], g = f.aa;
            m(g) ? g in this.o || (c.push(f), this.o[g] = !0) : c.push(f)
        }
        $.T.lb.call(this, c)
    };
    $.prototype.clear = function (b) {
        wb(this.o);
        this.p.clear();
        $.T.clear.call(this, b)
    };
    $.prototype.Hb = function (b, c, d) {
        var e = this.p;
        b = this.H(b, c);
        var f, g;
        f = 0;
        for (g = b.length; f < g; ++f) {
            var h = b[f];
            ln(e, h, function (b) {
                return Zd(b.extent, h)
            }) || (this.r.call(this, h, c, d), e.sa(h, {extent: h.slice()}))
        }
    };
    var My = {
        terrain: {Za: "jpg", opaque: !0},
        "terrain-background": {Za: "jpg", opaque: !0},
        "terrain-labels": {Za: "png", opaque: !1},
        "terrain-lines": {Za: "png", opaque: !1},
        "toner-background": {Za: "png", opaque: !0},
        toner: {Za: "png", opaque: !0},
        "toner-hybrid": {Za: "png", opaque: !1},
        "toner-labels": {Za: "png", opaque: !1},
        "toner-lines": {Za: "png", opaque: !1},
        "toner-lite": {Za: "png", opaque: !0},
        watercolor: {Za: "jpg", opaque: !0}
    }, Ny = {
        terrain: {minZoom: 4, maxZoom: 18},
        toner: {minZoom: 0, maxZoom: 20},
        watercolor: {minZoom: 3, maxZoom: 16}
    };

    function Oy(b) {
        var c = b.layer.indexOf("-"), d = My[b.layer],
            e = Sb ? "https://stamen-tiles-{a-d}.a.ssl.fastly.net/" : "http://{a-d}.tile.stamen.com/";
        Fy.call(this, {
            attributions: Py,
            crossOrigin: "anonymous",
            maxZoom: Ny[-1 == c ? b.layer : b.layer.slice(0, c)].maxZoom,
            opaque: d.opaque,
            tileLoadFunction: b.tileLoadFunction,
            url: m(b.url) ? b.url : e + b.layer + "/{z}/{x}/{y}." + d.Za
        })
    }

    v(Oy, Fy);
    var Py = [new pf({html: 'Map tiles by <a href="http://stamen.com/">Stamen Design</a>, under <a href="http://creativecommons.org/licenses/by/3.0/">CC BY 3.0</a>.'}), Hy];

    function Qy(b) {
        b = m(b) ? b : {};
        var c = m(b.params) ? b.params : {};
        Qx.call(this, {
            attributions: b.attributions,
            logo: b.logo,
            projection: b.projection,
            tileGrid: b.tileGrid,
            tileLoadFunction: b.tileLoadFunction,
            tileUrlFunction: ra(this.qk, this)
        });
        var d = b.urls;
        !m(d) && m(b.url) && (d = Px(b.url));
        this.c = null != d ? d : [];
        this.b = c;
        this.e = Sd()
    }

    v(Qy, Qx);
    l = Qy.prototype;
    l.nk = function () {
        return this.b
    };
    l.ok = function () {
        return this.c
    };
    l.pk = function (b) {
        b = m(b) ? Px(b) : null;
        this.Nf(b)
    };
    l.Nf = function (b) {
        this.c = null != b ? b : [];
        this.l()
    };
    l.qk = function (b, c, d) {
        var e = this.tileGrid;
        null === e && (e = fj(this, d));
        if (!(e.a.length <= b[0])) {
            var f = Xi(e, b, this.e), e = e.ua(b[0]);
            1 != c && (e = e * c + .5 | 0);
            c = {F: "image", FORMAT: "PNG32", TRANSPARENT: !0};
            Cb(c, this.b);
            var g = this.c;
            0 == g.length ? b = void 0 : (d = d.a.split(":").pop(), c.SIZE = e + "," + e, c.BBOX = f.join(","), c.BBOXSR = d, c.IMAGESR = d, b = 1 == g.length ? g[0] : g[Wb((b[1] << b[0]) + b[2], g.length)], ya(b, "/") || (b += "/"), ya(b, "MapServer/") ? b += "export" : ya(b, "ImageServer/") && (b += "exportImage"), b = Jr(Lr([b], c)));
            return b
        }
    };
    l.rk = function (b) {
        Cb(this.b, b);
        this.l()
    };

    function Ry(b, c) {
        Pi.call(this, b, 2);
        this.b = c.ua(b[0]);
        this.d = {}
    }

    v(Ry, Pi);
    Ry.prototype.Ta = function (b) {
        b = m(b) ? ma(b) : -1;
        if (b in this.d) return this.d[b];
        var c = this.b, d = Nf(c, c);
        d.strokeStyle = "black";
        d.strokeRect(.5, .5, c + .5, c + .5);
        d.fillStyle = "black";
        d.textAlign = "center";
        d.textBaseline = "middle";
        d.font = "24px sans-serif";
        d.fillText(kf(this.a), c / 2, c / 2);
        return this.d[b] = d.canvas
    };

    function Sy(b) {
        dj.call(this, {opaque: !1, projection: b.projection, tileGrid: b.tileGrid})
    }

    v(Sy, dj);
    Sy.prototype.Vb = function (b, c, d) {
        var e = this.nb(b, c, d);
        if (Si(this.a, e)) return this.a.get(e);
        b = new Ry([b, c, d], this.tileGrid);
        this.a.set(e, b);
        return b
    };

    function Ty(b) {
        Qx.call(this, {
            attributions: b.attributions,
            crossOrigin: b.crossOrigin,
            projection: Ae("EPSG:3857"),
            state: "loading",
            tileLoadFunction: b.tileLoadFunction
        });
        this.b = b.wrapX;
        (new Fx(b.url)).send(void 0, ra(this.c, this))
    }

    v(Ty, Qx);
    Ty.prototype.c = function (b) {
        var c = Ae("EPSG:4326"), d = this.g, e;
        m(b.bounds) && (e = te(b.bounds, ze(c, d)));
        var f = b.minzoom || 0, g = b.maxzoom || 22;
        this.tileGrid = d = new Sx({extent: cj(d), maxZoom: g, minZoom: f});
        this.tileUrlFunction = Ox(d.Db({extent: e, wrapX: this.b}), Lx(b.tiles));
        if (m(b.attribution) && null === this.f) {
            c = m(e) ? e : c.J();
            e = {};
            for (var h; f <= g; ++f) h = f.toString(), e[h] = [Yi(d, c, f)];
            this.f = [new pf({html: b.attribution, tileRanges: e})]
        }
        Li(this, "ready")
    };

    function Uy(b) {
        dj.call(this, {projection: Ae("EPSG:3857"), state: "loading"});
        this.e = m(b.preemptive) ? b.preemptive : !0;
        this.b = Nx;
        this.c = void 0;
        (new Fx(b.url)).send(void 0, ra(this.tk, this))
    }

    v(Uy, dj);
    l = Uy.prototype;
    l.$h = function () {
        return this.c
    };
    l.vh = function (b, c, d, e, f) {
        null === this.tileGrid ? !0 === f ? oh(function () {
            d.call(e, null)
        }) : d.call(e, null) : (c = this.tileGrid.Wb(b, c), Vy(this.Vb(c[0], c[1], c[2], 1, this.g), b, d, e, f))
    };
    l.tk = function (b) {
        var c = Ae("EPSG:4326"), d = this.g, e;
        m(b.bounds) && (e = te(b.bounds, ze(c, d)));
        var f = b.minzoom || 0, g = b.maxzoom || 22;
        this.tileGrid = d = new Sx({extent: cj(d), maxZoom: g, minZoom: f});
        this.c = b.template;
        var h = b.grids;
        if (null != h) {
            this.b = Ox(d.Db({extent: e}), Lx(h));
            if (m(b.attribution)) {
                c = m(e) ? e : c.J();
                for (e = {}; f <= g; ++f) h = f.toString(), e[h] = [Yi(d, c, f)];
                this.f = [new pf({html: b.attribution, tileRanges: e})]
            }
            Li(this, "ready")
        } else Li(this, "error")
    };
    l.Vb = function (b, c, d, e, f) {
        var g = this.nb(b, c, d);
        if (Si(this.a, g)) return this.a.get(g);
        b = [b, c, d];
        e = this.b(b, e, f);
        e = new Wy(b, m(e) ? 0 : 4, m(e) ? e : "", Xi(this.tileGrid, b), this.e);
        this.a.set(g, e);
        return e
    };
    l.Pe = function (b, c, d) {
        b = this.nb(b, c, d);
        Si(this.a, b) && this.a.get(b)
    };

    function Wy(b, c, d, e, f) {
        Pi.call(this, b, c);
        this.g = d;
        this.d = e;
        this.n = f;
        this.c = this.f = this.b = null
    }

    v(Wy, Pi);
    l = Wy.prototype;
    l.Ta = function () {
        return null
    };

    function Xy(b, c) {
        if (null === b.b || null === b.f || null === b.c) return null;
        var d = b.b[Math.floor((1 - (c[1] - b.d[1]) / (b.d[3] - b.d[1])) * b.b.length)];
        if (!ia(d)) return null;
        d = d.charCodeAt(Math.floor((c[0] - b.d[0]) / (b.d[2] - b.d[0]) * d.length));
        93 <= d && d--;
        35 <= d && d--;
        d = b.f[d - 32];
        return null != d ? b.c[d] : null
    }

    function Vy(b, c, d, e, f) {
        0 == b.state && !0 === f ? (Uc(b, "change", function () {
            d.call(e, Xy(this, c))
        }, !1, b), Yy(b)) : !0 === f ? oh(function () {
            d.call(e, Xy(this, c))
        }, b) : d.call(e, Xy(b, c))
    }

    l.qb = function () {
        return this.g
    };
    l.pi = function () {
        this.state = 3;
        Qi(this)
    };
    l.Ci = function (b) {
        this.b = b.grid;
        this.f = b.keys;
        this.c = b.data;
        this.state = 4;
        Qi(this)
    };

    function Yy(b) {
        0 == b.state && (b.state = 1, (new Fx(b.g)).send(void 0, ra(b.Ci, b), ra(b.pi, b)))
    }

    l.load = function () {
        this.n && Yy(this)
    };

    function Zy(b) {
        Y.call(this, {attributions: b.attributions, format: b.format, logo: b.logo, projection: b.projection});
        this.p = b.tileGrid;
        this.r = Nx;
        this.H = this.p.Db();
        this.o = {};
        m(b.tileUrlFunction) ? (this.r = b.tileUrlFunction, this.l()) : m(b.urls) ? (this.r = Lx(b.urls), this.l()) : m(b.url) && (this.r = Lx(Px(b.url)), this.l())
    }

    v(Zy, Y);
    l = Zy.prototype;
    l.clear = function () {
        wb(this.o)
    };

    function $y(b, c, d, e) {
        var f = b.o;
        b = b.p.Wb(c, d);
        f = f[b[0] + "/" + b[1] + "/" + b[2]];
        if (m(f)) for (b = 0, d = f.length; b < d; ++b) {
            var g = f[b];
            if (g.R().Jb(c[0], c[1]) && e.call(void 0, g)) break
        }
    }

    l.Fb = function (b, c, d, e) {
        var f = this.p, g = this.o;
        c = ac(f.a, c, 0);
        b = Yi(f, b, c);
        for (var h, f = b.a; f <= b.c; ++f) for (h = b.b; h <= b.d; ++h) {
            var k = g[c + "/" + f + "/" + h];
            if (m(k)) {
                var n, p;
                n = 0;
                for (p = k.length; n < p; ++n) {
                    var q = d.call(e, k[n]);
                    if (q) return q
                }
            }
        }
    };
    l.Aa = function () {
        var b = this.o, c = [], d;
        for (d in b) $a(c, b[d]);
        return c
    };
    l.Ch = function (b, c) {
        var d = [];
        $y(this, b, c, function (b) {
            d.push(b)
        });
        return d
    };
    l.Hb = function (b, c, d) {
        function e(b, c) {
            k[b] = c;
            Li(this, "ready")
        }

        var f = this.H, g = this.p, h = this.r, k = this.o;
        c = ac(g.a, c, 0);
        b = Yi(g, b, c);
        var g = [c, 0, 0], n, p;
        for (n = b.a; n <= b.c; ++n) for (p = b.b; p <= b.d; ++p) {
            var q = c + "/" + n + "/" + p;
            if (!(q in k)) {
                g[0] = c;
                g[1] = n;
                g[2] = p;
                f(g, d, g);
                var r = h(g, 1, d);
                m(r) && (k[q] = [], uy(this, r, sa(e, q), ca, this))
            }
        }
    };

    function az(b) {
        b = m(b) ? b : {};
        var c = m(b.params) ? b.params : {};
        Qx.call(this, {
            attributions: b.attributions,
            crossOrigin: b.crossOrigin,
            logo: b.logo,
            opaque: !yb(c, "TRANSPARENT", !0),
            projection: b.projection,
            tileGrid: b.tileGrid,
            tileLoadFunction: b.tileLoadFunction,
            tileUrlFunction: ra(this.yk, this)
        });
        var d = b.urls;
        !m(d) && m(b.url) && (d = Px(b.url));
        this.c = null != d ? d : [];
        this.i = m(b.gutter) ? b.gutter : 0;
        this.b = c;
        this.e = !0;
        this.j = b.serverType;
        this.p = m(b.hidpi) ? b.hidpi : !0;
        this.o = "";
        bz(this);
        this.r = Sd();
        cz(this)
    }

    v(az, Qx);
    l = az.prototype;
    l.uk = function (b, c, d, e) {
        d = Ae(d);
        var f = this.tileGrid;
        null === f && (f = fj(this, d));
        c = f.Wb(b, c);
        if (!(f.a.length <= c[0])) {
            var g = f.na(c[0]), h = Xi(f, c, this.r), f = f.ua(c[0]), k = this.i;
            0 !== k && (f += 2 * k, h = Wd(h, g * k, h));
            k = {
                SERVICE: "WMS",
                VERSION: "1.3.0",
                REQUEST: "GetFeatureInfo",
                FORMAT: "image/png",
                TRANSPARENT: !0,
                QUERY_LAYERS: this.b.LAYERS
            };
            Cb(k, this.b, e);
            e = Math.floor((h[3] - b[1]) / g);
            k[this.e ? "I" : "X"] = Math.floor((b[0] - h[0]) / g);
            k[this.e ? "J" : "Y"] = e;
            return dz(this, c, f, h, 1, d, k)
        }
    };
    l.hd = function () {
        return this.i
    };
    l.nb = function (b, c, d) {
        return this.o + az.T.nb.call(this, b, c, d)
    };
    l.vk = function () {
        return this.b
    };

    function dz(b, c, d, e, f, g, h) {
        var k = b.c;
        if (0 != k.length) {
            h.WIDTH = d;
            h.HEIGHT = d;
            h[b.e ? "CRS" : "SRS"] = g.a;
            "STYLES" in b.b || (h.STYLES = new String(""));
            if (1 != f) switch (b.j) {
                case "geoserver":
                    h.FORMAT_OPTIONS = "dpi:" + (90 * f + .5 | 0);
                    break;
                case "mapserver":
                    h.MAP_RESOLUTION = 90 * f;
                    break;
                case "carmentaserver":
                case "qgis":
                    h.DPI = 90 * f
            }
            d = g.b;
            b.e && "ne" == d.substr(0, 2) && (b = e[0], e[0] = e[1], e[1] = b, b = e[2], e[2] = e[3], e[3] = b);
            h.BBOX = e.join(",");
            return Jr(Lr([1 == k.length ? k[0] : k[Wb((c[1] << c[0]) + c[2], k.length)]], h))
        }
    }

    l.Nc = function (b, c, d) {
        b = az.T.Nc.call(this, b, c, d);
        return 1 != c && this.p && m(this.j) ? b * c + .5 | 0 : b
    };
    l.wk = function () {
        return this.c
    };

    function bz(b) {
        var c = 0, d = [], e, f;
        e = 0;
        for (f = b.c.length; e < f; ++e) d[c++] = b.c[e];
        for (var g in b.b) d[c++] = g + "-" + b.b[g];
        b.o = d.join("#")
    }

    l.xk = function (b) {
        b = m(b) ? Px(b) : null;
        this.Of(b)
    };
    l.Of = function (b) {
        this.c = null != b ? b : [];
        bz(this);
        this.l()
    };
    l.yk = function (b, c, d) {
        var e = this.tileGrid;
        null === e && (e = fj(this, d));
        if (!(e.a.length <= b[0])) {
            1 == c || this.p && m(this.j) || (c = 1);
            var f = e.na(b[0]), g = Xi(e, b, this.r), e = e.ua(b[0]), h = this.i;
            0 !== h && (e += 2 * h, g = Wd(g, f * h, g));
            1 != c && (e = e * c + .5 | 0);
            f = {SERVICE: "WMS", VERSION: "1.3.0", REQUEST: "GetMap", FORMAT: "image/png", TRANSPARENT: !0};
            Cb(f, this.b);
            return dz(this, b, e, g, c, d, f)
        }
    };
    l.zk = function (b) {
        Cb(this.b, b);
        bz(this);
        cz(this);
        this.l()
    };

    function cz(b) {
        b.e = 0 <= Ma(yb(b.b, "VERSION", "1.3.0"), "1.3")
    };

    function ez(b) {
        b = m(b) ? b : {};
        Z.call(this, {
            attributions: b.attributions,
            extent: b.extent,
            format: new tu({defaultDataProjection: b.defaultProjection}),
            logo: b.logo,
            object: b.object,
            projection: b.projection,
            text: b.text,
            url: b.url
        })
    }

    v(ez, Z);

    function fz(b) {
        this.b = b.matrixIds;
        Vi.call(this, {
            origin: b.origin,
            origins: b.origins,
            resolutions: b.resolutions,
            tileSize: b.tileSize,
            tileSizes: b.tileSizes
        })
    }

    v(fz, Vi);
    fz.prototype.g = function () {
        return this.b
    };

    function gz(b) {
        var c = [], d = [], e = [], f = [], g;
        g = Ae(b.SupportedCRS.replace(/urn:ogc:def:crs:(\w+):(.*:)?(\w+)$/, "$1:$3"));
        var h = g.md(), k = "ne" == g.b.substr(0, 2);
        cb(b.TileMatrix, function (b, c) {
            return c.ScaleDenominator - b.ScaleDenominator
        });
        Pa(b.TileMatrix, function (b) {
            d.push(b.Identifier);
            k ? e.push([b.TopLeftCorner[1], b.TopLeftCorner[0]]) : e.push(b.TopLeftCorner);
            c.push(2.8E-4 * b.ScaleDenominator / h);
            f.push(b.TileWidth)
        });
        return new fz({origins: e, resolutions: c, matrixIds: d, tileSizes: f})
    };

    function hz(b) {
        function c(b) {
            b = "KVP" == d ? Jr(Lr([b], f)) : b.replace(/\{(\w+?)\}/g, function (b, c) {
                return c.toLowerCase() in f ? f[c.toLowerCase()] : b
            });
            return function (c) {
                if (null !== c) {
                    var f = {TileMatrix: e.b[c[0]], TileCol: c[1], TileRow: c[2]};
                    Cb(f, g);
                    c = b;
                    return c = "KVP" == d ? Jr(Lr([c], f)) : c.replace(/\{(\w+?)\}/g, function (b, c) {
                        return f[c]
                    })
                }
            }
        }

        this.p = m(b.version) ? b.version : "1.0.0";
        this.c = m(b.format) ? b.format : "image/jpeg";
        this.b = m(b.dimensions) ? b.dimensions : {};
        this.i = "";
        iz(this);
        this.j = b.layer;
        this.e = b.matrixSet;
        this.o =
            b.style;
        var d = m(b.requestEncoding) ? b.requestEncoding : "KVP", e = b.tileGrid,
            f = {layer: this.j, style: this.o, tilematrixset: this.e};
        "KVP" == d && Cb(f, {Service: "WMTS", Request: "GetTile", Version: this.p, Format: this.c});
        var g = this.b, h = Nx, k = b.urls;
        !m(k) && m(b.url) && (k = Px(b.url));
        m(k) && (h = Mx(Ra(k, c)));
        var n = Sd(), p = [0, 0, 0], h = Ox(function (b, c, d) {
            if (e.a.length <= b[0]) return null;
            var f = b[1], g = -b[2] - 1, h = Xi(e, b, n), k = c.J();
            null !== k && c.e && (c = Math.ceil(qe(k) / qe(h)), f = Wb(f, c), p[0] = b[0], p[1] = f, p[2] = b[2], h = Xi(e, p, n));
            return !pe(h,
                k) || pe(h, k) && (h[0] == k[2] || h[2] == k[0] || h[1] == k[3] || h[3] == k[1]) ? null : gf(b[0], f, g, d)
        }, h);
        Qx.call(this, {
            attributions: b.attributions,
            crossOrigin: b.crossOrigin,
            logo: b.logo,
            projection: b.projection,
            tileClass: b.tileClass,
            tileGrid: e,
            tileLoadFunction: b.tileLoadFunction,
            tilePixelRatio: b.tilePixelRatio,
            tileUrlFunction: h
        })
    }

    v(hz, Qx);
    l = hz.prototype;
    l.Ah = function () {
        return this.b
    };
    l.Eh = function () {
        return this.c
    };
    l.nb = function (b, c, d) {
        return this.i + hz.T.nb.call(this, b, c, d)
    };
    l.Ak = function () {
        return this.j
    };
    l.Qh = function () {
        return this.e
    };
    l.Bk = function () {
        return this.o
    };
    l.di = function () {
        return this.p
    };

    function iz(b) {
        var c = 0, d = [], e;
        for (e in b.b) d[c++] = e + "-" + b.b[e];
        b.i = d.join("/")
    }

    l.om = function (b) {
        Cb(this.b, b);
        iz(this);
        this.l()
    };

    function jz(b) {
        var c = m(b) ? b : c;
        Vi.call(this, {origin: [0, 0], resolutions: c.resolutions})
    }

    v(jz, Vi);
    jz.prototype.Db = function (b) {
        b = m(b) ? b : {};
        var c = this.minZoom, d = this.maxZoom, e = null;
        if (m(b.extent)) {
            var e = Array(d + 1), f;
            for (f = 0; f <= d; ++f) e[f] = f < c ? null : Yi(this, b.extent, f)
        }
        return function (b, f, k) {
            f = b[0];
            if (f < c || d < f) return null;
            var n = Math.pow(2, f), p = b[1];
            if (0 > p || n <= p) return null;
            b = b[2];
            return b < -n || -1 < b || null !== e && !nf(e[f], p, -b - 1) ? null : gf(f, p, -b - 1, k)
        }
    };

    function kz(b) {
        b = m(b) ? b : {};
        var c = b.size, d = c[0], e = c[1], f = [], g = 256;
        switch (m(b.tierSizeCalculation) ? b.tierSizeCalculation : "default") {
            case "default":
                for (; d > g || e > g;) f.push([Math.ceil(d / g), Math.ceil(e / g)]), g += g;
                break;
            case "truncated":
                for (; d > g || e > g;) f.push([Math.ceil(d / g), Math.ceil(e / g)]), d >>= 1, e >>= 1
        }
        f.push([1, 1]);
        f.reverse();
        for (var g = [1], h = [0], e = 1, d = f.length; e < d; e++) g.push(1 << e), h.push(f[e - 1][0] * f[e - 1][1] + h[e - 1]);
        g.reverse();
        var g = new jz({resolutions: g}), k = b.url, c = Ox(g.Db({extent: [0, 0, c[0], c[1]]}), function (b) {
            if (null !==
                b) {
                var c = b[0], d = b[1];
                b = b[2];
                return k + "TileGroup" + ((d + b * f[c][0] + h[c]) / 256 | 0) + "/" + c + "-" + d + "-" + b + ".jpg"
            }
        });
        Qx.call(this, {
            attributions: b.attributions,
            crossOrigin: b.crossOrigin,
            logo: b.logo,
            tileClass: lz,
            tileGrid: g,
            tileUrlFunction: c
        })
    }

    v(kz, Qx);

    function lz(b, c, d, e, f) {
        Yv.call(this, b, c, d, e, f);
        this.c = {}
    }

    v(lz, Yv);
    lz.prototype.Ta = function (b) {
        var c = m(b) ? ma(b).toString() : "";
        if (c in this.c) return this.c[c];
        b = lz.T.Ta.call(this, b);
        if (2 == this.state) {
            if (256 == b.width && 256 == b.height) return this.c[c] = b;
            var d = Nf(256, 256);
            d.drawImage(b, 0, 0);
            return this.c[c] = d.canvas
        }
        return b
    };

    function mz(b) {
        b = m(b) ? b : {};
        this.d = m(b.initialSize) ? b.initialSize : 256;
        this.b = m(b.maxSize) ? b.maxSize : m(ua) ? ua : 2048;
        this.a = m(b.space) ? b.space : 1;
        this.f = [new nz(this.d, this.a)];
        this.c = this.d;
        this.e = [new nz(this.c, this.a)]
    }

    mz.prototype.add = function (b, c, d, e, f, g) {
        if (c + this.a > this.b || d + this.a > this.b) return null;
        e = oz(this, !1, b, c, d, e, g);
        if (null === e) return null;
        b = oz(this, !0, b, c, d, m(f) ? f : cd, g);
        return {offsetX: e.offsetX, offsetY: e.offsetY, image: e.image, xf: b.image}
    };

    function oz(b, c, d, e, f, g, h) {
        var k = c ? b.e : b.f, n, p, q;
        p = 0;
        for (q = k.length; p < q; ++p) {
            n = k[p];
            n = n.add(d, e, f, g, h);
            if (null !== n) return n;
            null === n && p === q - 1 && (c ? (n = Math.min(2 * b.c, b.b), b.c = n) : (n = Math.min(2 * b.d, b.b), b.d = n), n = new nz(n, b.a), k.push(n), ++q)
        }
    }

    function nz(b, c) {
        this.a = c;
        this.d = [{x: 0, y: 0, width: b, height: b}];
        this.c = {};
        this.b = Df("CANVAS");
        this.b.width = b;
        this.b.height = b;
        this.f = this.b.getContext("2d")
    }

    nz.prototype.get = function (b) {
        return yb(this.c, b, null)
    };
    nz.prototype.add = function (b, c, d, e, f) {
        var g, h, k;
        h = 0;
        for (k = this.d.length; h < k; ++h) if (g = this.d[h], g.width >= c + this.a && g.height >= d + this.a) return k = {
            offsetX: g.x + this.a,
            offsetY: g.y + this.a,
            image: this.b
        }, this.c[b] = k, e.call(f, this.f, g.x + this.a, g.y + this.a), b = h, c = c + this.a, d = d + this.a, f = e = void 0, g.width - c > g.height - d ? (e = {
            x: g.x + c,
            y: g.y,
            width: g.width - c,
            height: g.height
        }, f = {x: g.x, y: g.y + d, width: c, height: g.height - d}, pz(this, b, e, f)) : (e = {
            x: g.x + c,
            y: g.y,
            width: g.width - c,
            height: d
        }, f = {
            x: g.x, y: g.y + d, width: g.width, height: g.height -
                d
        }, pz(this, b, e, f)), k;
        return null
    };

    function pz(b, c, d, e) {
        c = [c, 1];
        0 < d.width && 0 < d.height && c.push(d);
        0 < e.width && 0 < e.height && c.push(e);
        b.d.splice.apply(b.d, c)
    };

    function qz(b) {
        this.q = this.c = this.f = null;
        this.n = m(b.fill) ? b.fill : null;
        this.N = [0, 0];
        this.a = b.points;
        this.b = m(b.radius) ? b.radius : b.radius1;
        this.e = m(b.radius2) ? b.radius2 : this.b;
        this.g = m(b.angle) ? b.angle : 0;
        this.d = m(b.stroke) ? b.stroke : null;
        this.H = this.S = this.D = null;
        var c = b.atlasManager, d = "", e = "", f = 0, g = null, h, k = 0;
        null !== this.d && (h = sg(this.d.a), k = this.d.d, m(k) || (k = 1), g = this.d.b, Yf || (g = null), e = this.d.f, m(e) || (e = "round"), d = this.d.c, m(d) || (d = "round"), f = this.d.e, m(f) || (f = 10));
        var n = 2 * (this.b + k) + 1, d = {
            strokeStyle: h,
            Tc: k, size: n, lineCap: d, lineDash: g, lineJoin: e, miterLimit: f
        };
        if (m(c)) {
            var n = Math.round(n), e = null === this.n, p;
            e && (p = ra(this.Sf, this, d));
            f = this.xb();
            p = c.add(f, n, n, ra(this.Tf, this, d), p);
            this.c = p.image;
            this.N = [p.offsetX, p.offsetY];
            c = p.image.width;
            this.q = e ? p.xf : this.c
        } else this.c = Df("CANVAS"), this.c.height = n, this.c.width = n, c = n = this.c.width, p = this.c.getContext("2d"), this.Tf(d, p, 0, 0), null === this.n ? (p = this.q = Df("CANVAS"), p.height = d.size, p.width = d.size, p = p.getContext("2d"), this.Sf(d, p, 0, 0)) : this.q = this.c;
        this.D =
            [n / 2, n / 2];
        this.S = [n, n];
        this.H = [c, c];
        vj.call(this, {
            opacity: 1,
            rotateWithView: !1,
            rotation: m(b.rotation) ? b.rotation : 0,
            scale: 1,
            snapToPixel: m(b.snapToPixel) ? b.snapToPixel : !0
        })
    }

    v(qz, vj);
    l = qz.prototype;
    l.wb = function () {
        return this.D
    };
    l.Gk = function () {
        return this.g
    };
    l.Hk = function () {
        return this.n
    };
    l.Id = function () {
        return this.q
    };
    l.Bb = function () {
        return this.c
    };
    l.jd = function () {
        return this.H
    };
    l.Oc = function () {
        return 2
    };
    l.Cb = function () {
        return this.N
    };
    l.Ik = function () {
        return this.a
    };
    l.Jk = function () {
        return this.b
    };
    l.Zh = function () {
        return this.e
    };
    l.gb = function () {
        return this.S
    };
    l.Kk = function () {
        return this.d
    };
    l.we = ca;
    l.load = ca;
    l.Oe = ca;
    l.Tf = function (b, c, d, e) {
        var f;
        c.setTransform(1, 0, 0, 1, 0, 0);
        c.translate(d, e);
        c.beginPath();
        this.e !== this.b && (this.a *= 2);
        for (d = 0; d <= this.a; d++) e = 2 * d * Math.PI / this.a - Math.PI / 2 + this.g, f = 0 === d % 2 ? this.b : this.e, c.lineTo(b.size / 2 + f * Math.cos(e), b.size / 2 + f * Math.sin(e));
        null !== this.n && (c.fillStyle = sg(this.n.a), c.fill());
        null !== this.d && (c.strokeStyle = b.strokeStyle, c.lineWidth = b.Tc, null === b.lineDash || c.setLineDash(b.lineDash), c.lineCap = b.lineCap, c.lineJoin = b.lineJoin, c.miterLimit = b.miterLimit, c.stroke());
        c.closePath()
    };
    l.Sf = function (b, c, d, e) {
        c.setTransform(1, 0, 0, 1, 0, 0);
        c.translate(d, e);
        c.beginPath();
        this.e !== this.b && (this.a *= 2);
        var f;
        for (d = 0; d <= this.a; d++) f = 2 * d * Math.PI / this.a - Math.PI / 2 + this.g, e = 0 === d % 2 ? this.b : this.e, c.lineTo(b.size / 2 + e * Math.cos(f), b.size / 2 + e * Math.sin(f));
        c.fillStyle = ll;
        c.fill();
        null !== this.d && (c.strokeStyle = b.strokeStyle, c.lineWidth = b.Tc, null === b.lineDash || c.setLineDash(b.lineDash), c.stroke());
        c.closePath()
    };
    l.xb = function () {
        var b = null === this.d ? "-" : this.d.xb(), c = null === this.n ? "-" : this.n.xb();
        if (null === this.f || b != this.f[1] || c != this.f[2] || this.b != this.f[3] || this.e != this.f[4] || this.g != this.f[5] || this.a != this.f[6]) this.f = ["r" + b + c + (m(this.b) ? this.b.toString() : "-") + (m(this.e) ? this.e.toString() : "-") + (m(this.g) ? this.g.toString() : "-") + (m(this.a) ? this.a.toString() : "-"), b, c, this.b, this.e, this.g, this.a];
        return this.f[0]
    };
    t("ol.animation.bounce", function (b) {
        var c = b.resolution, d = m(b.start) ? b.start : ta(), e = m(b.duration) ? b.duration : 1E3,
            f = m(b.easing) ? b.easing : cf;
        return function (b, h) {
            if (h.time < d) return h.animate = !0, h.viewHints[0] += 1, !0;
            if (h.time < d + e) {
                var k = f((h.time - d) / e), n = c - h.viewState.resolution;
                h.animate = !0;
                h.viewState.resolution += k * n;
                h.viewHints[0] += 1;
                return !0
            }
            return !1
        }
    }, OPENLAYERS);
    t("ol.animation.pan", df, OPENLAYERS);
    t("ol.animation.rotate", ef, OPENLAYERS);
    t("ol.animation.zoom", ff, OPENLAYERS);
    t("ol.Attribution", pf, OPENLAYERS);
    pf.prototype.getHTML = pf.prototype.b;
    kg.prototype.element = kg.prototype.element;
    t("ol.Collection", lg, OPENLAYERS);
    lg.prototype.clear = lg.prototype.clear;
    lg.prototype.extend = lg.prototype.xe;
    lg.prototype.forEach = lg.prototype.forEach;
    lg.prototype.getArray = lg.prototype.hj;
    lg.prototype.item = lg.prototype.item;
    lg.prototype.getLength = lg.prototype.Ib;
    lg.prototype.insertAt = lg.prototype.yd;
    lg.prototype.pop = lg.prototype.pop;
    lg.prototype.push = lg.prototype.push;
    lg.prototype.remove = lg.prototype.remove;
    lg.prototype.removeAt = lg.prototype.Le;
    lg.prototype.setAt = lg.prototype.Ul;
    t("ol.coordinate.add", vd, OPENLAYERS);
    t("ol.coordinate.createStringXY", function (b) {
        return function (c) {
            return Cd(c, b)
        }
    }, OPENLAYERS);
    t("ol.coordinate.format", yd, OPENLAYERS);
    t("ol.coordinate.rotate", Ad, OPENLAYERS);
    t("ol.coordinate.toStringHDMS", function (b) {
        return m(b) ? xd(b[1], "NS") + " " + xd(b[0], "EW") : ""
    }, OPENLAYERS);
    t("ol.coordinate.toStringXY", Cd, OPENLAYERS);
    t("ol.DeviceOrientation", np, OPENLAYERS);
    np.prototype.getAlpha = np.prototype.f;
    np.prototype.getBeta = np.prototype.e;
    np.prototype.getGamma = np.prototype.g;
    np.prototype.getHeading = np.prototype.i;
    np.prototype.getTracking = np.prototype.c;
    np.prototype.setTracking = np.prototype.b;
    t("ol.easing.easeIn", function (b) {
        return Math.pow(b, 3)
    }, OPENLAYERS);
    t("ol.easing.easeOut", $e, OPENLAYERS);
    t("ol.easing.inAndOut", af, OPENLAYERS);
    t("ol.easing.linear", bf, OPENLAYERS);
    t("ol.easing.upAndDown", cf, OPENLAYERS);
    t("ol.extent.boundingExtent", Rd, OPENLAYERS);
    t("ol.extent.buffer", Wd, OPENLAYERS);
    t("ol.extent.containsCoordinate", function (b, c) {
        return $d(b, c[0], c[1])
    }, OPENLAYERS);
    t("ol.extent.containsExtent", Zd, OPENLAYERS);
    t("ol.extent.containsXY", $d, OPENLAYERS);
    t("ol.extent.createEmpty", Sd, OPENLAYERS);
    t("ol.extent.equals", ce, OPENLAYERS);
    t("ol.extent.extend", de, OPENLAYERS);
    t("ol.extent.getBottomLeft", ge, OPENLAYERS);
    t("ol.extent.getBottomRight", he, OPENLAYERS);
    t("ol.extent.getCenter", ke, OPENLAYERS);
    t("ol.extent.getHeight", ne, OPENLAYERS);
    t("ol.extent.getIntersection", oe, OPENLAYERS);
    t("ol.extent.getSize", function (b) {
        return [b[2] - b[0], b[3] - b[1]]
    }, OPENLAYERS);
    t("ol.extent.getTopLeft", je, OPENLAYERS);
    t("ol.extent.getTopRight", ie, OPENLAYERS);
    t("ol.extent.getWidth", qe, OPENLAYERS);
    t("ol.extent.intersects", pe, OPENLAYERS);
    t("ol.extent.isEmpty", re, OPENLAYERS);
    t("ol.extent.applyTransform", te, OPENLAYERS);
    t("ol.Feature", N, OPENLAYERS);
    N.prototype.clone = N.prototype.clone;
    N.prototype.getGeometry = N.prototype.R;
    N.prototype.getId = N.prototype.Hh;
    N.prototype.getGeometryName = N.prototype.Gh;
    N.prototype.getStyle = N.prototype.oj;
    N.prototype.getStyleFunction = N.prototype.pj;
    N.prototype.setGeometry = N.prototype.Sa;
    N.prototype.setStyle = N.prototype.i;
    N.prototype.setId = N.prototype.c;
    N.prototype.setGeometryName = N.prototype.f;
    t("ol.FeatureOverlay", pp, OPENLAYERS);
    pp.prototype.addFeature = pp.prototype.Df;
    pp.prototype.getFeatures = pp.prototype.ij;
    pp.prototype.getMap = pp.prototype.jj;
    pp.prototype.removeFeature = pp.prototype.Dd;
    pp.prototype.setFeatures = pp.prototype.Sc;
    pp.prototype.setMap = pp.prototype.setMap;
    pp.prototype.setStyle = pp.prototype.Ff;
    pp.prototype.getStyle = pp.prototype.kj;
    pp.prototype.getStyleFunction = pp.prototype.lj;
    t("ol.Geolocation", V, OPENLAYERS);
    V.prototype.getAccuracy = V.prototype.mf;
    V.prototype.getAccuracyGeometry = V.prototype.o;
    V.prototype.getAltitude = V.prototype.p;
    V.prototype.getAltitudeAccuracy = V.prototype.r;
    V.prototype.getHeading = V.prototype.H;
    V.prototype.getPosition = V.prototype.N;
    V.prototype.getProjection = V.prototype.g;
    V.prototype.getSpeed = V.prototype.D;
    V.prototype.getTracking = V.prototype.i;
    V.prototype.getTrackingOptions = V.prototype.e;
    V.prototype.setProjection = V.prototype.j;
    V.prototype.setTracking = V.prototype.b;
    V.prototype.setTrackingOptions = V.prototype.q;
    t("ol.Graticule", Sv, OPENLAYERS);
    Sv.prototype.getMap = Sv.prototype.sj;
    Sv.prototype.getMeridians = Sv.prototype.Rh;
    Sv.prototype.getParallels = Sv.prototype.Wh;
    Sv.prototype.setMap = Sv.prototype.setMap;
    t("ol.has.DEVICE_PIXEL_RATIO", Wf, OPENLAYERS);
    t("ol.has.CANVAS", Zf, OPENLAYERS);
    t("ol.has.DEVICE_ORIENTATION", $f, OPENLAYERS);
    t("ol.has.GEOLOCATION", ag, OPENLAYERS);
    t("ol.has.TOUCH", bg, OPENLAYERS);
    t("ol.has.WEBGL", Vf, OPENLAYERS);
    Xv.prototype.getImage = Xv.prototype.a;
    Yv.prototype.getImage = Yv.prototype.Ta;
    t("ol.Kinetic", Kj, OPENLAYERS);
    t("ol.loadingstrategy.all", function () {
        return [[-Infinity, -Infinity, Infinity, Infinity]]
    }, OPENLAYERS);
    t("ol.loadingstrategy.bbox", xx, OPENLAYERS);
    t("ol.loadingstrategy.createTile", function (b) {
        return function (c, d) {
            var e = ac(b.a, d, 0), f = Yi(b, c, e), g = [], e = [e, 0, 0];
            for (e[1] = f.a; e[1] <= f.c; ++e[1]) for (e[2] = f.b; e[2] <= f.d; ++e[2]) g.push(Xi(b, e));
            return g
        }
    }, OPENLAYERS);
    t("ol.Map", K, OPENLAYERS);
    K.prototype.addControl = K.prototype.eh;
    K.prototype.addInteraction = K.prototype.fh;
    K.prototype.addLayer = K.prototype.bf;
    K.prototype.addOverlay = K.prototype.cf;
    K.prototype.beforeRender = K.prototype.La;
    K.prototype.forEachFeatureAtPixel = K.prototype.oe;
    K.prototype.forEachLayerAtPixel = K.prototype.wj;
    K.prototype.hasFeatureAtPixel = K.prototype.Oi;
    K.prototype.getEventCoordinate = K.prototype.Bh;
    K.prototype.getEventPixel = K.prototype.gd;
    K.prototype.getTarget = K.prototype.rc;
    K.prototype.getTargetElement = K.prototype.te;
    K.prototype.getCoordinateFromPixel = K.prototype.ra;
    K.prototype.getControls = K.prototype.zh;
    K.prototype.getOverlays = K.prototype.Vh;
    K.prototype.getInteractions = K.prototype.Ih;
    K.prototype.getLayerGroup = K.prototype.Ub;
    K.prototype.getLayers = K.prototype.ea;
    K.prototype.getPixelFromCoordinate = K.prototype.e;
    K.prototype.getSize = K.prototype.f;
    K.prototype.getView = K.prototype.a;
    K.prototype.getViewport = K.prototype.ei;
    K.prototype.renderSync = K.prototype.Rl;
    K.prototype.render = K.prototype.render;
    K.prototype.removeControl = K.prototype.Ll;
    K.prototype.removeInteraction = K.prototype.Ml;
    K.prototype.removeLayer = K.prototype.Nl;
    K.prototype.removeOverlay = K.prototype.Ol;
    K.prototype.setLayerGroup = K.prototype.rg;
    K.prototype.setSize = K.prototype.S;
    K.prototype.setTarget = K.prototype.ia;
    K.prototype.setView = K.prototype.Fa;
    K.prototype.updateSize = K.prototype.q;
    wi.prototype.originalEvent = wi.prototype.originalEvent;
    wi.prototype.pixel = wi.prototype.pixel;
    wi.prototype.coordinate = wi.prototype.coordinate;
    wi.prototype.dragging = wi.prototype.dragging;
    wi.prototype.preventDefault = wi.prototype.preventDefault;
    wi.prototype.stopPropagation = wi.prototype.pb;
    Tg.prototype.map = Tg.prototype.map;
    Tg.prototype.frameState = Tg.prototype.frameState;
    md.prototype.key = md.prototype.key;
    md.prototype.oldValue = md.prototype.oldValue;
    nd.prototype.transform = nd.prototype.transform;
    t("ol.Object", qd, OPENLAYERS);
    qd.prototype.bindTo = qd.prototype.K;
    qd.prototype.get = qd.prototype.get;
    qd.prototype.getKeys = qd.prototype.G;
    qd.prototype.getProperties = qd.prototype.I;
    qd.prototype.set = qd.prototype.set;
    qd.prototype.setProperties = qd.prototype.C;
    qd.prototype.unbind = qd.prototype.L;
    qd.prototype.unbindAll = qd.prototype.M;
    t("ol.Observable", kd, OPENLAYERS);
    t("ol.Observable.unByKey", ld, OPENLAYERS);
    kd.prototype.changed = kd.prototype.l;
    kd.prototype.getRevision = kd.prototype.u;
    kd.prototype.on = kd.prototype.s;
    kd.prototype.once = kd.prototype.v;
    kd.prototype.un = kd.prototype.t;
    kd.prototype.unByKey = kd.prototype.A;
    t("ol.WEBGL_MAX_TEXTURE_SIZE", ua, OPENLAYERS);
    t("ol.inherits", v, OPENLAYERS);
    t("ol.Overlay", L, OPENLAYERS);
    L.prototype.getElement = L.prototype.b;
    L.prototype.getMap = L.prototype.c;
    L.prototype.getOffset = L.prototype.i;
    L.prototype.getPosition = L.prototype.q;
    L.prototype.getPositioning = L.prototype.j;
    L.prototype.setElement = L.prototype.Me;
    L.prototype.setMap = L.prototype.setMap;
    L.prototype.setOffset = L.prototype.o;
    L.prototype.setPosition = L.prototype.e;
    L.prototype.setPositioning = L.prototype.p;
    Pi.prototype.getTileCoord = Pi.prototype.e;
    t("ol.View", B, OPENLAYERS);
    B.prototype.constrainCenter = B.prototype.i;
    B.prototype.constrainResolution = B.prototype.constrainResolution;
    B.prototype.constrainRotation = B.prototype.constrainRotation;
    B.prototype.getCenter = B.prototype.b;
    B.prototype.calculateExtent = B.prototype.g;
    B.prototype.getProjection = B.prototype.N;
    B.prototype.getResolution = B.prototype.a;
    B.prototype.getResolutionForExtent = B.prototype.j;
    B.prototype.getRotation = B.prototype.c;
    B.prototype.getZoom = B.prototype.hi;
    B.prototype.fitExtent = B.prototype.ne;
    B.prototype.fitGeometry = B.prototype.uh;
    B.prototype.centerOn = B.prototype.mh;
    B.prototype.rotate = B.prototype.rotate;
    B.prototype.setCenter = B.prototype.Ha;
    B.prototype.setResolution = B.prototype.f;
    B.prototype.setRotation = B.prototype.r;
    B.prototype.setZoom = B.prototype.S;
    t("ol.xml.getAllTextContent", Mp, OPENLAYERS);
    t("ol.xml.parse", fq, OPENLAYERS);
    t("ol.webgl.Context", Rn, OPENLAYERS);
    Rn.prototype.getGL = Rn.prototype.al;
    Rn.prototype.getHitDetectionFramebuffer = Rn.prototype.qe;
    Rn.prototype.useProgram = Rn.prototype.Pd;
    t("ol.tilegrid.TileGrid", Vi, OPENLAYERS);
    Vi.prototype.getMaxZoom = Vi.prototype.ld;
    Vi.prototype.getMinZoom = Vi.prototype.od;
    Vi.prototype.getOrigin = Vi.prototype.Lb;
    Vi.prototype.getResolution = Vi.prototype.na;
    Vi.prototype.getResolutions = Vi.prototype.Od;
    Vi.prototype.getTileCoordForCoordAndResolution = Vi.prototype.Wb;
    Vi.prototype.getTileCoordForCoordAndZ = Vi.prototype.Mc;
    Vi.prototype.getTileSize = Vi.prototype.ua;
    t("ol.tilegrid.WMTS", fz, OPENLAYERS);
    fz.prototype.getMatrixIds = fz.prototype.g;
    t("ol.tilegrid.WMTS.createFromCapabilitiesMatrixSet", gz, OPENLAYERS);
    t("ol.tilegrid.XYZ", Sx, OPENLAYERS);
    t("ol.tilegrid.Zoomify", jz, OPENLAYERS);
    t("ol.style.AtlasManager", mz, OPENLAYERS);
    t("ol.style.Circle", ql, OPENLAYERS);
    ql.prototype.getAnchor = ql.prototype.wb;
    ql.prototype.getFill = ql.prototype.Ck;
    ql.prototype.getImage = ql.prototype.Bb;
    ql.prototype.getOrigin = ql.prototype.Cb;
    ql.prototype.getRadius = ql.prototype.Dk;
    ql.prototype.getSize = ql.prototype.gb;
    ql.prototype.getStroke = ql.prototype.Ek;
    t("ol.style.Fill", pl, OPENLAYERS);
    pl.prototype.getColor = pl.prototype.b;
    pl.prototype.setColor = pl.prototype.c;
    t("ol.style.Icon", wj, OPENLAYERS);
    wj.prototype.getAnchor = wj.prototype.wb;
    wj.prototype.getImage = wj.prototype.Bb;
    wj.prototype.getOrigin = wj.prototype.Cb;
    wj.prototype.getSrc = wj.prototype.Fk;
    wj.prototype.getSize = wj.prototype.gb;
    t("ol.style.Image", vj, OPENLAYERS);
    vj.prototype.getOpacity = vj.prototype.Jd;
    vj.prototype.getRotateWithView = vj.prototype.qd;
    vj.prototype.getRotation = vj.prototype.Kd;
    vj.prototype.getScale = vj.prototype.Ld;
    vj.prototype.getSnapToPixel = vj.prototype.rd;
    vj.prototype.getImage = vj.prototype.Bb;
    vj.prototype.setRotation = vj.prototype.Md;
    vj.prototype.setScale = vj.prototype.Nd;
    t("ol.style.RegularShape", qz, OPENLAYERS);
    qz.prototype.getAnchor = qz.prototype.wb;
    qz.prototype.getAngle = qz.prototype.Gk;
    qz.prototype.getFill = qz.prototype.Hk;
    qz.prototype.getImage = qz.prototype.Bb;
    qz.prototype.getOrigin = qz.prototype.Cb;
    qz.prototype.getPoints = qz.prototype.Ik;
    qz.prototype.getRadius = qz.prototype.Jk;
    qz.prototype.getRadius2 = qz.prototype.Zh;
    qz.prototype.getSize = qz.prototype.gb;
    qz.prototype.getStroke = qz.prototype.Kk;
    t("ol.style.Stroke", kl, OPENLAYERS);
    kl.prototype.getColor = kl.prototype.Lk;
    kl.prototype.getLineCap = kl.prototype.Lh;
    kl.prototype.getLineDash = kl.prototype.Mk;
    kl.prototype.getLineJoin = kl.prototype.Mh;
    kl.prototype.getMiterLimit = kl.prototype.Sh;
    kl.prototype.getWidth = kl.prototype.Nk;
    kl.prototype.setColor = kl.prototype.Ok;
    kl.prototype.setLineCap = kl.prototype.Xl;
    kl.prototype.setLineDash = kl.prototype.Pk;
    kl.prototype.setLineJoin = kl.prototype.Yl;
    kl.prototype.setMiterLimit = kl.prototype.Zl;
    kl.prototype.setWidth = kl.prototype.fm;
    t("ol.style.Style", rl, OPENLAYERS);
    rl.prototype.getGeometry = rl.prototype.R;
    rl.prototype.getGeometryFunction = rl.prototype.Fh;
    rl.prototype.getFill = rl.prototype.Qk;
    rl.prototype.getImage = rl.prototype.Rk;
    rl.prototype.getStroke = rl.prototype.Sk;
    rl.prototype.getText = rl.prototype.Tk;
    rl.prototype.getZIndex = rl.prototype.gi;
    rl.prototype.setGeometry = rl.prototype.Uf;
    rl.prototype.setZIndex = rl.prototype.hm;
    t("ol.style.Text", fs, OPENLAYERS);
    fs.prototype.getFont = fs.prototype.Dh;
    fs.prototype.getOffsetX = fs.prototype.Th;
    fs.prototype.getOffsetY = fs.prototype.Uh;
    fs.prototype.getFill = fs.prototype.Uk;
    fs.prototype.getRotation = fs.prototype.Vk;
    fs.prototype.getScale = fs.prototype.Wk;
    fs.prototype.getStroke = fs.prototype.Xk;
    fs.prototype.getText = fs.prototype.Yk;
    fs.prototype.getTextAlign = fs.prototype.ai;
    fs.prototype.getTextBaseline = fs.prototype.bi;
    fs.prototype.setFont = fs.prototype.Wl;
    fs.prototype.setFill = fs.prototype.Vl;
    fs.prototype.setRotation = fs.prototype.Zk;
    fs.prototype.setScale = fs.prototype.$k;
    fs.prototype.setStroke = fs.prototype.bm;
    fs.prototype.setText = fs.prototype.cm;
    fs.prototype.setTextAlign = fs.prototype.dm;
    fs.prototype.setTextBaseline = fs.prototype.em;
    t("ol.Sphere", ue, OPENLAYERS);
    ue.prototype.geodesicArea = ue.prototype.d;
    ue.prototype.haversineDistance = ue.prototype.a;
    t("ol.source.BingMaps", Tx, OPENLAYERS);
    t("ol.source.BingMaps.TOS_ATTRIBUTION", Ux, OPENLAYERS);
    t("ol.source.Cluster", Vx, OPENLAYERS);
    Vx.prototype.getSource = Vx.prototype.H;
    Y.prototype.readFeatures = Y.prototype.a;
    t("ol.source.GeoJSON", vy, OPENLAYERS);
    t("ol.source.GPX", wy, OPENLAYERS);
    t("ol.source.IGC", xy, OPENLAYERS);
    t("ol.source.ImageCanvas", en, OPENLAYERS);
    t("ol.source.ImageMapGuide", yy, OPENLAYERS);
    yy.prototype.getParams = yy.prototype.Yj;
    yy.prototype.getImageLoadFunction = yy.prototype.Xj;
    yy.prototype.updateParams = yy.prototype.ak;
    yy.prototype.setImageLoadFunction = yy.prototype.$j;
    t("ol.source.Image", bn, OPENLAYERS);
    t("ol.source.ImageStatic", zy, OPENLAYERS);
    t("ol.source.ImageVector", sn, OPENLAYERS);
    sn.prototype.getSource = sn.prototype.bk;
    sn.prototype.getStyle = sn.prototype.ck;
    sn.prototype.getStyleFunction = sn.prototype.dk;
    sn.prototype.setStyle = sn.prototype.Mf;
    t("ol.source.ImageWMS", Ay, OPENLAYERS);
    Ay.prototype.getGetFeatureInfoUrl = Ay.prototype.gk;
    Ay.prototype.getParams = Ay.prototype.ik;
    Ay.prototype.getImageLoadFunction = Ay.prototype.hk;
    Ay.prototype.getUrl = Ay.prototype.jk;
    Ay.prototype.setImageLoadFunction = Ay.prototype.kk;
    Ay.prototype.setUrl = Ay.prototype.lk;
    Ay.prototype.updateParams = Ay.prototype.mk;
    t("ol.source.KML", Ey, OPENLAYERS);
    t("ol.source.MapQuest", Iy, OPENLAYERS);
    Iy.prototype.getLayer = Iy.prototype.e;
    t("ol.source.OSM", Gy, OPENLAYERS);
    t("ol.source.OSM.ATTRIBUTION", Hy, OPENLAYERS);
    t("ol.source.OSMXML", Ly, OPENLAYERS);
    t("ol.source.ServerVector", $, OPENLAYERS);
    $.prototype.clear = $.prototype.clear;
    $.prototype.readFeatures = $.prototype.a;
    t("ol.source.Source", Ki, OPENLAYERS);
    Ki.prototype.getAttributions = Ki.prototype.Y;
    Ki.prototype.getLogo = Ki.prototype.X;
    Ki.prototype.getProjection = Ki.prototype.Z;
    Ki.prototype.getState = Ki.prototype.$;
    t("ol.source.Stamen", Oy, OPENLAYERS);
    t("ol.source.StaticVector", Z, OPENLAYERS);
    t("ol.source.TileArcGISRest", Qy, OPENLAYERS);
    Qy.prototype.getParams = Qy.prototype.nk;
    Qy.prototype.getUrls = Qy.prototype.ok;
    Qy.prototype.setUrl = Qy.prototype.pk;
    Qy.prototype.setUrls = Qy.prototype.Nf;
    Qy.prototype.updateParams = Qy.prototype.rk;
    t("ol.source.TileDebug", Sy, OPENLAYERS);
    t("ol.source.TileImage", Qx, OPENLAYERS);
    Qx.prototype.getTileLoadFunction = Qx.prototype.bb;
    Qx.prototype.getTileUrlFunction = Qx.prototype.cb;
    Qx.prototype.setTileLoadFunction = Qx.prototype.jb;
    Qx.prototype.setTileUrlFunction = Qx.prototype.ta;
    t("ol.source.TileJSON", Ty, OPENLAYERS);
    t("ol.source.Tile", dj, OPENLAYERS);
    dj.prototype.getTileGrid = dj.prototype.xa;
    gj.prototype.tile = gj.prototype.tile;
    t("ol.source.TileUTFGrid", Uy, OPENLAYERS);
    Uy.prototype.getTemplate = Uy.prototype.$h;
    Uy.prototype.forDataAtCoordinateAndResolution = Uy.prototype.vh;
    t("ol.source.TileVector", Zy, OPENLAYERS);
    Zy.prototype.getFeatures = Zy.prototype.Aa;
    Zy.prototype.getFeaturesAtCoordinateAndResolution = Zy.prototype.Ch;
    t("ol.source.TileWMS", az, OPENLAYERS);
    az.prototype.getGetFeatureInfoUrl = az.prototype.uk;
    az.prototype.getParams = az.prototype.vk;
    az.prototype.getUrls = az.prototype.wk;
    az.prototype.setUrl = az.prototype.xk;
    az.prototype.setUrls = az.prototype.Of;
    az.prototype.updateParams = az.prototype.zk;
    t("ol.source.TopoJSON", ez, OPENLAYERS);
    t("ol.source.Vector", mn, OPENLAYERS);
    mn.prototype.addFeature = mn.prototype.Va;
    mn.prototype.addFeatures = mn.prototype.Ga;
    mn.prototype.clear = mn.prototype.clear;
    mn.prototype.forEachFeature = mn.prototype.$a;
    mn.prototype.forEachFeatureInExtent = mn.prototype.wa;
    mn.prototype.forEachFeatureIntersectingExtent = mn.prototype.Ma;
    mn.prototype.getFeatures = mn.prototype.Aa;
    mn.prototype.getFeaturesAtCoordinate = mn.prototype.Oa;
    mn.prototype.getClosestFeatureToCoordinate = mn.prototype.ab;
    mn.prototype.getExtent = mn.prototype.J;
    mn.prototype.getFeatureById = mn.prototype.Na;
    mn.prototype.removeFeature = mn.prototype.fb;
    pn.prototype.feature = pn.prototype.feature;
    t("ol.source.WMTS", hz, OPENLAYERS);
    hz.prototype.getDimensions = hz.prototype.Ah;
    hz.prototype.getFormat = hz.prototype.Eh;
    hz.prototype.getLayer = hz.prototype.Ak;
    hz.prototype.getMatrixSet = hz.prototype.Qh;
    hz.prototype.getStyle = hz.prototype.Bk;
    hz.prototype.getVersion = hz.prototype.di;
    hz.prototype.updateDimensions = hz.prototype.om;
    t("ol.source.WMTS.optionsFromCapabilities", function (b, c) {
        var d = Ta(b.Contents.Layer, function (b) {
            return b.Identifier == c.layer
        }), e, f;
        e = 1 < d.TileMatrixSetLink.length ? Ua(d.TileMatrixSetLink, function (b) {
            return b.TileMatrixSet == c.matrixSet
        }) : m(c.projection) ? Ua(d.TileMatrixSetLink, function (b) {
            return b.TileMatrixSet.SupportedCRS.replace(/urn:ogc:def:crs:(\w+):(.*:)?(\w+)$/, "$1:$3") == c.projection
        }) : 0;
        0 > e && (e = 0);
        f = d.TileMatrixSetLink[e].TileMatrixSet;
        var g = d.Format[0];
        m(c.format) && (g = c.format);
        e = Ua(d.Style, function (b) {
            return m(c.style) ?
                b.Title == c.style : b.isDefault
        });
        0 > e && (e = 0);
        e = d.Style[e].Identifier;
        var h = {};
        m(d.Dimension) && Pa(d.Dimension, function (b) {
            var c = b.Identifier, d = b["default"];
            m(d) || (d = b.values[0]);
            h[c] = d
        });
        var k = Ta(b.Contents.TileMatrixSet, function (b) {
                return b.Identifier == f
            }), n = gz(k),
            k = m(c.projection) ? Ae(c.projection) : Ae(k.SupportedCRS.replace(/urn:ogc:def:crs:(\w+):(.*:)?(\w+)$/, "$1:$3")),
            p = [], q = c.requestEncoding, q = m(q) ? q : "";
        if (b.OperationsMetadata.hasOwnProperty("GetTile") && 0 != q.lastIndexOf("REST", 0)) {
            var d = b.OperationsMetadata.GetTile.DCP.HTTP.Get,
                r = Ta(d[0].Constraint, function (b) {
                    return "GetEncoding" == b.name
                }).AllowedValues.Value;
            0 < r.length && Va(r, "KVP") && (q = "KVP", p.push(d[0].href))
        } else q = "REST", Pa(d.ResourceURL, function (b) {
            "tile" == b.resourceType && (g = b.format, p.push(b.template))
        });
        return {
            urls: p,
            layer: c.layer,
            matrixSet: f,
            format: g,
            projection: k,
            requestEncoding: q,
            tileGrid: n,
            style: e,
            dimensions: h
        }
    }, OPENLAYERS);
    t("ol.source.XYZ", Fy, OPENLAYERS);
    Fy.prototype.setTileUrlFunction = Fy.prototype.ta;
    Fy.prototype.setUrl = Fy.prototype.b;
    t("ol.source.Zoomify", kz, OPENLAYERS);
    Zk.prototype.vectorContext = Zk.prototype.vectorContext;
    Zk.prototype.frameState = Zk.prototype.frameState;
    Zk.prototype.context = Zk.prototype.context;
    Zk.prototype.glContext = Zk.prototype.glContext;
    lo.prototype.drawAsync = lo.prototype.jc;
    lo.prototype.drawCircleGeometry = lo.prototype.kc;
    lo.prototype.drawFeature = lo.prototype.me;
    lo.prototype.drawGeometryCollectionGeometry = lo.prototype.ed;
    lo.prototype.drawPointGeometry = lo.prototype.ub;
    lo.prototype.drawLineStringGeometry = lo.prototype.Eb;
    lo.prototype.drawMultiLineStringGeometry = lo.prototype.lc;
    lo.prototype.drawMultiPointGeometry = lo.prototype.tb;
    lo.prototype.drawMultiPolygonGeometry = lo.prototype.mc;
    lo.prototype.drawPolygonGeometry = lo.prototype.Rb;
    lo.prototype.drawText = lo.prototype.vb;
    lo.prototype.setFillStrokeStyle = lo.prototype.Ba;
    lo.prototype.setImageStyle = lo.prototype.ib;
    lo.prototype.setTextStyle = lo.prototype.Ca;
    Tl.prototype.drawAsync = Tl.prototype.jc;
    Tl.prototype.drawCircleGeometry = Tl.prototype.kc;
    Tl.prototype.drawFeature = Tl.prototype.me;
    Tl.prototype.drawPointGeometry = Tl.prototype.ub;
    Tl.prototype.drawMultiPointGeometry = Tl.prototype.tb;
    Tl.prototype.drawLineStringGeometry = Tl.prototype.Eb;
    Tl.prototype.drawMultiLineStringGeometry = Tl.prototype.lc;
    Tl.prototype.drawPolygonGeometry = Tl.prototype.Rb;
    Tl.prototype.drawMultiPolygonGeometry = Tl.prototype.mc;
    Tl.prototype.setFillStrokeStyle = Tl.prototype.Ba;
    Tl.prototype.setImageStyle = Tl.prototype.ib;
    Tl.prototype.setTextStyle = Tl.prototype.Ca;
    t("ol.proj.common.add", Sl, OPENLAYERS);
    t("ol.proj.METERS_PER_UNIT", we, OPENLAYERS);
    t("ol.proj.Projection", xe, OPENLAYERS);
    xe.prototype.getCode = xe.prototype.yh;
    xe.prototype.getExtent = xe.prototype.J;
    xe.prototype.getUnits = xe.prototype.Rj;
    xe.prototype.getMetersPerUnit = xe.prototype.md;
    xe.prototype.getWorldExtent = xe.prototype.fi;
    xe.prototype.isGlobal = xe.prototype.Si;
    xe.prototype.setExtent = xe.prototype.Sj;
    xe.prototype.setWorldExtent = xe.prototype.gm;
    t("ol.proj.addEquivalentProjections", De, OPENLAYERS);
    t("ol.proj.addProjection", Me, OPENLAYERS);
    t("ol.proj.addCoordinateTransforms", Pe, OPENLAYERS);
    t("ol.proj.get", Ae, OPENLAYERS);
    t("ol.proj.getTransform", Se, OPENLAYERS);
    t("ol.proj.transform", function (b, c, d) {
        return Se(c, d)(b, void 0, b.length)
    }, OPENLAYERS);
    t("ol.proj.transformExtent", Ue, OPENLAYERS);
    t("ol.layer.Heatmap", X, OPENLAYERS);
    X.prototype.getBlur = X.prototype.Ea;
    X.prototype.getGradient = X.prototype.Fa;
    X.prototype.getRadius = X.prototype.hc;
    X.prototype.setBlur = X.prototype.wc;
    X.prototype.setGradient = X.prototype.xc;
    X.prototype.setRadius = X.prototype.ic;
    t("ol.layer.Image", H, OPENLAYERS);
    H.prototype.getSource = H.prototype.a;
    t("ol.layer.Layer", E, OPENLAYERS);
    E.prototype.getSource = E.prototype.a;
    E.prototype.setSource = E.prototype.fa;
    t("ol.layer.Base", C, OPENLAYERS);
    C.prototype.getBrightness = C.prototype.c;
    C.prototype.getContrast = C.prototype.f;
    C.prototype.getHue = C.prototype.e;
    C.prototype.getExtent = C.prototype.J;
    C.prototype.getMaxResolution = C.prototype.g;
    C.prototype.getMinResolution = C.prototype.i;
    C.prototype.getOpacity = C.prototype.q;
    C.prototype.getSaturation = C.prototype.j;
    C.prototype.getVisible = C.prototype.b;
    C.prototype.setBrightness = C.prototype.D;
    C.prototype.setContrast = C.prototype.H;
    C.prototype.setHue = C.prototype.N;
    C.prototype.setExtent = C.prototype.o;
    C.prototype.setMaxResolution = C.prototype.S;
    C.prototype.setMinResolution = C.prototype.W;
    C.prototype.setOpacity = C.prototype.p;
    C.prototype.setSaturation = C.prototype.ca;
    C.prototype.setVisible = C.prototype.da;
    t("ol.layer.Group", G, OPENLAYERS);
    G.prototype.getLayers = G.prototype.$b;
    G.prototype.setLayers = G.prototype.r;
    t("ol.layer.Tile", I, OPENLAYERS);
    I.prototype.getPreload = I.prototype.r;
    I.prototype.getSource = I.prototype.a;
    I.prototype.setPreload = I.prototype.ia;
    I.prototype.getUseInterimTilesOnError = I.prototype.ea;
    I.prototype.setUseInterimTilesOnError = I.prototype.ka;
    t("ol.layer.Vector", J, OPENLAYERS);
    J.prototype.getSource = J.prototype.a;
    J.prototype.getStyle = J.prototype.af;
    J.prototype.getStyleFunction = J.prototype.df;
    J.prototype.setStyle = J.prototype.ka;
    t("ol.interaction.DoubleClickZoom", Rj, OPENLAYERS);
    t("ol.interaction.DoubleClickZoom.handleEvent", Sj, OPENLAYERS);
    t("ol.interaction.DragAndDrop", Ow, OPENLAYERS);
    t("ol.interaction.DragAndDrop.handleEvent", bd, OPENLAYERS);
    Pw.prototype.features = Pw.prototype.features;
    Pw.prototype.file = Pw.prototype.file;
    Pw.prototype.projection = Pw.prototype.projection;
    cl.prototype.coordinate = cl.prototype.coordinate;
    t("ol.interaction.DragBox", dl, OPENLAYERS);
    dl.prototype.getGeometry = dl.prototype.R;
    t("ol.interaction.DragPan", dk, OPENLAYERS);
    t("ol.interaction.DragRotateAndZoom", Sw, OPENLAYERS);
    t("ol.interaction.DragRotate", hk, OPENLAYERS);
    t("ol.interaction.DragZoom", wl, OPENLAYERS);
    Ww.prototype.feature = Ww.prototype.feature;
    t("ol.interaction.Draw", Xw, OPENLAYERS);
    t("ol.interaction.Draw.handleEvent", Zw, OPENLAYERS);
    Xw.prototype.finishDrawing = Xw.prototype.W;
    t("ol.interaction.Interaction", Nj, OPENLAYERS);
    Nj.prototype.getActive = Nj.prototype.a;
    Nj.prototype.setActive = Nj.prototype.b;
    t("ol.interaction.defaults", Ll, OPENLAYERS);
    t("ol.interaction.KeyboardPan", xl, OPENLAYERS);
    t("ol.interaction.KeyboardPan.handleEvent", yl, OPENLAYERS);
    t("ol.interaction.KeyboardZoom", zl, OPENLAYERS);
    t("ol.interaction.KeyboardZoom.handleEvent", Al, OPENLAYERS);
    t("ol.interaction.Modify", jx, OPENLAYERS);
    t("ol.interaction.Modify.handleEvent", mx, OPENLAYERS);
    t("ol.interaction.MouseWheelZoom", Bl, OPENLAYERS);
    t("ol.interaction.MouseWheelZoom.handleEvent", Cl, OPENLAYERS);
    t("ol.interaction.PinchRotate", Dl, OPENLAYERS);
    t("ol.interaction.PinchZoom", Hl, OPENLAYERS);
    t("ol.interaction.Pointer", ak, OPENLAYERS);
    t("ol.interaction.Pointer.handleEvent", bk, OPENLAYERS);
    sx.prototype.selected = sx.prototype.d;
    sx.prototype.deselected = sx.prototype.a;
    t("ol.interaction.Select", tx, OPENLAYERS);
    tx.prototype.getFeatures = tx.prototype.o;
    t("ol.interaction.Select.handleEvent", ux, OPENLAYERS);
    tx.prototype.setMap = tx.prototype.setMap;
    t("ol.geom.Circle", Fm, OPENLAYERS);
    Fm.prototype.clone = Fm.prototype.clone;
    Fm.prototype.getCenter = Fm.prototype.ye;
    Fm.prototype.getRadius = Fm.prototype.Hf;
    Fm.prototype.getType = Fm.prototype.O;
    Fm.prototype.setCenter = Fm.prototype.Jj;
    Fm.prototype.setCenterAndRadius = Fm.prototype.pg;
    Fm.prototype.setRadius = Fm.prototype.Kj;
    Fm.prototype.transform = Fm.prototype.transform;
    t("ol.geom.Geometry", lk, OPENLAYERS);
    lk.prototype.clone = lk.prototype.clone;
    lk.prototype.getClosestPoint = lk.prototype.f;
    lk.prototype.getExtent = lk.prototype.J;
    lk.prototype.getType = lk.prototype.O;
    lk.prototype.applyTransform = lk.prototype.qa;
    lk.prototype.intersectsExtent = lk.prototype.ja;
    lk.prototype.translate = lk.prototype.Ia;
    lk.prototype.transform = lk.prototype.transform;
    t("ol.geom.GeometryCollection", Hm, OPENLAYERS);
    Hm.prototype.clone = Hm.prototype.clone;
    Hm.prototype.getGeometries = Hm.prototype.nf;
    Hm.prototype.getType = Hm.prototype.O;
    Hm.prototype.intersectsExtent = Hm.prototype.ja;
    Hm.prototype.setGeometries = Hm.prototype.qg;
    Hm.prototype.applyTransform = Hm.prototype.qa;
    Hm.prototype.translate = Hm.prototype.Ia;
    t("ol.geom.LinearRing", Hk, OPENLAYERS);
    Hk.prototype.clone = Hk.prototype.clone;
    Hk.prototype.getArea = Hk.prototype.Nj;
    Hk.prototype.getCoordinates = Hk.prototype.Q;
    Hk.prototype.getType = Hk.prototype.O;
    Hk.prototype.setCoordinates = Hk.prototype.V;
    t("ol.geom.LineString", Om, OPENLAYERS);
    Om.prototype.appendCoordinate = Om.prototype.gh;
    Om.prototype.clone = Om.prototype.clone;
    Om.prototype.getCoordinateAtM = Om.prototype.Lj;
    Om.prototype.getCoordinates = Om.prototype.Q;
    Om.prototype.getLength = Om.prototype.Mj;
    Om.prototype.getType = Om.prototype.O;
    Om.prototype.intersectsExtent = Om.prototype.ja;
    Om.prototype.setCoordinates = Om.prototype.V;
    t("ol.geom.MultiLineString", Qm, OPENLAYERS);
    Qm.prototype.appendLineString = Qm.prototype.hh;
    Qm.prototype.clone = Qm.prototype.clone;
    Qm.prototype.getCoordinateAtM = Qm.prototype.Oj;
    Qm.prototype.getCoordinates = Qm.prototype.Q;
    Qm.prototype.getLineString = Qm.prototype.Nh;
    Qm.prototype.getLineStrings = Qm.prototype.Lc;
    Qm.prototype.getType = Qm.prototype.O;
    Qm.prototype.intersectsExtent = Qm.prototype.ja;
    Qm.prototype.setCoordinates = Qm.prototype.V;
    t("ol.geom.MultiPoint", Tm, OPENLAYERS);
    Tm.prototype.appendPoint = Tm.prototype.jh;
    Tm.prototype.clone = Tm.prototype.clone;
    Tm.prototype.getCoordinates = Tm.prototype.Q;
    Tm.prototype.getPoint = Tm.prototype.Xh;
    Tm.prototype.getPoints = Tm.prototype.Ed;
    Tm.prototype.getType = Tm.prototype.O;
    Tm.prototype.intersectsExtent = Tm.prototype.ja;
    Tm.prototype.setCoordinates = Tm.prototype.V;
    t("ol.geom.MultiPolygon", Um, OPENLAYERS);
    Um.prototype.appendPolygon = Um.prototype.kh;
    Um.prototype.clone = Um.prototype.clone;
    Um.prototype.getArea = Um.prototype.Pj;
    Um.prototype.getCoordinates = Um.prototype.Q;
    Um.prototype.getInteriorPoints = Um.prototype.Kh;
    Um.prototype.getPolygon = Um.prototype.Yh;
    Um.prototype.getPolygons = Um.prototype.pd;
    Um.prototype.getType = Um.prototype.O;
    Um.prototype.intersectsExtent = Um.prototype.ja;
    Um.prototype.setCoordinates = Um.prototype.V;
    t("ol.geom.Point", Jk, OPENLAYERS);
    Jk.prototype.clone = Jk.prototype.clone;
    Jk.prototype.getCoordinates = Jk.prototype.Q;
    Jk.prototype.getType = Jk.prototype.O;
    Jk.prototype.intersectsExtent = Jk.prototype.ja;
    Jk.prototype.setCoordinates = Jk.prototype.V;
    t("ol.geom.Polygon", F, OPENLAYERS);
    F.prototype.appendLinearRing = F.prototype.ih;
    F.prototype.clone = F.prototype.clone;
    F.prototype.getArea = F.prototype.Qj;
    F.prototype.getCoordinates = F.prototype.Q;
    F.prototype.getInteriorPoint = F.prototype.Jh;
    F.prototype.getLinearRingCount = F.prototype.Ph;
    F.prototype.getLinearRing = F.prototype.Oh;
    F.prototype.getLinearRings = F.prototype.kd;
    F.prototype.getType = F.prototype.O;
    F.prototype.intersectsExtent = F.prototype.ja;
    F.prototype.setCoordinates = F.prototype.V;
    t("ol.geom.Polygon.circular", Yk, OPENLAYERS);
    t("ol.geom.Polygon.fromExtent", function (b) {
        var c = b[0], d = b[1], e = b[2];
        b = b[3];
        c = [c, d, c, b, e, b, e, d, c, d];
        d = new F(null);
        Vk(d, "XY", c, [c.length]);
        return d
    }, OPENLAYERS);
    t("ol.geom.SimpleGeometry", nk, OPENLAYERS);
    nk.prototype.getFirstCoordinate = nk.prototype.yb;
    nk.prototype.getLastCoordinate = nk.prototype.zb;
    nk.prototype.getLayout = nk.prototype.Ab;
    nk.prototype.applyTransform = nk.prototype.qa;
    nk.prototype.translate = nk.prototype.Ia;
    t("ol.format.Feature", rp, OPENLAYERS);
    t("ol.format.GeoJSON", Bp, OPENLAYERS);
    Bp.prototype.readFeature = Bp.prototype.Nb;
    Bp.prototype.readFeatures = Bp.prototype.ma;
    Bp.prototype.readGeometry = Bp.prototype.Qc;
    Bp.prototype.readProjection = Bp.prototype.Ja;
    Bp.prototype.writeFeature = Bp.prototype.$d;
    Bp.prototype.writeFeatureObject = Bp.prototype.a;
    Bp.prototype.writeFeatures = Bp.prototype.Qb;
    Bp.prototype.writeFeaturesObject = Bp.prototype.c;
    Bp.prototype.writeGeometry = Bp.prototype.Wc;
    Bp.prototype.writeGeometryObject = Bp.prototype.f;
    t("ol.format.GPX", Rq, OPENLAYERS);
    Rq.prototype.readFeature = Rq.prototype.Nb;
    Rq.prototype.readFeatures = Rq.prototype.ma;
    Rq.prototype.readProjection = Rq.prototype.Ja;
    Rq.prototype.writeFeatures = Rq.prototype.Qb;
    Rq.prototype.writeFeaturesNode = Rq.prototype.a;
    t("ol.format.IGC", Br, OPENLAYERS);
    Br.prototype.readFeature = Br.prototype.Nb;
    Br.prototype.readFeatures = Br.prototype.ma;
    Br.prototype.readProjection = Br.prototype.Ja;
    t("ol.format.KML", gs, OPENLAYERS);
    gs.prototype.readFeature = gs.prototype.Nb;
    gs.prototype.readFeatures = gs.prototype.ma;
    gs.prototype.readName = gs.prototype.Dl;
    gs.prototype.readNetworkLinks = gs.prototype.El;
    gs.prototype.readProjection = gs.prototype.Ja;
    gs.prototype.writeFeatures = gs.prototype.Qb;
    gs.prototype.writeFeaturesNode = gs.prototype.a;
    t("ol.format.OSMXML", Qt, OPENLAYERS);
    Qt.prototype.readFeatures = Qt.prototype.ma;
    Qt.prototype.readProjection = Qt.prototype.Ja;
    t("ol.format.Polyline", ou, OPENLAYERS);
    t("ol.format.Polyline.encodeDeltas", pu, OPENLAYERS);
    t("ol.format.Polyline.decodeDeltas", ru, OPENLAYERS);
    t("ol.format.Polyline.encodeFloats", qu, OPENLAYERS);
    t("ol.format.Polyline.decodeFloats", su, OPENLAYERS);
    ou.prototype.readFeature = ou.prototype.Nb;
    ou.prototype.readFeatures = ou.prototype.ma;
    ou.prototype.readGeometry = ou.prototype.Qc;
    ou.prototype.readProjection = ou.prototype.Ja;
    ou.prototype.writeGeometry = ou.prototype.Wc;
    t("ol.format.TopoJSON", tu, OPENLAYERS);
    tu.prototype.readFeatures = tu.prototype.ma;
    tu.prototype.readProjection = tu.prototype.Ja;
    t("ol.format.WFS", zu, OPENLAYERS);
    zu.prototype.readFeatures = zu.prototype.ma;
    zu.prototype.readTransactionResponse = zu.prototype.g;
    zu.prototype.readFeatureCollectionMetadata = zu.prototype.e;
    zu.prototype.writeGetFeature = zu.prototype.n;
    zu.prototype.writeTransaction = zu.prototype.q;
    zu.prototype.readProjection = zu.prototype.Ja;
    t("ol.format.WKT", Mu, OPENLAYERS);
    Mu.prototype.readFeature = Mu.prototype.Nb;
    Mu.prototype.readFeatures = Mu.prototype.ma;
    Mu.prototype.readGeometry = Mu.prototype.Qc;
    Mu.prototype.writeFeature = Mu.prototype.$d;
    Mu.prototype.writeFeatures = Mu.prototype.Qb;
    Mu.prototype.writeGeometry = Mu.prototype.Wc;
    t("ol.format.WMSCapabilities", dv, OPENLAYERS);
    dv.prototype.read = dv.prototype.b;
    t("ol.format.WMSGetFeatureInfo", Av, OPENLAYERS);
    Av.prototype.readFeatures = Av.prototype.ma;
    t("ol.format.WMTSCapabilities", Cv, OPENLAYERS);
    Cv.prototype.read = Cv.prototype.b;
    t("ol.format.GML2", Qq, OPENLAYERS);
    t("ol.format.GML3", Hq, OPENLAYERS);
    Hq.prototype.writeGeometryNode = Hq.prototype.i;
    Hq.prototype.writeFeatures = Hq.prototype.Qb;
    Hq.prototype.writeFeaturesNode = Hq.prototype.a;
    t("ol.format.GML", Hq, OPENLAYERS);
    Hq.prototype.writeFeatures = Hq.prototype.Qb;
    Hq.prototype.writeFeaturesNode = Hq.prototype.a;
    t("ol.format.GMLBase", vq, OPENLAYERS);
    vq.prototype.readFeatures = vq.prototype.ma;
    t("ol.events.condition.altKeyOnly", function (b) {
        b = b.a;
        return b.d && !b.g && !b.c
    }, OPENLAYERS);
    t("ol.events.condition.altShiftKeysOnly", Tj, OPENLAYERS);
    t("ol.events.condition.always", bd, OPENLAYERS);
    t("ol.events.condition.click", function (b) {
        return b.type == Ai
    }, OPENLAYERS);
    t("ol.events.condition.never", ad, OPENLAYERS);
    t("ol.events.condition.pointerMove", Uj, OPENLAYERS);
    t("ol.events.condition.singleClick", Vj, OPENLAYERS);
    t("ol.events.condition.noModifierKeys", Wj, OPENLAYERS);
    t("ol.events.condition.platformModifierKeyOnly", function (b) {
        b = b.a;
        return !b.d && b.g && !b.c
    }, OPENLAYERS);
    t("ol.events.condition.shiftKeyOnly", Xj, OPENLAYERS);
    t("ol.events.condition.targetNotEditable", Yj, OPENLAYERS);
    t("ol.events.condition.mouseOnly", Zj, OPENLAYERS);
    t("ol.dom.Input", op, OPENLAYERS);
    op.prototype.getChecked = op.prototype.a;
    op.prototype.getValue = op.prototype.b;
    op.prototype.setValue = op.prototype.f;
    op.prototype.setChecked = op.prototype.c;
    t("ol.control.Attribution", Vg, OPENLAYERS);
    t("ol.control.Attribution.render", Wg, OPENLAYERS);
    Vg.prototype.getCollapsible = Vg.prototype.Aj;
    Vg.prototype.setCollapsible = Vg.prototype.Dj;
    Vg.prototype.setCollapsed = Vg.prototype.Cj;
    Vg.prototype.getCollapsed = Vg.prototype.zj;
    t("ol.control.Control", Ug, OPENLAYERS);
    Ug.prototype.getMap = Ug.prototype.f;
    Ug.prototype.setMap = Ug.prototype.setMap;
    Ug.prototype.setTarget = Ug.prototype.b;
    t("ol.control.defaults", ah, OPENLAYERS);
    t("ol.control.FullScreen", fh, OPENLAYERS);
    t("ol.control.MousePosition", gh, OPENLAYERS);
    t("ol.control.MousePosition.render", hh, OPENLAYERS);
    gh.prototype.getCoordinateFormat = gh.prototype.j;
    gh.prototype.getProjection = gh.prototype.p;
    gh.prototype.setMap = gh.prototype.setMap;
    gh.prototype.setCoordinateFormat = gh.prototype.D;
    gh.prototype.setProjection = gh.prototype.r;
    t("ol.control.OverviewMap", No, OPENLAYERS);
    No.prototype.setMap = No.prototype.setMap;
    t("ol.control.OverviewMap.render", Oo, OPENLAYERS);
    No.prototype.getCollapsible = No.prototype.Fj;
    No.prototype.setCollapsible = No.prototype.Ij;
    No.prototype.setCollapsed = No.prototype.Hj;
    No.prototype.getCollapsed = No.prototype.Ej;
    t("ol.control.Rotate", Yg, OPENLAYERS);
    t("ol.control.Rotate.render", Zg, OPENLAYERS);
    t("ol.control.ScaleLine", To, OPENLAYERS);
    To.prototype.getUnits = To.prototype.o;
    t("ol.control.ScaleLine.render", Uo, OPENLAYERS);
    To.prototype.setUnits = To.prototype.p;
    t("ol.control.Zoom", $g, OPENLAYERS);
    t("ol.control.ZoomSlider", hp, OPENLAYERS);
    t("ol.control.ZoomSlider.render", jp, OPENLAYERS);
    t("ol.control.ZoomToExtent", mp, OPENLAYERS);
    t("ol.color.asArray", qg, OPENLAYERS);
    t("ol.color.asString", sg, OPENLAYERS);
    qd.prototype.changed = qd.prototype.l;
    qd.prototype.getRevision = qd.prototype.u;
    qd.prototype.on = qd.prototype.s;
    qd.prototype.once = qd.prototype.v;
    qd.prototype.un = qd.prototype.t;
    qd.prototype.unByKey = qd.prototype.A;
    lg.prototype.bindTo = lg.prototype.K;
    lg.prototype.get = lg.prototype.get;
    lg.prototype.getKeys = lg.prototype.G;
    lg.prototype.getProperties = lg.prototype.I;
    lg.prototype.set = lg.prototype.set;
    lg.prototype.setProperties = lg.prototype.C;
    lg.prototype.unbind = lg.prototype.L;
    lg.prototype.unbindAll = lg.prototype.M;
    lg.prototype.changed = lg.prototype.l;
    lg.prototype.getRevision = lg.prototype.u;
    lg.prototype.on = lg.prototype.s;
    lg.prototype.once = lg.prototype.v;
    lg.prototype.un = lg.prototype.t;
    lg.prototype.unByKey = lg.prototype.A;
    np.prototype.bindTo = np.prototype.K;
    np.prototype.get = np.prototype.get;
    np.prototype.getKeys = np.prototype.G;
    np.prototype.getProperties = np.prototype.I;
    np.prototype.set = np.prototype.set;
    np.prototype.setProperties = np.prototype.C;
    np.prototype.unbind = np.prototype.L;
    np.prototype.unbindAll = np.prototype.M;
    np.prototype.changed = np.prototype.l;
    np.prototype.getRevision = np.prototype.u;
    np.prototype.on = np.prototype.s;
    np.prototype.once = np.prototype.v;
    np.prototype.un = np.prototype.t;
    np.prototype.unByKey = np.prototype.A;
    N.prototype.bindTo = N.prototype.K;
    N.prototype.get = N.prototype.get;
    N.prototype.getKeys = N.prototype.G;
    N.prototype.getProperties = N.prototype.I;
    N.prototype.set = N.prototype.set;
    N.prototype.setProperties = N.prototype.C;
    N.prototype.unbind = N.prototype.L;
    N.prototype.unbindAll = N.prototype.M;
    N.prototype.changed = N.prototype.l;
    N.prototype.getRevision = N.prototype.u;
    N.prototype.on = N.prototype.s;
    N.prototype.once = N.prototype.v;
    N.prototype.un = N.prototype.t;
    N.prototype.unByKey = N.prototype.A;
    V.prototype.bindTo = V.prototype.K;
    V.prototype.get = V.prototype.get;
    V.prototype.getKeys = V.prototype.G;
    V.prototype.getProperties = V.prototype.I;
    V.prototype.set = V.prototype.set;
    V.prototype.setProperties = V.prototype.C;
    V.prototype.unbind = V.prototype.L;
    V.prototype.unbindAll = V.prototype.M;
    V.prototype.changed = V.prototype.l;
    V.prototype.getRevision = V.prototype.u;
    V.prototype.on = V.prototype.s;
    V.prototype.once = V.prototype.v;
    V.prototype.un = V.prototype.t;
    V.prototype.unByKey = V.prototype.A;
    Yv.prototype.getTileCoord = Yv.prototype.e;
    K.prototype.bindTo = K.prototype.K;
    K.prototype.get = K.prototype.get;
    K.prototype.getKeys = K.prototype.G;
    K.prototype.getProperties = K.prototype.I;
    K.prototype.set = K.prototype.set;
    K.prototype.setProperties = K.prototype.C;
    K.prototype.unbind = K.prototype.L;
    K.prototype.unbindAll = K.prototype.M;
    K.prototype.changed = K.prototype.l;
    K.prototype.getRevision = K.prototype.u;
    K.prototype.on = K.prototype.s;
    K.prototype.once = K.prototype.v;
    K.prototype.un = K.prototype.t;
    K.prototype.unByKey = K.prototype.A;
    wi.prototype.map = wi.prototype.map;
    wi.prototype.frameState = wi.prototype.frameState;
    xi.prototype.originalEvent = xi.prototype.originalEvent;
    xi.prototype.pixel = xi.prototype.pixel;
    xi.prototype.coordinate = xi.prototype.coordinate;
    xi.prototype.dragging = xi.prototype.dragging;
    xi.prototype.preventDefault = xi.prototype.preventDefault;
    xi.prototype.stopPropagation = xi.prototype.pb;
    xi.prototype.map = xi.prototype.map;
    xi.prototype.frameState = xi.prototype.frameState;
    L.prototype.bindTo = L.prototype.K;
    L.prototype.get = L.prototype.get;
    L.prototype.getKeys = L.prototype.G;
    L.prototype.getProperties = L.prototype.I;
    L.prototype.set = L.prototype.set;
    L.prototype.setProperties = L.prototype.C;
    L.prototype.unbind = L.prototype.L;
    L.prototype.unbindAll = L.prototype.M;
    L.prototype.changed = L.prototype.l;
    L.prototype.getRevision = L.prototype.u;
    L.prototype.on = L.prototype.s;
    L.prototype.once = L.prototype.v;
    L.prototype.un = L.prototype.t;
    L.prototype.unByKey = L.prototype.A;
    B.prototype.bindTo = B.prototype.K;
    B.prototype.get = B.prototype.get;
    B.prototype.getKeys = B.prototype.G;
    B.prototype.getProperties = B.prototype.I;
    B.prototype.set = B.prototype.set;
    B.prototype.setProperties = B.prototype.C;
    B.prototype.unbind = B.prototype.L;
    B.prototype.unbindAll = B.prototype.M;
    B.prototype.changed = B.prototype.l;
    B.prototype.getRevision = B.prototype.u;
    B.prototype.on = B.prototype.s;
    B.prototype.once = B.prototype.v;
    B.prototype.un = B.prototype.t;
    B.prototype.unByKey = B.prototype.A;
    fz.prototype.getMaxZoom = fz.prototype.ld;
    fz.prototype.getMinZoom = fz.prototype.od;
    fz.prototype.getOrigin = fz.prototype.Lb;
    fz.prototype.getResolution = fz.prototype.na;
    fz.prototype.getResolutions = fz.prototype.Od;
    fz.prototype.getTileCoordForCoordAndResolution = fz.prototype.Wb;
    fz.prototype.getTileCoordForCoordAndZ = fz.prototype.Mc;
    fz.prototype.getTileSize = fz.prototype.ua;
    Sx.prototype.getMaxZoom = Sx.prototype.ld;
    Sx.prototype.getMinZoom = Sx.prototype.od;
    Sx.prototype.getOrigin = Sx.prototype.Lb;
    Sx.prototype.getResolution = Sx.prototype.na;
    Sx.prototype.getResolutions = Sx.prototype.Od;
    Sx.prototype.getTileCoordForCoordAndResolution = Sx.prototype.Wb;
    Sx.prototype.getTileCoordForCoordAndZ = Sx.prototype.Mc;
    Sx.prototype.getTileSize = Sx.prototype.ua;
    jz.prototype.getMaxZoom = jz.prototype.ld;
    jz.prototype.getMinZoom = jz.prototype.od;
    jz.prototype.getOrigin = jz.prototype.Lb;
    jz.prototype.getResolution = jz.prototype.na;
    jz.prototype.getResolutions = jz.prototype.Od;
    jz.prototype.getTileCoordForCoordAndResolution = jz.prototype.Wb;
    jz.prototype.getTileCoordForCoordAndZ = jz.prototype.Mc;
    jz.prototype.getTileSize = jz.prototype.ua;
    ql.prototype.getOpacity = ql.prototype.Jd;
    ql.prototype.getRotateWithView = ql.prototype.qd;
    ql.prototype.getRotation = ql.prototype.Kd;
    ql.prototype.getScale = ql.prototype.Ld;
    ql.prototype.getSnapToPixel = ql.prototype.rd;
    ql.prototype.setRotation = ql.prototype.Md;
    ql.prototype.setScale = ql.prototype.Nd;
    wj.prototype.getOpacity = wj.prototype.Jd;
    wj.prototype.getRotateWithView = wj.prototype.qd;
    wj.prototype.getRotation = wj.prototype.Kd;
    wj.prototype.getScale = wj.prototype.Ld;
    wj.prototype.getSnapToPixel = wj.prototype.rd;
    wj.prototype.setRotation = wj.prototype.Md;
    wj.prototype.setScale = wj.prototype.Nd;
    qz.prototype.getOpacity = qz.prototype.Jd;
    qz.prototype.getRotateWithView = qz.prototype.qd;
    qz.prototype.getRotation = qz.prototype.Kd;
    qz.prototype.getScale = qz.prototype.Ld;
    qz.prototype.getSnapToPixel = qz.prototype.rd;
    qz.prototype.setRotation = qz.prototype.Md;
    qz.prototype.setScale = qz.prototype.Nd;
    Ki.prototype.bindTo = Ki.prototype.K;
    Ki.prototype.get = Ki.prototype.get;
    Ki.prototype.getKeys = Ki.prototype.G;
    Ki.prototype.getProperties = Ki.prototype.I;
    Ki.prototype.set = Ki.prototype.set;
    Ki.prototype.setProperties = Ki.prototype.C;
    Ki.prototype.unbind = Ki.prototype.L;
    Ki.prototype.unbindAll = Ki.prototype.M;
    Ki.prototype.changed = Ki.prototype.l;
    Ki.prototype.getRevision = Ki.prototype.u;
    Ki.prototype.on = Ki.prototype.s;
    Ki.prototype.once = Ki.prototype.v;
    Ki.prototype.un = Ki.prototype.t;
    Ki.prototype.unByKey = Ki.prototype.A;
    dj.prototype.getAttributions = dj.prototype.Y;
    dj.prototype.getLogo = dj.prototype.X;
    dj.prototype.getProjection = dj.prototype.Z;
    dj.prototype.getState = dj.prototype.$;
    dj.prototype.bindTo = dj.prototype.K;
    dj.prototype.get = dj.prototype.get;
    dj.prototype.getKeys = dj.prototype.G;
    dj.prototype.getProperties = dj.prototype.I;
    dj.prototype.set = dj.prototype.set;
    dj.prototype.setProperties = dj.prototype.C;
    dj.prototype.unbind = dj.prototype.L;
    dj.prototype.unbindAll = dj.prototype.M;
    dj.prototype.changed = dj.prototype.l;
    dj.prototype.getRevision = dj.prototype.u;
    dj.prototype.on = dj.prototype.s;
    dj.prototype.once = dj.prototype.v;
    dj.prototype.un = dj.prototype.t;
    dj.prototype.unByKey = dj.prototype.A;
    Qx.prototype.getTileGrid = Qx.prototype.xa;
    Qx.prototype.getAttributions = Qx.prototype.Y;
    Qx.prototype.getLogo = Qx.prototype.X;
    Qx.prototype.getProjection = Qx.prototype.Z;
    Qx.prototype.getState = Qx.prototype.$;
    Qx.prototype.bindTo = Qx.prototype.K;
    Qx.prototype.get = Qx.prototype.get;
    Qx.prototype.getKeys = Qx.prototype.G;
    Qx.prototype.getProperties = Qx.prototype.I;
    Qx.prototype.set = Qx.prototype.set;
    Qx.prototype.setProperties = Qx.prototype.C;
    Qx.prototype.unbind = Qx.prototype.L;
    Qx.prototype.unbindAll = Qx.prototype.M;
    Qx.prototype.changed = Qx.prototype.l;
    Qx.prototype.getRevision = Qx.prototype.u;
    Qx.prototype.on = Qx.prototype.s;
    Qx.prototype.once = Qx.prototype.v;
    Qx.prototype.un = Qx.prototype.t;
    Qx.prototype.unByKey = Qx.prototype.A;
    Tx.prototype.getTileLoadFunction = Tx.prototype.bb;
    Tx.prototype.getTileUrlFunction = Tx.prototype.cb;
    Tx.prototype.setTileLoadFunction = Tx.prototype.jb;
    Tx.prototype.setTileUrlFunction = Tx.prototype.ta;
    Tx.prototype.getTileGrid = Tx.prototype.xa;
    Tx.prototype.getAttributions = Tx.prototype.Y;
    Tx.prototype.getLogo = Tx.prototype.X;
    Tx.prototype.getProjection = Tx.prototype.Z;
    Tx.prototype.getState = Tx.prototype.$;
    Tx.prototype.bindTo = Tx.prototype.K;
    Tx.prototype.get = Tx.prototype.get;
    Tx.prototype.getKeys = Tx.prototype.G;
    Tx.prototype.getProperties = Tx.prototype.I;
    Tx.prototype.set = Tx.prototype.set;
    Tx.prototype.setProperties = Tx.prototype.C;
    Tx.prototype.unbind = Tx.prototype.L;
    Tx.prototype.unbindAll = Tx.prototype.M;
    Tx.prototype.changed = Tx.prototype.l;
    Tx.prototype.getRevision = Tx.prototype.u;
    Tx.prototype.on = Tx.prototype.s;
    Tx.prototype.once = Tx.prototype.v;
    Tx.prototype.un = Tx.prototype.t;
    Tx.prototype.unByKey = Tx.prototype.A;
    mn.prototype.getAttributions = mn.prototype.Y;
    mn.prototype.getLogo = mn.prototype.X;
    mn.prototype.getProjection = mn.prototype.Z;
    mn.prototype.getState = mn.prototype.$;
    mn.prototype.bindTo = mn.prototype.K;
    mn.prototype.get = mn.prototype.get;
    mn.prototype.getKeys = mn.prototype.G;
    mn.prototype.getProperties = mn.prototype.I;
    mn.prototype.set = mn.prototype.set;
    mn.prototype.setProperties = mn.prototype.C;
    mn.prototype.unbind = mn.prototype.L;
    mn.prototype.unbindAll = mn.prototype.M;
    mn.prototype.changed = mn.prototype.l;
    mn.prototype.getRevision = mn.prototype.u;
    mn.prototype.on = mn.prototype.s;
    mn.prototype.once = mn.prototype.v;
    mn.prototype.un = mn.prototype.t;
    mn.prototype.unByKey = mn.prototype.A;
    Vx.prototype.addFeature = Vx.prototype.Va;
    Vx.prototype.addFeatures = Vx.prototype.Ga;
    Vx.prototype.clear = Vx.prototype.clear;
    Vx.prototype.forEachFeature = Vx.prototype.$a;
    Vx.prototype.forEachFeatureInExtent = Vx.prototype.wa;
    Vx.prototype.forEachFeatureIntersectingExtent = Vx.prototype.Ma;
    Vx.prototype.getFeatures = Vx.prototype.Aa;
    Vx.prototype.getFeaturesAtCoordinate = Vx.prototype.Oa;
    Vx.prototype.getClosestFeatureToCoordinate = Vx.prototype.ab;
    Vx.prototype.getExtent = Vx.prototype.J;
    Vx.prototype.getFeatureById = Vx.prototype.Na;
    Vx.prototype.removeFeature = Vx.prototype.fb;
    Vx.prototype.getAttributions = Vx.prototype.Y;
    Vx.prototype.getLogo = Vx.prototype.X;
    Vx.prototype.getProjection = Vx.prototype.Z;
    Vx.prototype.getState = Vx.prototype.$;
    Vx.prototype.bindTo = Vx.prototype.K;
    Vx.prototype.get = Vx.prototype.get;
    Vx.prototype.getKeys = Vx.prototype.G;
    Vx.prototype.getProperties = Vx.prototype.I;
    Vx.prototype.set = Vx.prototype.set;
    Vx.prototype.setProperties = Vx.prototype.C;
    Vx.prototype.unbind = Vx.prototype.L;
    Vx.prototype.unbindAll = Vx.prototype.M;
    Vx.prototype.changed = Vx.prototype.l;
    Vx.prototype.getRevision = Vx.prototype.u;
    Vx.prototype.on = Vx.prototype.s;
    Vx.prototype.once = Vx.prototype.v;
    Vx.prototype.un = Vx.prototype.t;
    Vx.prototype.unByKey = Vx.prototype.A;
    Y.prototype.addFeature = Y.prototype.Va;
    Y.prototype.addFeatures = Y.prototype.Ga;
    Y.prototype.clear = Y.prototype.clear;
    Y.prototype.forEachFeature = Y.prototype.$a;
    Y.prototype.forEachFeatureInExtent = Y.prototype.wa;
    Y.prototype.forEachFeatureIntersectingExtent = Y.prototype.Ma;
    Y.prototype.getFeatures = Y.prototype.Aa;
    Y.prototype.getFeaturesAtCoordinate = Y.prototype.Oa;
    Y.prototype.getClosestFeatureToCoordinate = Y.prototype.ab;
    Y.prototype.getExtent = Y.prototype.J;
    Y.prototype.getFeatureById = Y.prototype.Na;
    Y.prototype.removeFeature = Y.prototype.fb;
    Y.prototype.getAttributions = Y.prototype.Y;
    Y.prototype.getLogo = Y.prototype.X;
    Y.prototype.getProjection = Y.prototype.Z;
    Y.prototype.getState = Y.prototype.$;
    Y.prototype.bindTo = Y.prototype.K;
    Y.prototype.get = Y.prototype.get;
    Y.prototype.getKeys = Y.prototype.G;
    Y.prototype.getProperties = Y.prototype.I;
    Y.prototype.set = Y.prototype.set;
    Y.prototype.setProperties = Y.prototype.C;
    Y.prototype.unbind = Y.prototype.L;
    Y.prototype.unbindAll = Y.prototype.M;
    Y.prototype.changed = Y.prototype.l;
    Y.prototype.getRevision = Y.prototype.u;
    Y.prototype.on = Y.prototype.s;
    Y.prototype.once = Y.prototype.v;
    Y.prototype.un = Y.prototype.t;
    Y.prototype.unByKey = Y.prototype.A;
    Z.prototype.readFeatures = Z.prototype.a;
    Z.prototype.addFeature = Z.prototype.Va;
    Z.prototype.addFeatures = Z.prototype.Ga;
    Z.prototype.clear = Z.prototype.clear;
    Z.prototype.forEachFeature = Z.prototype.$a;
    Z.prototype.forEachFeatureInExtent = Z.prototype.wa;
    Z.prototype.forEachFeatureIntersectingExtent = Z.prototype.Ma;
    Z.prototype.getFeatures = Z.prototype.Aa;
    Z.prototype.getFeaturesAtCoordinate = Z.prototype.Oa;
    Z.prototype.getClosestFeatureToCoordinate = Z.prototype.ab;
    Z.prototype.getExtent = Z.prototype.J;
    Z.prototype.getFeatureById = Z.prototype.Na;
    Z.prototype.removeFeature = Z.prototype.fb;
    Z.prototype.getAttributions = Z.prototype.Y;
    Z.prototype.getLogo = Z.prototype.X;
    Z.prototype.getProjection = Z.prototype.Z;
    Z.prototype.getState = Z.prototype.$;
    Z.prototype.bindTo = Z.prototype.K;
    Z.prototype.get = Z.prototype.get;
    Z.prototype.getKeys = Z.prototype.G;
    Z.prototype.getProperties = Z.prototype.I;
    Z.prototype.set = Z.prototype.set;
    Z.prototype.setProperties = Z.prototype.C;
    Z.prototype.unbind = Z.prototype.L;
    Z.prototype.unbindAll = Z.prototype.M;
    Z.prototype.changed = Z.prototype.l;
    Z.prototype.getRevision = Z.prototype.u;
    Z.prototype.on = Z.prototype.s;
    Z.prototype.once = Z.prototype.v;
    Z.prototype.un = Z.prototype.t;
    Z.prototype.unByKey = Z.prototype.A;
    vy.prototype.readFeatures = vy.prototype.a;
    vy.prototype.addFeature = vy.prototype.Va;
    vy.prototype.addFeatures = vy.prototype.Ga;
    vy.prototype.clear = vy.prototype.clear;
    vy.prototype.forEachFeature = vy.prototype.$a;
    vy.prototype.forEachFeatureInExtent = vy.prototype.wa;
    vy.prototype.forEachFeatureIntersectingExtent = vy.prototype.Ma;
    vy.prototype.getFeatures = vy.prototype.Aa;
    vy.prototype.getFeaturesAtCoordinate = vy.prototype.Oa;
    vy.prototype.getClosestFeatureToCoordinate = vy.prototype.ab;
    vy.prototype.getExtent = vy.prototype.J;
    vy.prototype.getFeatureById = vy.prototype.Na;
    vy.prototype.removeFeature = vy.prototype.fb;
    vy.prototype.getAttributions = vy.prototype.Y;
    vy.prototype.getLogo = vy.prototype.X;
    vy.prototype.getProjection = vy.prototype.Z;
    vy.prototype.getState = vy.prototype.$;
    vy.prototype.bindTo = vy.prototype.K;
    vy.prototype.get = vy.prototype.get;
    vy.prototype.getKeys = vy.prototype.G;
    vy.prototype.getProperties = vy.prototype.I;
    vy.prototype.set = vy.prototype.set;
    vy.prototype.setProperties = vy.prototype.C;
    vy.prototype.unbind = vy.prototype.L;
    vy.prototype.unbindAll = vy.prototype.M;
    vy.prototype.changed = vy.prototype.l;
    vy.prototype.getRevision = vy.prototype.u;
    vy.prototype.on = vy.prototype.s;
    vy.prototype.once = vy.prototype.v;
    vy.prototype.un = vy.prototype.t;
    vy.prototype.unByKey = vy.prototype.A;
    wy.prototype.readFeatures = wy.prototype.a;
    wy.prototype.addFeature = wy.prototype.Va;
    wy.prototype.addFeatures = wy.prototype.Ga;
    wy.prototype.clear = wy.prototype.clear;
    wy.prototype.forEachFeature = wy.prototype.$a;
    wy.prototype.forEachFeatureInExtent = wy.prototype.wa;
    wy.prototype.forEachFeatureIntersectingExtent = wy.prototype.Ma;
    wy.prototype.getFeatures = wy.prototype.Aa;
    wy.prototype.getFeaturesAtCoordinate = wy.prototype.Oa;
    wy.prototype.getClosestFeatureToCoordinate = wy.prototype.ab;
    wy.prototype.getExtent = wy.prototype.J;
    wy.prototype.getFeatureById = wy.prototype.Na;
    wy.prototype.removeFeature = wy.prototype.fb;
    wy.prototype.getAttributions = wy.prototype.Y;
    wy.prototype.getLogo = wy.prototype.X;
    wy.prototype.getProjection = wy.prototype.Z;
    wy.prototype.getState = wy.prototype.$;
    wy.prototype.bindTo = wy.prototype.K;
    wy.prototype.get = wy.prototype.get;
    wy.prototype.getKeys = wy.prototype.G;
    wy.prototype.getProperties = wy.prototype.I;
    wy.prototype.set = wy.prototype.set;
    wy.prototype.setProperties = wy.prototype.C;
    wy.prototype.unbind = wy.prototype.L;
    wy.prototype.unbindAll = wy.prototype.M;
    wy.prototype.changed = wy.prototype.l;
    wy.prototype.getRevision = wy.prototype.u;
    wy.prototype.on = wy.prototype.s;
    wy.prototype.once = wy.prototype.v;
    wy.prototype.un = wy.prototype.t;
    wy.prototype.unByKey = wy.prototype.A;
    xy.prototype.readFeatures = xy.prototype.a;
    xy.prototype.addFeature = xy.prototype.Va;
    xy.prototype.addFeatures = xy.prototype.Ga;
    xy.prototype.clear = xy.prototype.clear;
    xy.prototype.forEachFeature = xy.prototype.$a;
    xy.prototype.forEachFeatureInExtent = xy.prototype.wa;
    xy.prototype.forEachFeatureIntersectingExtent = xy.prototype.Ma;
    xy.prototype.getFeatures = xy.prototype.Aa;
    xy.prototype.getFeaturesAtCoordinate = xy.prototype.Oa;
    xy.prototype.getClosestFeatureToCoordinate = xy.prototype.ab;
    xy.prototype.getExtent = xy.prototype.J;
    xy.prototype.getFeatureById = xy.prototype.Na;
    xy.prototype.removeFeature = xy.prototype.fb;
    xy.prototype.getAttributions = xy.prototype.Y;
    xy.prototype.getLogo = xy.prototype.X;
    xy.prototype.getProjection = xy.prototype.Z;
    xy.prototype.getState = xy.prototype.$;
    xy.prototype.bindTo = xy.prototype.K;
    xy.prototype.get = xy.prototype.get;
    xy.prototype.getKeys = xy.prototype.G;
    xy.prototype.getProperties = xy.prototype.I;
    xy.prototype.set = xy.prototype.set;
    xy.prototype.setProperties = xy.prototype.C;
    xy.prototype.unbind = xy.prototype.L;
    xy.prototype.unbindAll = xy.prototype.M;
    xy.prototype.changed = xy.prototype.l;
    xy.prototype.getRevision = xy.prototype.u;
    xy.prototype.on = xy.prototype.s;
    xy.prototype.once = xy.prototype.v;
    xy.prototype.un = xy.prototype.t;
    xy.prototype.unByKey = xy.prototype.A;
    bn.prototype.getAttributions = bn.prototype.Y;
    bn.prototype.getLogo = bn.prototype.X;
    bn.prototype.getProjection = bn.prototype.Z;
    bn.prototype.getState = bn.prototype.$;
    bn.prototype.bindTo = bn.prototype.K;
    bn.prototype.get = bn.prototype.get;
    bn.prototype.getKeys = bn.prototype.G;
    bn.prototype.getProperties = bn.prototype.I;
    bn.prototype.set = bn.prototype.set;
    bn.prototype.setProperties = bn.prototype.C;
    bn.prototype.unbind = bn.prototype.L;
    bn.prototype.unbindAll = bn.prototype.M;
    bn.prototype.changed = bn.prototype.l;
    bn.prototype.getRevision = bn.prototype.u;
    bn.prototype.on = bn.prototype.s;
    bn.prototype.once = bn.prototype.v;
    bn.prototype.un = bn.prototype.t;
    bn.prototype.unByKey = bn.prototype.A;
    en.prototype.getAttributions = en.prototype.Y;
    en.prototype.getLogo = en.prototype.X;
    en.prototype.getProjection = en.prototype.Z;
    en.prototype.getState = en.prototype.$;
    en.prototype.bindTo = en.prototype.K;
    en.prototype.get = en.prototype.get;
    en.prototype.getKeys = en.prototype.G;
    en.prototype.getProperties = en.prototype.I;
    en.prototype.set = en.prototype.set;
    en.prototype.setProperties = en.prototype.C;
    en.prototype.unbind = en.prototype.L;
    en.prototype.unbindAll = en.prototype.M;
    en.prototype.changed = en.prototype.l;
    en.prototype.getRevision = en.prototype.u;
    en.prototype.on = en.prototype.s;
    en.prototype.once = en.prototype.v;
    en.prototype.un = en.prototype.t;
    en.prototype.unByKey = en.prototype.A;
    yy.prototype.getAttributions = yy.prototype.Y;
    yy.prototype.getLogo = yy.prototype.X;
    yy.prototype.getProjection = yy.prototype.Z;
    yy.prototype.getState = yy.prototype.$;
    yy.prototype.bindTo = yy.prototype.K;
    yy.prototype.get = yy.prototype.get;
    yy.prototype.getKeys = yy.prototype.G;
    yy.prototype.getProperties = yy.prototype.I;
    yy.prototype.set = yy.prototype.set;
    yy.prototype.setProperties = yy.prototype.C;
    yy.prototype.unbind = yy.prototype.L;
    yy.prototype.unbindAll = yy.prototype.M;
    yy.prototype.changed = yy.prototype.l;
    yy.prototype.getRevision = yy.prototype.u;
    yy.prototype.on = yy.prototype.s;
    yy.prototype.once = yy.prototype.v;
    yy.prototype.un = yy.prototype.t;
    yy.prototype.unByKey = yy.prototype.A;
    zy.prototype.getAttributions = zy.prototype.Y;
    zy.prototype.getLogo = zy.prototype.X;
    zy.prototype.getProjection = zy.prototype.Z;
    zy.prototype.getState = zy.prototype.$;
    zy.prototype.bindTo = zy.prototype.K;
    zy.prototype.get = zy.prototype.get;
    zy.prototype.getKeys = zy.prototype.G;
    zy.prototype.getProperties = zy.prototype.I;
    zy.prototype.set = zy.prototype.set;
    zy.prototype.setProperties = zy.prototype.C;
    zy.prototype.unbind = zy.prototype.L;
    zy.prototype.unbindAll = zy.prototype.M;
    zy.prototype.changed = zy.prototype.l;
    zy.prototype.getRevision = zy.prototype.u;
    zy.prototype.on = zy.prototype.s;
    zy.prototype.once = zy.prototype.v;
    zy.prototype.un = zy.prototype.t;
    zy.prototype.unByKey = zy.prototype.A;
    sn.prototype.getAttributions = sn.prototype.Y;
    sn.prototype.getLogo = sn.prototype.X;
    sn.prototype.getProjection = sn.prototype.Z;
    sn.prototype.getState = sn.prototype.$;
    sn.prototype.bindTo = sn.prototype.K;
    sn.prototype.get = sn.prototype.get;
    sn.prototype.getKeys = sn.prototype.G;
    sn.prototype.getProperties = sn.prototype.I;
    sn.prototype.set = sn.prototype.set;
    sn.prototype.setProperties = sn.prototype.C;
    sn.prototype.unbind = sn.prototype.L;
    sn.prototype.unbindAll = sn.prototype.M;
    sn.prototype.changed = sn.prototype.l;
    sn.prototype.getRevision = sn.prototype.u;
    sn.prototype.on = sn.prototype.s;
    sn.prototype.once = sn.prototype.v;
    sn.prototype.un = sn.prototype.t;
    sn.prototype.unByKey = sn.prototype.A;
    Ay.prototype.getAttributions = Ay.prototype.Y;
    Ay.prototype.getLogo = Ay.prototype.X;
    Ay.prototype.getProjection = Ay.prototype.Z;
    Ay.prototype.getState = Ay.prototype.$;
    Ay.prototype.bindTo = Ay.prototype.K;
    Ay.prototype.get = Ay.prototype.get;
    Ay.prototype.getKeys = Ay.prototype.G;
    Ay.prototype.getProperties = Ay.prototype.I;
    Ay.prototype.set = Ay.prototype.set;
    Ay.prototype.setProperties = Ay.prototype.C;
    Ay.prototype.unbind = Ay.prototype.L;
    Ay.prototype.unbindAll = Ay.prototype.M;
    Ay.prototype.changed = Ay.prototype.l;
    Ay.prototype.getRevision = Ay.prototype.u;
    Ay.prototype.on = Ay.prototype.s;
    Ay.prototype.once = Ay.prototype.v;
    Ay.prototype.un = Ay.prototype.t;
    Ay.prototype.unByKey = Ay.prototype.A;
    Ey.prototype.readFeatures = Ey.prototype.a;
    Ey.prototype.addFeature = Ey.prototype.Va;
    Ey.prototype.addFeatures = Ey.prototype.Ga;
    Ey.prototype.clear = Ey.prototype.clear;
    Ey.prototype.forEachFeature = Ey.prototype.$a;
    Ey.prototype.forEachFeatureInExtent = Ey.prototype.wa;
    Ey.prototype.forEachFeatureIntersectingExtent = Ey.prototype.Ma;
    Ey.prototype.getFeatures = Ey.prototype.Aa;
    Ey.prototype.getFeaturesAtCoordinate = Ey.prototype.Oa;
    Ey.prototype.getClosestFeatureToCoordinate = Ey.prototype.ab;
    Ey.prototype.getExtent = Ey.prototype.J;
    Ey.prototype.getFeatureById = Ey.prototype.Na;
    Ey.prototype.removeFeature = Ey.prototype.fb;
    Ey.prototype.getAttributions = Ey.prototype.Y;
    Ey.prototype.getLogo = Ey.prototype.X;
    Ey.prototype.getProjection = Ey.prototype.Z;
    Ey.prototype.getState = Ey.prototype.$;
    Ey.prototype.bindTo = Ey.prototype.K;
    Ey.prototype.get = Ey.prototype.get;
    Ey.prototype.getKeys = Ey.prototype.G;
    Ey.prototype.getProperties = Ey.prototype.I;
    Ey.prototype.set = Ey.prototype.set;
    Ey.prototype.setProperties = Ey.prototype.C;
    Ey.prototype.unbind = Ey.prototype.L;
    Ey.prototype.unbindAll = Ey.prototype.M;
    Ey.prototype.changed = Ey.prototype.l;
    Ey.prototype.getRevision = Ey.prototype.u;
    Ey.prototype.on = Ey.prototype.s;
    Ey.prototype.once = Ey.prototype.v;
    Ey.prototype.un = Ey.prototype.t;
    Ey.prototype.unByKey = Ey.prototype.A;
    Fy.prototype.getTileLoadFunction = Fy.prototype.bb;
    Fy.prototype.getTileUrlFunction = Fy.prototype.cb;
    Fy.prototype.setTileLoadFunction = Fy.prototype.jb;
    Fy.prototype.getTileGrid = Fy.prototype.xa;
    Fy.prototype.getAttributions = Fy.prototype.Y;
    Fy.prototype.getLogo = Fy.prototype.X;
    Fy.prototype.getProjection = Fy.prototype.Z;
    Fy.prototype.getState = Fy.prototype.$;
    Fy.prototype.bindTo = Fy.prototype.K;
    Fy.prototype.get = Fy.prototype.get;
    Fy.prototype.getKeys = Fy.prototype.G;
    Fy.prototype.getProperties = Fy.prototype.I;
    Fy.prototype.set = Fy.prototype.set;
    Fy.prototype.setProperties = Fy.prototype.C;
    Fy.prototype.unbind = Fy.prototype.L;
    Fy.prototype.unbindAll = Fy.prototype.M;
    Fy.prototype.changed = Fy.prototype.l;
    Fy.prototype.getRevision = Fy.prototype.u;
    Fy.prototype.on = Fy.prototype.s;
    Fy.prototype.once = Fy.prototype.v;
    Fy.prototype.un = Fy.prototype.t;
    Fy.prototype.unByKey = Fy.prototype.A;
    Iy.prototype.setTileUrlFunction = Iy.prototype.ta;
    Iy.prototype.setUrl = Iy.prototype.b;
    Iy.prototype.getTileLoadFunction = Iy.prototype.bb;
    Iy.prototype.getTileUrlFunction = Iy.prototype.cb;
    Iy.prototype.setTileLoadFunction = Iy.prototype.jb;
    Iy.prototype.getTileGrid = Iy.prototype.xa;
    Iy.prototype.getAttributions = Iy.prototype.Y;
    Iy.prototype.getLogo = Iy.prototype.X;
    Iy.prototype.getProjection = Iy.prototype.Z;
    Iy.prototype.getState = Iy.prototype.$;
    Iy.prototype.bindTo = Iy.prototype.K;
    Iy.prototype.get = Iy.prototype.get;
    Iy.prototype.getKeys = Iy.prototype.G;
    Iy.prototype.getProperties = Iy.prototype.I;
    Iy.prototype.set = Iy.prototype.set;
    Iy.prototype.setProperties = Iy.prototype.C;
    Iy.prototype.unbind = Iy.prototype.L;
    Iy.prototype.unbindAll = Iy.prototype.M;
    Iy.prototype.changed = Iy.prototype.l;
    Iy.prototype.getRevision = Iy.prototype.u;
    Iy.prototype.on = Iy.prototype.s;
    Iy.prototype.once = Iy.prototype.v;
    Iy.prototype.un = Iy.prototype.t;
    Iy.prototype.unByKey = Iy.prototype.A;
    Gy.prototype.setTileUrlFunction = Gy.prototype.ta;
    Gy.prototype.setUrl = Gy.prototype.b;
    Gy.prototype.getTileLoadFunction = Gy.prototype.bb;
    Gy.prototype.getTileUrlFunction = Gy.prototype.cb;
    Gy.prototype.setTileLoadFunction = Gy.prototype.jb;
    Gy.prototype.getTileGrid = Gy.prototype.xa;
    Gy.prototype.getAttributions = Gy.prototype.Y;
    Gy.prototype.getLogo = Gy.prototype.X;
    Gy.prototype.getProjection = Gy.prototype.Z;
    Gy.prototype.getState = Gy.prototype.$;
    Gy.prototype.bindTo = Gy.prototype.K;
    Gy.prototype.get = Gy.prototype.get;
    Gy.prototype.getKeys = Gy.prototype.G;
    Gy.prototype.getProperties = Gy.prototype.I;
    Gy.prototype.set = Gy.prototype.set;
    Gy.prototype.setProperties = Gy.prototype.C;
    Gy.prototype.unbind = Gy.prototype.L;
    Gy.prototype.unbindAll = Gy.prototype.M;
    Gy.prototype.changed = Gy.prototype.l;
    Gy.prototype.getRevision = Gy.prototype.u;
    Gy.prototype.on = Gy.prototype.s;
    Gy.prototype.once = Gy.prototype.v;
    Gy.prototype.un = Gy.prototype.t;
    Gy.prototype.unByKey = Gy.prototype.A;
    Ly.prototype.readFeatures = Ly.prototype.a;
    Ly.prototype.addFeature = Ly.prototype.Va;
    Ly.prototype.addFeatures = Ly.prototype.Ga;
    Ly.prototype.clear = Ly.prototype.clear;
    Ly.prototype.forEachFeature = Ly.prototype.$a;
    Ly.prototype.forEachFeatureInExtent = Ly.prototype.wa;
    Ly.prototype.forEachFeatureIntersectingExtent = Ly.prototype.Ma;
    Ly.prototype.getFeatures = Ly.prototype.Aa;
    Ly.prototype.getFeaturesAtCoordinate = Ly.prototype.Oa;
    Ly.prototype.getClosestFeatureToCoordinate = Ly.prototype.ab;
    Ly.prototype.getExtent = Ly.prototype.J;
    Ly.prototype.getFeatureById = Ly.prototype.Na;
    Ly.prototype.removeFeature = Ly.prototype.fb;
    Ly.prototype.getAttributions = Ly.prototype.Y;
    Ly.prototype.getLogo = Ly.prototype.X;
    Ly.prototype.getProjection = Ly.prototype.Z;
    Ly.prototype.getState = Ly.prototype.$;
    Ly.prototype.bindTo = Ly.prototype.K;
    Ly.prototype.get = Ly.prototype.get;
    Ly.prototype.getKeys = Ly.prototype.G;
    Ly.prototype.getProperties = Ly.prototype.I;
    Ly.prototype.set = Ly.prototype.set;
    Ly.prototype.setProperties = Ly.prototype.C;
    Ly.prototype.unbind = Ly.prototype.L;
    Ly.prototype.unbindAll = Ly.prototype.M;
    Ly.prototype.changed = Ly.prototype.l;
    Ly.prototype.getRevision = Ly.prototype.u;
    Ly.prototype.on = Ly.prototype.s;
    Ly.prototype.once = Ly.prototype.v;
    Ly.prototype.un = Ly.prototype.t;
    Ly.prototype.unByKey = Ly.prototype.A;
    $.prototype.addFeature = $.prototype.Va;
    $.prototype.addFeatures = $.prototype.Ga;
    $.prototype.forEachFeature = $.prototype.$a;
    $.prototype.forEachFeatureInExtent = $.prototype.wa;
    $.prototype.forEachFeatureIntersectingExtent = $.prototype.Ma;
    $.prototype.getFeatures = $.prototype.Aa;
    $.prototype.getFeaturesAtCoordinate = $.prototype.Oa;
    $.prototype.getClosestFeatureToCoordinate = $.prototype.ab;
    $.prototype.getExtent = $.prototype.J;
    $.prototype.getFeatureById = $.prototype.Na;
    $.prototype.removeFeature = $.prototype.fb;
    $.prototype.getAttributions = $.prototype.Y;
    $.prototype.getLogo = $.prototype.X;
    $.prototype.getProjection = $.prototype.Z;
    $.prototype.getState = $.prototype.$;
    $.prototype.bindTo = $.prototype.K;
    $.prototype.get = $.prototype.get;
    $.prototype.getKeys = $.prototype.G;
    $.prototype.getProperties = $.prototype.I;
    $.prototype.set = $.prototype.set;
    $.prototype.setProperties = $.prototype.C;
    $.prototype.unbind = $.prototype.L;
    $.prototype.unbindAll = $.prototype.M;
    $.prototype.changed = $.prototype.l;
    $.prototype.getRevision = $.prototype.u;
    $.prototype.on = $.prototype.s;
    $.prototype.once = $.prototype.v;
    $.prototype.un = $.prototype.t;
    $.prototype.unByKey = $.prototype.A;
    Oy.prototype.setTileUrlFunction = Oy.prototype.ta;
    Oy.prototype.setUrl = Oy.prototype.b;
    Oy.prototype.getTileLoadFunction = Oy.prototype.bb;
    Oy.prototype.getTileUrlFunction = Oy.prototype.cb;
    Oy.prototype.setTileLoadFunction = Oy.prototype.jb;
    Oy.prototype.getTileGrid = Oy.prototype.xa;
    Oy.prototype.getAttributions = Oy.prototype.Y;
    Oy.prototype.getLogo = Oy.prototype.X;
    Oy.prototype.getProjection = Oy.prototype.Z;
    Oy.prototype.getState = Oy.prototype.$;
    Oy.prototype.bindTo = Oy.prototype.K;
    Oy.prototype.get = Oy.prototype.get;
    Oy.prototype.getKeys = Oy.prototype.G;
    Oy.prototype.getProperties = Oy.prototype.I;
    Oy.prototype.set = Oy.prototype.set;
    Oy.prototype.setProperties = Oy.prototype.C;
    Oy.prototype.unbind = Oy.prototype.L;
    Oy.prototype.unbindAll = Oy.prototype.M;
    Oy.prototype.changed = Oy.prototype.l;
    Oy.prototype.getRevision = Oy.prototype.u;
    Oy.prototype.on = Oy.prototype.s;
    Oy.prototype.once = Oy.prototype.v;
    Oy.prototype.un = Oy.prototype.t;
    Oy.prototype.unByKey = Oy.prototype.A;
    Qy.prototype.getTileLoadFunction = Qy.prototype.bb;
    Qy.prototype.getTileUrlFunction = Qy.prototype.cb;
    Qy.prototype.setTileLoadFunction = Qy.prototype.jb;
    Qy.prototype.setTileUrlFunction = Qy.prototype.ta;
    Qy.prototype.getTileGrid = Qy.prototype.xa;
    Qy.prototype.getAttributions = Qy.prototype.Y;
    Qy.prototype.getLogo = Qy.prototype.X;
    Qy.prototype.getProjection = Qy.prototype.Z;
    Qy.prototype.getState = Qy.prototype.$;
    Qy.prototype.bindTo = Qy.prototype.K;
    Qy.prototype.get = Qy.prototype.get;
    Qy.prototype.getKeys = Qy.prototype.G;
    Qy.prototype.getProperties = Qy.prototype.I;
    Qy.prototype.set = Qy.prototype.set;
    Qy.prototype.setProperties = Qy.prototype.C;
    Qy.prototype.unbind = Qy.prototype.L;
    Qy.prototype.unbindAll = Qy.prototype.M;
    Qy.prototype.changed = Qy.prototype.l;
    Qy.prototype.getRevision = Qy.prototype.u;
    Qy.prototype.on = Qy.prototype.s;
    Qy.prototype.once = Qy.prototype.v;
    Qy.prototype.un = Qy.prototype.t;
    Qy.prototype.unByKey = Qy.prototype.A;
    Sy.prototype.getTileGrid = Sy.prototype.xa;
    Sy.prototype.getAttributions = Sy.prototype.Y;
    Sy.prototype.getLogo = Sy.prototype.X;
    Sy.prototype.getProjection = Sy.prototype.Z;
    Sy.prototype.getState = Sy.prototype.$;
    Sy.prototype.bindTo = Sy.prototype.K;
    Sy.prototype.get = Sy.prototype.get;
    Sy.prototype.getKeys = Sy.prototype.G;
    Sy.prototype.getProperties = Sy.prototype.I;
    Sy.prototype.set = Sy.prototype.set;
    Sy.prototype.setProperties = Sy.prototype.C;
    Sy.prototype.unbind = Sy.prototype.L;
    Sy.prototype.unbindAll = Sy.prototype.M;
    Sy.prototype.changed = Sy.prototype.l;
    Sy.prototype.getRevision = Sy.prototype.u;
    Sy.prototype.on = Sy.prototype.s;
    Sy.prototype.once = Sy.prototype.v;
    Sy.prototype.un = Sy.prototype.t;
    Sy.prototype.unByKey = Sy.prototype.A;
    Ty.prototype.getTileLoadFunction = Ty.prototype.bb;
    Ty.prototype.getTileUrlFunction = Ty.prototype.cb;
    Ty.prototype.setTileLoadFunction = Ty.prototype.jb;
    Ty.prototype.setTileUrlFunction = Ty.prototype.ta;
    Ty.prototype.getTileGrid = Ty.prototype.xa;
    Ty.prototype.getAttributions = Ty.prototype.Y;
    Ty.prototype.getLogo = Ty.prototype.X;
    Ty.prototype.getProjection = Ty.prototype.Z;
    Ty.prototype.getState = Ty.prototype.$;
    Ty.prototype.bindTo = Ty.prototype.K;
    Ty.prototype.get = Ty.prototype.get;
    Ty.prototype.getKeys = Ty.prototype.G;
    Ty.prototype.getProperties = Ty.prototype.I;
    Ty.prototype.set = Ty.prototype.set;
    Ty.prototype.setProperties = Ty.prototype.C;
    Ty.prototype.unbind = Ty.prototype.L;
    Ty.prototype.unbindAll = Ty.prototype.M;
    Ty.prototype.changed = Ty.prototype.l;
    Ty.prototype.getRevision = Ty.prototype.u;
    Ty.prototype.on = Ty.prototype.s;
    Ty.prototype.once = Ty.prototype.v;
    Ty.prototype.un = Ty.prototype.t;
    Ty.prototype.unByKey = Ty.prototype.A;
    Uy.prototype.getTileGrid = Uy.prototype.xa;
    Uy.prototype.getAttributions = Uy.prototype.Y;
    Uy.prototype.getLogo = Uy.prototype.X;
    Uy.prototype.getProjection = Uy.prototype.Z;
    Uy.prototype.getState = Uy.prototype.$;
    Uy.prototype.bindTo = Uy.prototype.K;
    Uy.prototype.get = Uy.prototype.get;
    Uy.prototype.getKeys = Uy.prototype.G;
    Uy.prototype.getProperties = Uy.prototype.I;
    Uy.prototype.set = Uy.prototype.set;
    Uy.prototype.setProperties = Uy.prototype.C;
    Uy.prototype.unbind = Uy.prototype.L;
    Uy.prototype.unbindAll = Uy.prototype.M;
    Uy.prototype.changed = Uy.prototype.l;
    Uy.prototype.getRevision = Uy.prototype.u;
    Uy.prototype.on = Uy.prototype.s;
    Uy.prototype.once = Uy.prototype.v;
    Uy.prototype.un = Uy.prototype.t;
    Uy.prototype.unByKey = Uy.prototype.A;
    Zy.prototype.readFeatures = Zy.prototype.a;
    Zy.prototype.forEachFeatureIntersectingExtent = Zy.prototype.Ma;
    Zy.prototype.getFeaturesAtCoordinate = Zy.prototype.Oa;
    Zy.prototype.getFeatureById = Zy.prototype.Na;
    Zy.prototype.getAttributions = Zy.prototype.Y;
    Zy.prototype.getLogo = Zy.prototype.X;
    Zy.prototype.getProjection = Zy.prototype.Z;
    Zy.prototype.getState = Zy.prototype.$;
    Zy.prototype.bindTo = Zy.prototype.K;
    Zy.prototype.get = Zy.prototype.get;
    Zy.prototype.getKeys = Zy.prototype.G;
    Zy.prototype.getProperties = Zy.prototype.I;
    Zy.prototype.set = Zy.prototype.set;
    Zy.prototype.setProperties = Zy.prototype.C;
    Zy.prototype.unbind = Zy.prototype.L;
    Zy.prototype.unbindAll = Zy.prototype.M;
    Zy.prototype.changed = Zy.prototype.l;
    Zy.prototype.getRevision = Zy.prototype.u;
    Zy.prototype.on = Zy.prototype.s;
    Zy.prototype.once = Zy.prototype.v;
    Zy.prototype.un = Zy.prototype.t;
    Zy.prototype.unByKey = Zy.prototype.A;
    az.prototype.getTileLoadFunction = az.prototype.bb;
    az.prototype.getTileUrlFunction = az.prototype.cb;
    az.prototype.setTileLoadFunction = az.prototype.jb;
    az.prototype.setTileUrlFunction = az.prototype.ta;
    az.prototype.getTileGrid = az.prototype.xa;
    az.prototype.getAttributions = az.prototype.Y;
    az.prototype.getLogo = az.prototype.X;
    az.prototype.getProjection = az.prototype.Z;
    az.prototype.getState = az.prototype.$;
    az.prototype.bindTo = az.prototype.K;
    az.prototype.get = az.prototype.get;
    az.prototype.getKeys = az.prototype.G;
    az.prototype.getProperties = az.prototype.I;
    az.prototype.set = az.prototype.set;
    az.prototype.setProperties = az.prototype.C;
    az.prototype.unbind = az.prototype.L;
    az.prototype.unbindAll = az.prototype.M;
    az.prototype.changed = az.prototype.l;
    az.prototype.getRevision = az.prototype.u;
    az.prototype.on = az.prototype.s;
    az.prototype.once = az.prototype.v;
    az.prototype.un = az.prototype.t;
    az.prototype.unByKey = az.prototype.A;
    ez.prototype.readFeatures = ez.prototype.a;
    ez.prototype.addFeature = ez.prototype.Va;
    ez.prototype.addFeatures = ez.prototype.Ga;
    ez.prototype.clear = ez.prototype.clear;
    ez.prototype.forEachFeature = ez.prototype.$a;
    ez.prototype.forEachFeatureInExtent = ez.prototype.wa;
    ez.prototype.forEachFeatureIntersectingExtent = ez.prototype.Ma;
    ez.prototype.getFeatures = ez.prototype.Aa;
    ez.prototype.getFeaturesAtCoordinate = ez.prototype.Oa;
    ez.prototype.getClosestFeatureToCoordinate = ez.prototype.ab;
    ez.prototype.getExtent = ez.prototype.J;
    ez.prototype.getFeatureById = ez.prototype.Na;
    ez.prototype.removeFeature = ez.prototype.fb;
    ez.prototype.getAttributions = ez.prototype.Y;
    ez.prototype.getLogo = ez.prototype.X;
    ez.prototype.getProjection = ez.prototype.Z;
    ez.prototype.getState = ez.prototype.$;
    ez.prototype.bindTo = ez.prototype.K;
    ez.prototype.get = ez.prototype.get;
    ez.prototype.getKeys = ez.prototype.G;
    ez.prototype.getProperties = ez.prototype.I;
    ez.prototype.set = ez.prototype.set;
    ez.prototype.setProperties = ez.prototype.C;
    ez.prototype.unbind = ez.prototype.L;
    ez.prototype.unbindAll = ez.prototype.M;
    ez.prototype.changed = ez.prototype.l;
    ez.prototype.getRevision = ez.prototype.u;
    ez.prototype.on = ez.prototype.s;
    ez.prototype.once = ez.prototype.v;
    ez.prototype.un = ez.prototype.t;
    ez.prototype.unByKey = ez.prototype.A;
    hz.prototype.getTileLoadFunction = hz.prototype.bb;
    hz.prototype.getTileUrlFunction = hz.prototype.cb;
    hz.prototype.setTileLoadFunction = hz.prototype.jb;
    hz.prototype.setTileUrlFunction = hz.prototype.ta;
    hz.prototype.getTileGrid = hz.prototype.xa;
    hz.prototype.getAttributions = hz.prototype.Y;
    hz.prototype.getLogo = hz.prototype.X;
    hz.prototype.getProjection = hz.prototype.Z;
    hz.prototype.getState = hz.prototype.$;
    hz.prototype.bindTo = hz.prototype.K;
    hz.prototype.get = hz.prototype.get;
    hz.prototype.getKeys = hz.prototype.G;
    hz.prototype.getProperties = hz.prototype.I;
    hz.prototype.set = hz.prototype.set;
    hz.prototype.setProperties = hz.prototype.C;
    hz.prototype.unbind = hz.prototype.L;
    hz.prototype.unbindAll = hz.prototype.M;
    hz.prototype.changed = hz.prototype.l;
    hz.prototype.getRevision = hz.prototype.u;
    hz.prototype.on = hz.prototype.s;
    hz.prototype.once = hz.prototype.v;
    hz.prototype.un = hz.prototype.t;
    hz.prototype.unByKey = hz.prototype.A;
    kz.prototype.getTileLoadFunction = kz.prototype.bb;
    kz.prototype.getTileUrlFunction = kz.prototype.cb;
    kz.prototype.setTileLoadFunction = kz.prototype.jb;
    kz.prototype.setTileUrlFunction = kz.prototype.ta;
    kz.prototype.getTileGrid = kz.prototype.xa;
    kz.prototype.getAttributions = kz.prototype.Y;
    kz.prototype.getLogo = kz.prototype.X;
    kz.prototype.getProjection = kz.prototype.Z;
    kz.prototype.getState = kz.prototype.$;
    kz.prototype.bindTo = kz.prototype.K;
    kz.prototype.get = kz.prototype.get;
    kz.prototype.getKeys = kz.prototype.G;
    kz.prototype.getProperties = kz.prototype.I;
    kz.prototype.set = kz.prototype.set;
    kz.prototype.setProperties = kz.prototype.C;
    kz.prototype.unbind = kz.prototype.L;
    kz.prototype.unbindAll = kz.prototype.M;
    kz.prototype.changed = kz.prototype.l;
    kz.prototype.getRevision = kz.prototype.u;
    kz.prototype.on = kz.prototype.s;
    kz.prototype.once = kz.prototype.v;
    kz.prototype.un = kz.prototype.t;
    kz.prototype.unByKey = kz.prototype.A;
    lj.prototype.changed = lj.prototype.l;
    lj.prototype.getRevision = lj.prototype.u;
    lj.prototype.on = lj.prototype.s;
    lj.prototype.once = lj.prototype.v;
    lj.prototype.un = lj.prototype.t;
    lj.prototype.unByKey = lj.prototype.A;
    to.prototype.changed = to.prototype.l;
    to.prototype.getRevision = to.prototype.u;
    to.prototype.on = to.prototype.s;
    to.prototype.once = to.prototype.v;
    to.prototype.un = to.prototype.t;
    to.prototype.unByKey = to.prototype.A;
    wo.prototype.changed = wo.prototype.l;
    wo.prototype.getRevision = wo.prototype.u;
    wo.prototype.on = wo.prototype.s;
    wo.prototype.once = wo.prototype.v;
    wo.prototype.un = wo.prototype.t;
    wo.prototype.unByKey = wo.prototype.A;
    Co.prototype.changed = Co.prototype.l;
    Co.prototype.getRevision = Co.prototype.u;
    Co.prototype.on = Co.prototype.s;
    Co.prototype.once = Co.prototype.v;
    Co.prototype.un = Co.prototype.t;
    Co.prototype.unByKey = Co.prototype.A;
    Eo.prototype.changed = Eo.prototype.l;
    Eo.prototype.getRevision = Eo.prototype.u;
    Eo.prototype.on = Eo.prototype.s;
    Eo.prototype.once = Eo.prototype.v;
    Eo.prototype.un = Eo.prototype.t;
    Eo.prototype.unByKey = Eo.prototype.A;
    zn.prototype.changed = zn.prototype.l;
    zn.prototype.getRevision = zn.prototype.u;
    zn.prototype.on = zn.prototype.s;
    zn.prototype.once = zn.prototype.v;
    zn.prototype.un = zn.prototype.t;
    zn.prototype.unByKey = zn.prototype.A;
    An.prototype.changed = An.prototype.l;
    An.prototype.getRevision = An.prototype.u;
    An.prototype.on = An.prototype.s;
    An.prototype.once = An.prototype.v;
    An.prototype.un = An.prototype.t;
    An.prototype.unByKey = An.prototype.A;
    Bn.prototype.changed = Bn.prototype.l;
    Bn.prototype.getRevision = Bn.prototype.u;
    Bn.prototype.on = Bn.prototype.s;
    Bn.prototype.once = Bn.prototype.v;
    Bn.prototype.un = Bn.prototype.t;
    Bn.prototype.unByKey = Bn.prototype.A;
    Dn.prototype.changed = Dn.prototype.l;
    Dn.prototype.getRevision = Dn.prototype.u;
    Dn.prototype.on = Dn.prototype.s;
    Dn.prototype.once = Dn.prototype.v;
    Dn.prototype.un = Dn.prototype.t;
    Dn.prototype.unByKey = Dn.prototype.A;
    Am.prototype.changed = Am.prototype.l;
    Am.prototype.getRevision = Am.prototype.u;
    Am.prototype.on = Am.prototype.s;
    Am.prototype.once = Am.prototype.v;
    Am.prototype.un = Am.prototype.t;
    Am.prototype.unByKey = Am.prototype.A;
    un.prototype.changed = un.prototype.l;
    un.prototype.getRevision = un.prototype.u;
    un.prototype.on = un.prototype.s;
    un.prototype.once = un.prototype.v;
    un.prototype.un = un.prototype.t;
    un.prototype.unByKey = un.prototype.A;
    vn.prototype.changed = vn.prototype.l;
    vn.prototype.getRevision = vn.prototype.u;
    vn.prototype.on = vn.prototype.s;
    vn.prototype.once = vn.prototype.v;
    vn.prototype.un = vn.prototype.t;
    vn.prototype.unByKey = vn.prototype.A;
    wn.prototype.changed = wn.prototype.l;
    wn.prototype.getRevision = wn.prototype.u;
    wn.prototype.on = wn.prototype.s;
    wn.prototype.once = wn.prototype.v;
    wn.prototype.un = wn.prototype.t;
    wn.prototype.unByKey = wn.prototype.A;
    C.prototype.bindTo = C.prototype.K;
    C.prototype.get = C.prototype.get;
    C.prototype.getKeys = C.prototype.G;
    C.prototype.getProperties = C.prototype.I;
    C.prototype.set = C.prototype.set;
    C.prototype.setProperties = C.prototype.C;
    C.prototype.unbind = C.prototype.L;
    C.prototype.unbindAll = C.prototype.M;
    C.prototype.changed = C.prototype.l;
    C.prototype.getRevision = C.prototype.u;
    C.prototype.on = C.prototype.s;
    C.prototype.once = C.prototype.v;
    C.prototype.un = C.prototype.t;
    C.prototype.unByKey = C.prototype.A;
    E.prototype.getBrightness = E.prototype.c;
    E.prototype.getContrast = E.prototype.f;
    E.prototype.getHue = E.prototype.e;
    E.prototype.getExtent = E.prototype.J;
    E.prototype.getMaxResolution = E.prototype.g;
    E.prototype.getMinResolution = E.prototype.i;
    E.prototype.getOpacity = E.prototype.q;
    E.prototype.getSaturation = E.prototype.j;
    E.prototype.getVisible = E.prototype.b;
    E.prototype.setBrightness = E.prototype.D;
    E.prototype.setContrast = E.prototype.H;
    E.prototype.setHue = E.prototype.N;
    E.prototype.setExtent = E.prototype.o;
    E.prototype.setMaxResolution = E.prototype.S;
    E.prototype.setMinResolution = E.prototype.W;
    E.prototype.setOpacity = E.prototype.p;
    E.prototype.setSaturation = E.prototype.ca;
    E.prototype.setVisible = E.prototype.da;
    E.prototype.bindTo = E.prototype.K;
    E.prototype.get = E.prototype.get;
    E.prototype.getKeys = E.prototype.G;
    E.prototype.getProperties = E.prototype.I;
    E.prototype.set = E.prototype.set;
    E.prototype.setProperties = E.prototype.C;
    E.prototype.unbind = E.prototype.L;
    E.prototype.unbindAll = E.prototype.M;
    E.prototype.changed = E.prototype.l;
    E.prototype.getRevision = E.prototype.u;
    E.prototype.on = E.prototype.s;
    E.prototype.once = E.prototype.v;
    E.prototype.un = E.prototype.t;
    E.prototype.unByKey = E.prototype.A;
    J.prototype.setSource = J.prototype.fa;
    J.prototype.getBrightness = J.prototype.c;
    J.prototype.getContrast = J.prototype.f;
    J.prototype.getHue = J.prototype.e;
    J.prototype.getExtent = J.prototype.J;
    J.prototype.getMaxResolution = J.prototype.g;
    J.prototype.getMinResolution = J.prototype.i;
    J.prototype.getOpacity = J.prototype.q;
    J.prototype.getSaturation = J.prototype.j;
    J.prototype.getVisible = J.prototype.b;
    J.prototype.setBrightness = J.prototype.D;
    J.prototype.setContrast = J.prototype.H;
    J.prototype.setHue = J.prototype.N;
    J.prototype.setExtent = J.prototype.o;
    J.prototype.setMaxResolution = J.prototype.S;
    J.prototype.setMinResolution = J.prototype.W;
    J.prototype.setOpacity = J.prototype.p;
    J.prototype.setSaturation = J.prototype.ca;
    J.prototype.setVisible = J.prototype.da;
    J.prototype.bindTo = J.prototype.K;
    J.prototype.get = J.prototype.get;
    J.prototype.getKeys = J.prototype.G;
    J.prototype.getProperties = J.prototype.I;
    J.prototype.set = J.prototype.set;
    J.prototype.setProperties = J.prototype.C;
    J.prototype.unbind = J.prototype.L;
    J.prototype.unbindAll = J.prototype.M;
    J.prototype.changed = J.prototype.l;
    J.prototype.getRevision = J.prototype.u;
    J.prototype.on = J.prototype.s;
    J.prototype.once = J.prototype.v;
    J.prototype.un = J.prototype.t;
    J.prototype.unByKey = J.prototype.A;
    X.prototype.getSource = X.prototype.a;
    X.prototype.getStyle = X.prototype.af;
    X.prototype.getStyleFunction = X.prototype.df;
    X.prototype.setStyle = X.prototype.ka;
    X.prototype.setSource = X.prototype.fa;
    X.prototype.getBrightness = X.prototype.c;
    X.prototype.getContrast = X.prototype.f;
    X.prototype.getHue = X.prototype.e;
    X.prototype.getExtent = X.prototype.J;
    X.prototype.getMaxResolution = X.prototype.g;
    X.prototype.getMinResolution = X.prototype.i;
    X.prototype.getOpacity = X.prototype.q;
    X.prototype.getSaturation = X.prototype.j;
    X.prototype.getVisible = X.prototype.b;
    X.prototype.setBrightness = X.prototype.D;
    X.prototype.setContrast = X.prototype.H;
    X.prototype.setHue = X.prototype.N;
    X.prototype.setExtent = X.prototype.o;
    X.prototype.setMaxResolution = X.prototype.S;
    X.prototype.setMinResolution = X.prototype.W;
    X.prototype.setOpacity = X.prototype.p;
    X.prototype.setSaturation = X.prototype.ca;
    X.prototype.setVisible = X.prototype.da;
    X.prototype.bindTo = X.prototype.K;
    X.prototype.get = X.prototype.get;
    X.prototype.getKeys = X.prototype.G;
    X.prototype.getProperties = X.prototype.I;
    X.prototype.set = X.prototype.set;
    X.prototype.setProperties = X.prototype.C;
    X.prototype.unbind = X.prototype.L;
    X.prototype.unbindAll = X.prototype.M;
    X.prototype.changed = X.prototype.l;
    X.prototype.getRevision = X.prototype.u;
    X.prototype.on = X.prototype.s;
    X.prototype.once = X.prototype.v;
    X.prototype.un = X.prototype.t;
    X.prototype.unByKey = X.prototype.A;
    H.prototype.setSource = H.prototype.fa;
    H.prototype.getBrightness = H.prototype.c;
    H.prototype.getContrast = H.prototype.f;
    H.prototype.getHue = H.prototype.e;
    H.prototype.getExtent = H.prototype.J;
    H.prototype.getMaxResolution = H.prototype.g;
    H.prototype.getMinResolution = H.prototype.i;
    H.prototype.getOpacity = H.prototype.q;
    H.prototype.getSaturation = H.prototype.j;
    H.prototype.getVisible = H.prototype.b;
    H.prototype.setBrightness = H.prototype.D;
    H.prototype.setContrast = H.prototype.H;
    H.prototype.setHue = H.prototype.N;
    H.prototype.setExtent = H.prototype.o;
    H.prototype.setMaxResolution = H.prototype.S;
    H.prototype.setMinResolution = H.prototype.W;
    H.prototype.setOpacity = H.prototype.p;
    H.prototype.setSaturation = H.prototype.ca;
    H.prototype.setVisible = H.prototype.da;
    H.prototype.bindTo = H.prototype.K;
    H.prototype.get = H.prototype.get;
    H.prototype.getKeys = H.prototype.G;
    H.prototype.getProperties = H.prototype.I;
    H.prototype.set = H.prototype.set;
    H.prototype.setProperties = H.prototype.C;
    H.prototype.unbind = H.prototype.L;
    H.prototype.unbindAll = H.prototype.M;
    H.prototype.changed = H.prototype.l;
    H.prototype.getRevision = H.prototype.u;
    H.prototype.on = H.prototype.s;
    H.prototype.once = H.prototype.v;
    H.prototype.un = H.prototype.t;
    H.prototype.unByKey = H.prototype.A;
    G.prototype.getBrightness = G.prototype.c;
    G.prototype.getContrast = G.prototype.f;
    G.prototype.getHue = G.prototype.e;
    G.prototype.getExtent = G.prototype.J;
    G.prototype.getMaxResolution = G.prototype.g;
    G.prototype.getMinResolution = G.prototype.i;
    G.prototype.getOpacity = G.prototype.q;
    G.prototype.getSaturation = G.prototype.j;
    G.prototype.getVisible = G.prototype.b;
    G.prototype.setBrightness = G.prototype.D;
    G.prototype.setContrast = G.prototype.H;
    G.prototype.setHue = G.prototype.N;
    G.prototype.setExtent = G.prototype.o;
    G.prototype.setMaxResolution = G.prototype.S;
    G.prototype.setMinResolution = G.prototype.W;
    G.prototype.setOpacity = G.prototype.p;
    G.prototype.setSaturation = G.prototype.ca;
    G.prototype.setVisible = G.prototype.da;
    G.prototype.bindTo = G.prototype.K;
    G.prototype.get = G.prototype.get;
    G.prototype.getKeys = G.prototype.G;
    G.prototype.getProperties = G.prototype.I;
    G.prototype.set = G.prototype.set;
    G.prototype.setProperties = G.prototype.C;
    G.prototype.unbind = G.prototype.L;
    G.prototype.unbindAll = G.prototype.M;
    G.prototype.changed = G.prototype.l;
    G.prototype.getRevision = G.prototype.u;
    G.prototype.on = G.prototype.s;
    G.prototype.once = G.prototype.v;
    G.prototype.un = G.prototype.t;
    G.prototype.unByKey = G.prototype.A;
    I.prototype.setSource = I.prototype.fa;
    I.prototype.getBrightness = I.prototype.c;
    I.prototype.getContrast = I.prototype.f;
    I.prototype.getHue = I.prototype.e;
    I.prototype.getExtent = I.prototype.J;
    I.prototype.getMaxResolution = I.prototype.g;
    I.prototype.getMinResolution = I.prototype.i;
    I.prototype.getOpacity = I.prototype.q;
    I.prototype.getSaturation = I.prototype.j;
    I.prototype.getVisible = I.prototype.b;
    I.prototype.setBrightness = I.prototype.D;
    I.prototype.setContrast = I.prototype.H;
    I.prototype.setHue = I.prototype.N;
    I.prototype.setExtent = I.prototype.o;
    I.prototype.setMaxResolution = I.prototype.S;
    I.prototype.setMinResolution = I.prototype.W;
    I.prototype.setOpacity = I.prototype.p;
    I.prototype.setSaturation = I.prototype.ca;
    I.prototype.setVisible = I.prototype.da;
    I.prototype.bindTo = I.prototype.K;
    I.prototype.get = I.prototype.get;
    I.prototype.getKeys = I.prototype.G;
    I.prototype.getProperties = I.prototype.I;
    I.prototype.set = I.prototype.set;
    I.prototype.setProperties = I.prototype.C;
    I.prototype.unbind = I.prototype.L;
    I.prototype.unbindAll = I.prototype.M;
    I.prototype.changed = I.prototype.l;
    I.prototype.getRevision = I.prototype.u;
    I.prototype.on = I.prototype.s;
    I.prototype.once = I.prototype.v;
    I.prototype.un = I.prototype.t;
    I.prototype.unByKey = I.prototype.A;
    Nj.prototype.bindTo = Nj.prototype.K;
    Nj.prototype.get = Nj.prototype.get;
    Nj.prototype.getKeys = Nj.prototype.G;
    Nj.prototype.getProperties = Nj.prototype.I;
    Nj.prototype.set = Nj.prototype.set;
    Nj.prototype.setProperties = Nj.prototype.C;
    Nj.prototype.unbind = Nj.prototype.L;
    Nj.prototype.unbindAll = Nj.prototype.M;
    Nj.prototype.changed = Nj.prototype.l;
    Nj.prototype.getRevision = Nj.prototype.u;
    Nj.prototype.on = Nj.prototype.s;
    Nj.prototype.once = Nj.prototype.v;
    Nj.prototype.un = Nj.prototype.t;
    Nj.prototype.unByKey = Nj.prototype.A;
    Rj.prototype.getActive = Rj.prototype.a;
    Rj.prototype.setActive = Rj.prototype.b;
    Rj.prototype.bindTo = Rj.prototype.K;
    Rj.prototype.get = Rj.prototype.get;
    Rj.prototype.getKeys = Rj.prototype.G;
    Rj.prototype.getProperties = Rj.prototype.I;
    Rj.prototype.set = Rj.prototype.set;
    Rj.prototype.setProperties = Rj.prototype.C;
    Rj.prototype.unbind = Rj.prototype.L;
    Rj.prototype.unbindAll = Rj.prototype.M;
    Rj.prototype.changed = Rj.prototype.l;
    Rj.prototype.getRevision = Rj.prototype.u;
    Rj.prototype.on = Rj.prototype.s;
    Rj.prototype.once = Rj.prototype.v;
    Rj.prototype.un = Rj.prototype.t;
    Rj.prototype.unByKey = Rj.prototype.A;
    Ow.prototype.getActive = Ow.prototype.a;
    Ow.prototype.setActive = Ow.prototype.b;
    Ow.prototype.bindTo = Ow.prototype.K;
    Ow.prototype.get = Ow.prototype.get;
    Ow.prototype.getKeys = Ow.prototype.G;
    Ow.prototype.getProperties = Ow.prototype.I;
    Ow.prototype.set = Ow.prototype.set;
    Ow.prototype.setProperties = Ow.prototype.C;
    Ow.prototype.unbind = Ow.prototype.L;
    Ow.prototype.unbindAll = Ow.prototype.M;
    Ow.prototype.changed = Ow.prototype.l;
    Ow.prototype.getRevision = Ow.prototype.u;
    Ow.prototype.on = Ow.prototype.s;
    Ow.prototype.once = Ow.prototype.v;
    Ow.prototype.un = Ow.prototype.t;
    Ow.prototype.unByKey = Ow.prototype.A;
    ak.prototype.getActive = ak.prototype.a;
    ak.prototype.setActive = ak.prototype.b;
    ak.prototype.bindTo = ak.prototype.K;
    ak.prototype.get = ak.prototype.get;
    ak.prototype.getKeys = ak.prototype.G;
    ak.prototype.getProperties = ak.prototype.I;
    ak.prototype.set = ak.prototype.set;
    ak.prototype.setProperties = ak.prototype.C;
    ak.prototype.unbind = ak.prototype.L;
    ak.prototype.unbindAll = ak.prototype.M;
    ak.prototype.changed = ak.prototype.l;
    ak.prototype.getRevision = ak.prototype.u;
    ak.prototype.on = ak.prototype.s;
    ak.prototype.once = ak.prototype.v;
    ak.prototype.un = ak.prototype.t;
    ak.prototype.unByKey = ak.prototype.A;
    dl.prototype.getActive = dl.prototype.a;
    dl.prototype.setActive = dl.prototype.b;
    dl.prototype.bindTo = dl.prototype.K;
    dl.prototype.get = dl.prototype.get;
    dl.prototype.getKeys = dl.prototype.G;
    dl.prototype.getProperties = dl.prototype.I;
    dl.prototype.set = dl.prototype.set;
    dl.prototype.setProperties = dl.prototype.C;
    dl.prototype.unbind = dl.prototype.L;
    dl.prototype.unbindAll = dl.prototype.M;
    dl.prototype.changed = dl.prototype.l;
    dl.prototype.getRevision = dl.prototype.u;
    dl.prototype.on = dl.prototype.s;
    dl.prototype.once = dl.prototype.v;
    dl.prototype.un = dl.prototype.t;
    dl.prototype.unByKey = dl.prototype.A;
    dk.prototype.getActive = dk.prototype.a;
    dk.prototype.setActive = dk.prototype.b;
    dk.prototype.bindTo = dk.prototype.K;
    dk.prototype.get = dk.prototype.get;
    dk.prototype.getKeys = dk.prototype.G;
    dk.prototype.getProperties = dk.prototype.I;
    dk.prototype.set = dk.prototype.set;
    dk.prototype.setProperties = dk.prototype.C;
    dk.prototype.unbind = dk.prototype.L;
    dk.prototype.unbindAll = dk.prototype.M;
    dk.prototype.changed = dk.prototype.l;
    dk.prototype.getRevision = dk.prototype.u;
    dk.prototype.on = dk.prototype.s;
    dk.prototype.once = dk.prototype.v;
    dk.prototype.un = dk.prototype.t;
    dk.prototype.unByKey = dk.prototype.A;
    Sw.prototype.getActive = Sw.prototype.a;
    Sw.prototype.setActive = Sw.prototype.b;
    Sw.prototype.bindTo = Sw.prototype.K;
    Sw.prototype.get = Sw.prototype.get;
    Sw.prototype.getKeys = Sw.prototype.G;
    Sw.prototype.getProperties = Sw.prototype.I;
    Sw.prototype.set = Sw.prototype.set;
    Sw.prototype.setProperties = Sw.prototype.C;
    Sw.prototype.unbind = Sw.prototype.L;
    Sw.prototype.unbindAll = Sw.prototype.M;
    Sw.prototype.changed = Sw.prototype.l;
    Sw.prototype.getRevision = Sw.prototype.u;
    Sw.prototype.on = Sw.prototype.s;
    Sw.prototype.once = Sw.prototype.v;
    Sw.prototype.un = Sw.prototype.t;
    Sw.prototype.unByKey = Sw.prototype.A;
    hk.prototype.getActive = hk.prototype.a;
    hk.prototype.setActive = hk.prototype.b;
    hk.prototype.bindTo = hk.prototype.K;
    hk.prototype.get = hk.prototype.get;
    hk.prototype.getKeys = hk.prototype.G;
    hk.prototype.getProperties = hk.prototype.I;
    hk.prototype.set = hk.prototype.set;
    hk.prototype.setProperties = hk.prototype.C;
    hk.prototype.unbind = hk.prototype.L;
    hk.prototype.unbindAll = hk.prototype.M;
    hk.prototype.changed = hk.prototype.l;
    hk.prototype.getRevision = hk.prototype.u;
    hk.prototype.on = hk.prototype.s;
    hk.prototype.once = hk.prototype.v;
    hk.prototype.un = hk.prototype.t;
    hk.prototype.unByKey = hk.prototype.A;
    wl.prototype.getGeometry = wl.prototype.R;
    wl.prototype.getActive = wl.prototype.a;
    wl.prototype.setActive = wl.prototype.b;
    wl.prototype.bindTo = wl.prototype.K;
    wl.prototype.get = wl.prototype.get;
    wl.prototype.getKeys = wl.prototype.G;
    wl.prototype.getProperties = wl.prototype.I;
    wl.prototype.set = wl.prototype.set;
    wl.prototype.setProperties = wl.prototype.C;
    wl.prototype.unbind = wl.prototype.L;
    wl.prototype.unbindAll = wl.prototype.M;
    wl.prototype.changed = wl.prototype.l;
    wl.prototype.getRevision = wl.prototype.u;
    wl.prototype.on = wl.prototype.s;
    wl.prototype.once = wl.prototype.v;
    wl.prototype.un = wl.prototype.t;
    wl.prototype.unByKey = wl.prototype.A;
    Xw.prototype.getActive = Xw.prototype.a;
    Xw.prototype.setActive = Xw.prototype.b;
    Xw.prototype.bindTo = Xw.prototype.K;
    Xw.prototype.get = Xw.prototype.get;
    Xw.prototype.getKeys = Xw.prototype.G;
    Xw.prototype.getProperties = Xw.prototype.I;
    Xw.prototype.set = Xw.prototype.set;
    Xw.prototype.setProperties = Xw.prototype.C;
    Xw.prototype.unbind = Xw.prototype.L;
    Xw.prototype.unbindAll = Xw.prototype.M;
    Xw.prototype.changed = Xw.prototype.l;
    Xw.prototype.getRevision = Xw.prototype.u;
    Xw.prototype.on = Xw.prototype.s;
    Xw.prototype.once = Xw.prototype.v;
    Xw.prototype.un = Xw.prototype.t;
    Xw.prototype.unByKey = Xw.prototype.A;
    xl.prototype.getActive = xl.prototype.a;
    xl.prototype.setActive = xl.prototype.b;
    xl.prototype.bindTo = xl.prototype.K;
    xl.prototype.get = xl.prototype.get;
    xl.prototype.getKeys = xl.prototype.G;
    xl.prototype.getProperties = xl.prototype.I;
    xl.prototype.set = xl.prototype.set;
    xl.prototype.setProperties = xl.prototype.C;
    xl.prototype.unbind = xl.prototype.L;
    xl.prototype.unbindAll = xl.prototype.M;
    xl.prototype.changed = xl.prototype.l;
    xl.prototype.getRevision = xl.prototype.u;
    xl.prototype.on = xl.prototype.s;
    xl.prototype.once = xl.prototype.v;
    xl.prototype.un = xl.prototype.t;
    xl.prototype.unByKey = xl.prototype.A;
    zl.prototype.getActive = zl.prototype.a;
    zl.prototype.setActive = zl.prototype.b;
    zl.prototype.bindTo = zl.prototype.K;
    zl.prototype.get = zl.prototype.get;
    zl.prototype.getKeys = zl.prototype.G;
    zl.prototype.getProperties = zl.prototype.I;
    zl.prototype.set = zl.prototype.set;
    zl.prototype.setProperties = zl.prototype.C;
    zl.prototype.unbind = zl.prototype.L;
    zl.prototype.unbindAll = zl.prototype.M;
    zl.prototype.changed = zl.prototype.l;
    zl.prototype.getRevision = zl.prototype.u;
    zl.prototype.on = zl.prototype.s;
    zl.prototype.once = zl.prototype.v;
    zl.prototype.un = zl.prototype.t;
    zl.prototype.unByKey = zl.prototype.A;
    jx.prototype.getActive = jx.prototype.a;
    jx.prototype.setActive = jx.prototype.b;
    jx.prototype.bindTo = jx.prototype.K;
    jx.prototype.get = jx.prototype.get;
    jx.prototype.getKeys = jx.prototype.G;
    jx.prototype.getProperties = jx.prototype.I;
    jx.prototype.set = jx.prototype.set;
    jx.prototype.setProperties = jx.prototype.C;
    jx.prototype.unbind = jx.prototype.L;
    jx.prototype.unbindAll = jx.prototype.M;
    jx.prototype.changed = jx.prototype.l;
    jx.prototype.getRevision = jx.prototype.u;
    jx.prototype.on = jx.prototype.s;
    jx.prototype.once = jx.prototype.v;
    jx.prototype.un = jx.prototype.t;
    jx.prototype.unByKey = jx.prototype.A;
    Bl.prototype.getActive = Bl.prototype.a;
    Bl.prototype.setActive = Bl.prototype.b;
    Bl.prototype.bindTo = Bl.prototype.K;
    Bl.prototype.get = Bl.prototype.get;
    Bl.prototype.getKeys = Bl.prototype.G;
    Bl.prototype.getProperties = Bl.prototype.I;
    Bl.prototype.set = Bl.prototype.set;
    Bl.prototype.setProperties = Bl.prototype.C;
    Bl.prototype.unbind = Bl.prototype.L;
    Bl.prototype.unbindAll = Bl.prototype.M;
    Bl.prototype.changed = Bl.prototype.l;
    Bl.prototype.getRevision = Bl.prototype.u;
    Bl.prototype.on = Bl.prototype.s;
    Bl.prototype.once = Bl.prototype.v;
    Bl.prototype.un = Bl.prototype.t;
    Bl.prototype.unByKey = Bl.prototype.A;
    Dl.prototype.getActive = Dl.prototype.a;
    Dl.prototype.setActive = Dl.prototype.b;
    Dl.prototype.bindTo = Dl.prototype.K;
    Dl.prototype.get = Dl.prototype.get;
    Dl.prototype.getKeys = Dl.prototype.G;
    Dl.prototype.getProperties = Dl.prototype.I;
    Dl.prototype.set = Dl.prototype.set;
    Dl.prototype.setProperties = Dl.prototype.C;
    Dl.prototype.unbind = Dl.prototype.L;
    Dl.prototype.unbindAll = Dl.prototype.M;
    Dl.prototype.changed = Dl.prototype.l;
    Dl.prototype.getRevision = Dl.prototype.u;
    Dl.prototype.on = Dl.prototype.s;
    Dl.prototype.once = Dl.prototype.v;
    Dl.prototype.un = Dl.prototype.t;
    Dl.prototype.unByKey = Dl.prototype.A;
    Hl.prototype.getActive = Hl.prototype.a;
    Hl.prototype.setActive = Hl.prototype.b;
    Hl.prototype.bindTo = Hl.prototype.K;
    Hl.prototype.get = Hl.prototype.get;
    Hl.prototype.getKeys = Hl.prototype.G;
    Hl.prototype.getProperties = Hl.prototype.I;
    Hl.prototype.set = Hl.prototype.set;
    Hl.prototype.setProperties = Hl.prototype.C;
    Hl.prototype.unbind = Hl.prototype.L;
    Hl.prototype.unbindAll = Hl.prototype.M;
    Hl.prototype.changed = Hl.prototype.l;
    Hl.prototype.getRevision = Hl.prototype.u;
    Hl.prototype.on = Hl.prototype.s;
    Hl.prototype.once = Hl.prototype.v;
    Hl.prototype.un = Hl.prototype.t;
    Hl.prototype.unByKey = Hl.prototype.A;
    tx.prototype.getActive = tx.prototype.a;
    tx.prototype.setActive = tx.prototype.b;
    tx.prototype.bindTo = tx.prototype.K;
    tx.prototype.get = tx.prototype.get;
    tx.prototype.getKeys = tx.prototype.G;
    tx.prototype.getProperties = tx.prototype.I;
    tx.prototype.set = tx.prototype.set;
    tx.prototype.setProperties = tx.prototype.C;
    tx.prototype.unbind = tx.prototype.L;
    tx.prototype.unbindAll = tx.prototype.M;
    tx.prototype.changed = tx.prototype.l;
    tx.prototype.getRevision = tx.prototype.u;
    tx.prototype.on = tx.prototype.s;
    tx.prototype.once = tx.prototype.v;
    tx.prototype.un = tx.prototype.t;
    tx.prototype.unByKey = tx.prototype.A;
    lk.prototype.changed = lk.prototype.l;
    lk.prototype.getRevision = lk.prototype.u;
    lk.prototype.on = lk.prototype.s;
    lk.prototype.once = lk.prototype.v;
    lk.prototype.un = lk.prototype.t;
    lk.prototype.unByKey = lk.prototype.A;
    nk.prototype.clone = nk.prototype.clone;
    nk.prototype.getClosestPoint = nk.prototype.f;
    nk.prototype.getExtent = nk.prototype.J;
    nk.prototype.getType = nk.prototype.O;
    nk.prototype.intersectsExtent = nk.prototype.ja;
    nk.prototype.transform = nk.prototype.transform;
    nk.prototype.changed = nk.prototype.l;
    nk.prototype.getRevision = nk.prototype.u;
    nk.prototype.on = nk.prototype.s;
    nk.prototype.once = nk.prototype.v;
    nk.prototype.un = nk.prototype.t;
    nk.prototype.unByKey = nk.prototype.A;
    Fm.prototype.getFirstCoordinate = Fm.prototype.yb;
    Fm.prototype.getLastCoordinate = Fm.prototype.zb;
    Fm.prototype.getLayout = Fm.prototype.Ab;
    Fm.prototype.applyTransform = Fm.prototype.qa;
    Fm.prototype.translate = Fm.prototype.Ia;
    Fm.prototype.getClosestPoint = Fm.prototype.f;
    Fm.prototype.getExtent = Fm.prototype.J;
    Fm.prototype.intersectsExtent = Fm.prototype.ja;
    Fm.prototype.changed = Fm.prototype.l;
    Fm.prototype.getRevision = Fm.prototype.u;
    Fm.prototype.on = Fm.prototype.s;
    Fm.prototype.once = Fm.prototype.v;
    Fm.prototype.un = Fm.prototype.t;
    Fm.prototype.unByKey = Fm.prototype.A;
    Hm.prototype.getClosestPoint = Hm.prototype.f;
    Hm.prototype.getExtent = Hm.prototype.J;
    Hm.prototype.transform = Hm.prototype.transform;
    Hm.prototype.changed = Hm.prototype.l;
    Hm.prototype.getRevision = Hm.prototype.u;
    Hm.prototype.on = Hm.prototype.s;
    Hm.prototype.once = Hm.prototype.v;
    Hm.prototype.un = Hm.prototype.t;
    Hm.prototype.unByKey = Hm.prototype.A;
    Hk.prototype.getFirstCoordinate = Hk.prototype.yb;
    Hk.prototype.getLastCoordinate = Hk.prototype.zb;
    Hk.prototype.getLayout = Hk.prototype.Ab;
    Hk.prototype.applyTransform = Hk.prototype.qa;
    Hk.prototype.translate = Hk.prototype.Ia;
    Hk.prototype.getClosestPoint = Hk.prototype.f;
    Hk.prototype.getExtent = Hk.prototype.J;
    Hk.prototype.intersectsExtent = Hk.prototype.ja;
    Hk.prototype.transform = Hk.prototype.transform;
    Hk.prototype.changed = Hk.prototype.l;
    Hk.prototype.getRevision = Hk.prototype.u;
    Hk.prototype.on = Hk.prototype.s;
    Hk.prototype.once = Hk.prototype.v;
    Hk.prototype.un = Hk.prototype.t;
    Hk.prototype.unByKey = Hk.prototype.A;
    Om.prototype.getFirstCoordinate = Om.prototype.yb;
    Om.prototype.getLastCoordinate = Om.prototype.zb;
    Om.prototype.getLayout = Om.prototype.Ab;
    Om.prototype.applyTransform = Om.prototype.qa;
    Om.prototype.translate = Om.prototype.Ia;
    Om.prototype.getClosestPoint = Om.prototype.f;
    Om.prototype.getExtent = Om.prototype.J;
    Om.prototype.transform = Om.prototype.transform;
    Om.prototype.changed = Om.prototype.l;
    Om.prototype.getRevision = Om.prototype.u;
    Om.prototype.on = Om.prototype.s;
    Om.prototype.once = Om.prototype.v;
    Om.prototype.un = Om.prototype.t;
    Om.prototype.unByKey = Om.prototype.A;
    Qm.prototype.getFirstCoordinate = Qm.prototype.yb;
    Qm.prototype.getLastCoordinate = Qm.prototype.zb;
    Qm.prototype.getLayout = Qm.prototype.Ab;
    Qm.prototype.applyTransform = Qm.prototype.qa;
    Qm.prototype.translate = Qm.prototype.Ia;
    Qm.prototype.getClosestPoint = Qm.prototype.f;
    Qm.prototype.getExtent = Qm.prototype.J;
    Qm.prototype.transform = Qm.prototype.transform;
    Qm.prototype.changed = Qm.prototype.l;
    Qm.prototype.getRevision = Qm.prototype.u;
    Qm.prototype.on = Qm.prototype.s;
    Qm.prototype.once = Qm.prototype.v;
    Qm.prototype.un = Qm.prototype.t;
    Qm.prototype.unByKey = Qm.prototype.A;
    Tm.prototype.getFirstCoordinate = Tm.prototype.yb;
    Tm.prototype.getLastCoordinate = Tm.prototype.zb;
    Tm.prototype.getLayout = Tm.prototype.Ab;
    Tm.prototype.applyTransform = Tm.prototype.qa;
    Tm.prototype.translate = Tm.prototype.Ia;
    Tm.prototype.getClosestPoint = Tm.prototype.f;
    Tm.prototype.getExtent = Tm.prototype.J;
    Tm.prototype.transform = Tm.prototype.transform;
    Tm.prototype.changed = Tm.prototype.l;
    Tm.prototype.getRevision = Tm.prototype.u;
    Tm.prototype.on = Tm.prototype.s;
    Tm.prototype.once = Tm.prototype.v;
    Tm.prototype.un = Tm.prototype.t;
    Tm.prototype.unByKey = Tm.prototype.A;
    Um.prototype.getFirstCoordinate = Um.prototype.yb;
    Um.prototype.getLastCoordinate = Um.prototype.zb;
    Um.prototype.getLayout = Um.prototype.Ab;
    Um.prototype.applyTransform = Um.prototype.qa;
    Um.prototype.translate = Um.prototype.Ia;
    Um.prototype.getClosestPoint = Um.prototype.f;
    Um.prototype.getExtent = Um.prototype.J;
    Um.prototype.transform = Um.prototype.transform;
    Um.prototype.changed = Um.prototype.l;
    Um.prototype.getRevision = Um.prototype.u;
    Um.prototype.on = Um.prototype.s;
    Um.prototype.once = Um.prototype.v;
    Um.prototype.un = Um.prototype.t;
    Um.prototype.unByKey = Um.prototype.A;
    Jk.prototype.getFirstCoordinate = Jk.prototype.yb;
    Jk.prototype.getLastCoordinate = Jk.prototype.zb;
    Jk.prototype.getLayout = Jk.prototype.Ab;
    Jk.prototype.applyTransform = Jk.prototype.qa;
    Jk.prototype.translate = Jk.prototype.Ia;
    Jk.prototype.getClosestPoint = Jk.prototype.f;
    Jk.prototype.getExtent = Jk.prototype.J;
    Jk.prototype.transform = Jk.prototype.transform;
    Jk.prototype.changed = Jk.prototype.l;
    Jk.prototype.getRevision = Jk.prototype.u;
    Jk.prototype.on = Jk.prototype.s;
    Jk.prototype.once = Jk.prototype.v;
    Jk.prototype.un = Jk.prototype.t;
    Jk.prototype.unByKey = Jk.prototype.A;
    F.prototype.getFirstCoordinate = F.prototype.yb;
    F.prototype.getLastCoordinate = F.prototype.zb;
    F.prototype.getLayout = F.prototype.Ab;
    F.prototype.applyTransform = F.prototype.qa;
    F.prototype.translate = F.prototype.Ia;
    F.prototype.getClosestPoint = F.prototype.f;
    F.prototype.getExtent = F.prototype.J;
    F.prototype.transform = F.prototype.transform;
    F.prototype.changed = F.prototype.l;
    F.prototype.getRevision = F.prototype.u;
    F.prototype.on = F.prototype.s;
    F.prototype.once = F.prototype.v;
    F.prototype.un = F.prototype.t;
    F.prototype.unByKey = F.prototype.A;
    Qq.prototype.readFeatures = Qq.prototype.ma;
    Hq.prototype.readFeatures = Hq.prototype.ma;
    Hq.prototype.readFeatures = Hq.prototype.ma;
    op.prototype.bindTo = op.prototype.K;
    op.prototype.get = op.prototype.get;
    op.prototype.getKeys = op.prototype.G;
    op.prototype.getProperties = op.prototype.I;
    op.prototype.set = op.prototype.set;
    op.prototype.setProperties = op.prototype.C;
    op.prototype.unbind = op.prototype.L;
    op.prototype.unbindAll = op.prototype.M;
    op.prototype.changed = op.prototype.l;
    op.prototype.getRevision = op.prototype.u;
    op.prototype.on = op.prototype.s;
    op.prototype.once = op.prototype.v;
    op.prototype.un = op.prototype.t;
    op.prototype.unByKey = op.prototype.A;
    Ug.prototype.bindTo = Ug.prototype.K;
    Ug.prototype.get = Ug.prototype.get;
    Ug.prototype.getKeys = Ug.prototype.G;
    Ug.prototype.getProperties = Ug.prototype.I;
    Ug.prototype.set = Ug.prototype.set;
    Ug.prototype.setProperties = Ug.prototype.C;
    Ug.prototype.unbind = Ug.prototype.L;
    Ug.prototype.unbindAll = Ug.prototype.M;
    Ug.prototype.changed = Ug.prototype.l;
    Ug.prototype.getRevision = Ug.prototype.u;
    Ug.prototype.on = Ug.prototype.s;
    Ug.prototype.once = Ug.prototype.v;
    Ug.prototype.un = Ug.prototype.t;
    Ug.prototype.unByKey = Ug.prototype.A;
    Vg.prototype.getMap = Vg.prototype.f;
    Vg.prototype.setMap = Vg.prototype.setMap;
    Vg.prototype.setTarget = Vg.prototype.b;
    Vg.prototype.bindTo = Vg.prototype.K;
    Vg.prototype.get = Vg.prototype.get;
    Vg.prototype.getKeys = Vg.prototype.G;
    Vg.prototype.getProperties = Vg.prototype.I;
    Vg.prototype.set = Vg.prototype.set;
    Vg.prototype.setProperties = Vg.prototype.C;
    Vg.prototype.unbind = Vg.prototype.L;
    Vg.prototype.unbindAll = Vg.prototype.M;
    Vg.prototype.changed = Vg.prototype.l;
    Vg.prototype.getRevision = Vg.prototype.u;
    Vg.prototype.on = Vg.prototype.s;
    Vg.prototype.once = Vg.prototype.v;
    Vg.prototype.un = Vg.prototype.t;
    Vg.prototype.unByKey = Vg.prototype.A;
    fh.prototype.getMap = fh.prototype.f;
    fh.prototype.setMap = fh.prototype.setMap;
    fh.prototype.setTarget = fh.prototype.b;
    fh.prototype.bindTo = fh.prototype.K;
    fh.prototype.get = fh.prototype.get;
    fh.prototype.getKeys = fh.prototype.G;
    fh.prototype.getProperties = fh.prototype.I;
    fh.prototype.set = fh.prototype.set;
    fh.prototype.setProperties = fh.prototype.C;
    fh.prototype.unbind = fh.prototype.L;
    fh.prototype.unbindAll = fh.prototype.M;
    fh.prototype.changed = fh.prototype.l;
    fh.prototype.getRevision = fh.prototype.u;
    fh.prototype.on = fh.prototype.s;
    fh.prototype.once = fh.prototype.v;
    fh.prototype.un = fh.prototype.t;
    fh.prototype.unByKey = fh.prototype.A;
    gh.prototype.getMap = gh.prototype.f;
    gh.prototype.setTarget = gh.prototype.b;
    gh.prototype.bindTo = gh.prototype.K;
    gh.prototype.get = gh.prototype.get;
    gh.prototype.getKeys = gh.prototype.G;
    gh.prototype.getProperties = gh.prototype.I;
    gh.prototype.set = gh.prototype.set;
    gh.prototype.setProperties = gh.prototype.C;
    gh.prototype.unbind = gh.prototype.L;
    gh.prototype.unbindAll = gh.prototype.M;
    gh.prototype.changed = gh.prototype.l;
    gh.prototype.getRevision = gh.prototype.u;
    gh.prototype.on = gh.prototype.s;
    gh.prototype.once = gh.prototype.v;
    gh.prototype.un = gh.prototype.t;
    gh.prototype.unByKey = gh.prototype.A;
    No.prototype.getMap = No.prototype.f;
    No.prototype.setTarget = No.prototype.b;
    No.prototype.bindTo = No.prototype.K;
    No.prototype.get = No.prototype.get;
    No.prototype.getKeys = No.prototype.G;
    No.prototype.getProperties = No.prototype.I;
    No.prototype.set = No.prototype.set;
    No.prototype.setProperties = No.prototype.C;
    No.prototype.unbind = No.prototype.L;
    No.prototype.unbindAll = No.prototype.M;
    No.prototype.changed = No.prototype.l;
    No.prototype.getRevision = No.prototype.u;
    No.prototype.on = No.prototype.s;
    No.prototype.once = No.prototype.v;
    No.prototype.un = No.prototype.t;
    No.prototype.unByKey = No.prototype.A;
    Yg.prototype.getMap = Yg.prototype.f;
    Yg.prototype.setMap = Yg.prototype.setMap;
    Yg.prototype.setTarget = Yg.prototype.b;
    Yg.prototype.bindTo = Yg.prototype.K;
    Yg.prototype.get = Yg.prototype.get;
    Yg.prototype.getKeys = Yg.prototype.G;
    Yg.prototype.getProperties = Yg.prototype.I;
    Yg.prototype.set = Yg.prototype.set;
    Yg.prototype.setProperties = Yg.prototype.C;
    Yg.prototype.unbind = Yg.prototype.L;
    Yg.prototype.unbindAll = Yg.prototype.M;
    Yg.prototype.changed = Yg.prototype.l;
    Yg.prototype.getRevision = Yg.prototype.u;
    Yg.prototype.on = Yg.prototype.s;
    Yg.prototype.once = Yg.prototype.v;
    Yg.prototype.un = Yg.prototype.t;
    Yg.prototype.unByKey = Yg.prototype.A;
    To.prototype.getMap = To.prototype.f;
    To.prototype.setMap = To.prototype.setMap;
    To.prototype.setTarget = To.prototype.b;
    To.prototype.bindTo = To.prototype.K;
    To.prototype.get = To.prototype.get;
    To.prototype.getKeys = To.prototype.G;
    To.prototype.getProperties = To.prototype.I;
    To.prototype.set = To.prototype.set;
    To.prototype.setProperties = To.prototype.C;
    To.prototype.unbind = To.prototype.L;
    To.prototype.unbindAll = To.prototype.M;
    To.prototype.changed = To.prototype.l;
    To.prototype.getRevision = To.prototype.u;
    To.prototype.on = To.prototype.s;
    To.prototype.once = To.prototype.v;
    To.prototype.un = To.prototype.t;
    To.prototype.unByKey = To.prototype.A;
    $g.prototype.getMap = $g.prototype.f;
    $g.prototype.setMap = $g.prototype.setMap;
    $g.prototype.setTarget = $g.prototype.b;
    $g.prototype.bindTo = $g.prototype.K;
    $g.prototype.get = $g.prototype.get;
    $g.prototype.getKeys = $g.prototype.G;
    $g.prototype.getProperties = $g.prototype.I;
    $g.prototype.set = $g.prototype.set;
    $g.prototype.setProperties = $g.prototype.C;
    $g.prototype.unbind = $g.prototype.L;
    $g.prototype.unbindAll = $g.prototype.M;
    $g.prototype.changed = $g.prototype.l;
    $g.prototype.getRevision = $g.prototype.u;
    $g.prototype.on = $g.prototype.s;
    $g.prototype.once = $g.prototype.v;
    $g.prototype.un = $g.prototype.t;
    $g.prototype.unByKey = $g.prototype.A;
    hp.prototype.getMap = hp.prototype.f;
    hp.prototype.setTarget = hp.prototype.b;
    hp.prototype.bindTo = hp.prototype.K;
    hp.prototype.get = hp.prototype.get;
    hp.prototype.getKeys = hp.prototype.G;
    hp.prototype.getProperties = hp.prototype.I;
    hp.prototype.set = hp.prototype.set;
    hp.prototype.setProperties = hp.prototype.C;
    hp.prototype.unbind = hp.prototype.L;
    hp.prototype.unbindAll = hp.prototype.M;
    hp.prototype.changed = hp.prototype.l;
    hp.prototype.getRevision = hp.prototype.u;
    hp.prototype.on = hp.prototype.s;
    hp.prototype.once = hp.prototype.v;
    hp.prototype.un = hp.prototype.t;
    hp.prototype.unByKey = hp.prototype.A;
    mp.prototype.getMap = mp.prototype.f;
    mp.prototype.setMap = mp.prototype.setMap;
    mp.prototype.setTarget = mp.prototype.b;
    mp.prototype.bindTo = mp.prototype.K;
    mp.prototype.get = mp.prototype.get;
    mp.prototype.getKeys = mp.prototype.G;
    mp.prototype.getProperties = mp.prototype.I;
    mp.prototype.set = mp.prototype.set;
    mp.prototype.setProperties = mp.prototype.C;
    mp.prototype.unbind = mp.prototype.L;
    mp.prototype.unbindAll = mp.prototype.M;
    mp.prototype.changed = mp.prototype.l;
    mp.prototype.getRevision = mp.prototype.u;
    mp.prototype.on = mp.prototype.s;
    mp.prototype.once = mp.prototype.v;
    mp.prototype.un = mp.prototype.t;
    mp.prototype.unByKey = mp.prototype.A;
    return OPENLAYERS.ol;
}));

