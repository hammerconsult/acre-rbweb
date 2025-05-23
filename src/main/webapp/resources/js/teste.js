(function (a) {
    a.idleTimer = function (h) {
        var c = false, e = false, g = 30000, j = "mousemove keydown DOMMouseScroll mousewheel click", d = function () {
            c = !c;
            a(document).trigger((c ? "idle" : "active") + ".idleTimer")
        }, k = function () {
            return e
        }, i = function () {
            return c
        }, f = function () {
            e = false;
            clearTimeout(a.idleTimer.tId);
            a(document).unbind(".idleTimer")
        }, b = function () {
            clearTimeout(a.idleTimer.tId);
            if (e) {
                if (c) {
                    d()
                } else {
                    a.idleTimer.tId = setTimeout(d, g)
                }
            }
        };
        e = true;
        c = false;
        if (typeof h == "number") {
            g = h
        } else {
            if (h === "destroy") {
                f();
                return
            }
        }
        a(document).bind(a.trim((j + " ").split(" ").join(".idleTimer ")), b);
        a.idleTimer.tId = setTimeout(d, g)
    }
})(jQuery);
PrimeFaces.widget.IdleMonitor = PrimeFaces.widget.BaseWidget.extend({
    init: function (b) {
        this._super(b);
        var a = this;
        $(document).bind("idle.idleTimer", function () {
            if (a.cfg.onidle) {
                a.cfg.onidle.call(a)
            }
            if (a.cfg.behaviors) {
                var c = a.cfg.behaviors.idle;
                if (c) {
                    c.call(a)
                }
            }
        });
        $(document).bind("active.idleTimer", function () {
            if (a.cfg.onactive) {
                a.cfg.onactive.call(this)
            }
            if (a.cfg.behaviors) {
                var c = a.cfg.behaviors.active;
                if (c) {
                    c.call(a)
                }
            }
        });
        $.idleTimer(this.cfg.timeout)
    }
});
/**
 * Created by Rodolfo on 23/03/2017.
 */
