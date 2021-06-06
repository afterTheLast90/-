function convertFileSize(size) {
    let kb = 1024;
    let mb = kb * 1024;
    let gb = mb * 1024;
    let tb = gb*1025;

    if (size >= tb ){
        return String((size / tb).toFixed(2))+" TB";
    } else if (size >= gb) {
        return String((size / gb).toFixed(2))+" GB";
    } else if (size >= mb) {
        let f =  size / mb;
        if (f>100)
            return String(f.toFixed(0))+" MB";
        else
            return String(f.toFixed(2))+"MB"
    } else if (size >= kb) {
        let f = size / kb;
        if (f>100)
            return String(f.toFixed(0))+" KB";
        else
            return String(f.toFixed(2))+"KB"
    } else {
        return String(size)+" B";
    }
}