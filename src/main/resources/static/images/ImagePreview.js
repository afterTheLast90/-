/*
 * 预览图片 Object
 * 2015-05-23 17:25
 *
 */
function ImagePreview() {

    var IMGP_BG = null;
    var IMGP_P_BG = null;
    var IMGSHOW_BG = null;
    var IMGSHOW_BOX = null;
    var IMGSHOW_NEXT = null;
    var IMGSHOW_LAST = null;
    var IMGSHOW_NOW = null;
    var isFullscreen = false;
    _start();
    IMGP_BG.onmousewheel = function(e) {
            //存在兼容性
            //console.log(e.wheelDelta+" "+e.detail);
            return false;
        }
        //显示图片
    this.show = function(imgs) {
        IMGP_BG.style.display = "block";
        IMGSHOW_NOW.src = imgs;
    }
        //关闭预览
    IMGSHOW_BG.ondblclick = function() {
        isFullscreen = false;
        document.body.style.overflow = "auto";
        IMGP_BG.style.display = "none";
    }

    function _start() {
        //创建预览照片盒子
        IMGP_BG = document.createElement("div");
        IMGP_BG.id = "IMGP_BG";
        document.body.appendChild(IMGP_BG);
        //大图预览背景
        IMGSHOW_BG = document.createElement("div");
        IMGSHOW_BG.id = "IMGSHOW_BG";
        IMGP_BG.appendChild(IMGSHOW_BG);
        //大图预览盒子
        IMGSHOW_BOX = document.createElement("div");
        IMGSHOW_BOX.id = "IMGSHOW_BOX";
        IMGSHOW_BG.appendChild(IMGSHOW_BOX);;
        //正在预览
        IMGSHOW_NOW = document.createElement("img");
        IMGSHOW_NOW.id = "IMGSHOW_NOW";
        IMGSHOW_BOX.appendChild(IMGSHOW_NOW);
        //--------------------------------
        //创建待预览图片背景
        IMGP_P_BG = document.createElement("div");
        IMGP_P_BG.id = "IMGP_P_BG";
        IMGP_BG.appendChild(IMGP_P_BG);


        var bodyW = document.documentElement.clientHeight;
        IMGSHOW_BG.style.height = (bodyW) / bodyW * 100 + "%";
        IMGSHOW_BOX.style.lineHeight = (bodyW - 5) + "px";

    }
    window.onresize = function() {
        var bodyW = document.documentElement.clientHeight;
        IMGSHOW_BG.style.height = (bodyW) / bodyW * 100 + "%";
        IMGSHOW_BOX.style.lineHeight = (bodyW - 5) + "px";
        IMGSHOW_NEXT.style.top = ((bodyW) - 400) / 2 + "px";
        IMGSHOW_LAST.style.top = ((bodyW) - 400) / 2 + "px";
    }

    function _setCss(obj, css) {
        for (var v in css) {
            obj.style[v] = css[v];
        }
    }


}