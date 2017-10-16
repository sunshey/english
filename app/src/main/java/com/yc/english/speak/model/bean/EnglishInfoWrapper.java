package com.yc.english.speak.model.bean;

import java.util.List;

/**
 * Created by wanglin  on 2017/10/12 16:29.
 */

public class EnglishInfoWrapper {

    /**
     * code : 1
     * data : {"list":[{"title":"经典影视","type":1,"item_list":[{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":13562,"update_date":"2017年09月28日","sub_title":"功夫熊猫3 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":10871,"update_date":"2017年08月28日","sub_title":"冰川时代4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":12362,"update_date":"2017年09月20日","sub_title":"机器总动员 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":11762,"update_date":"2017年09月02日","sub_title":"怪物史瑞克4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":14456,"update_date":"2017年08月28日","sub_title":"赛车总动员 英文版"}]},{"title":"卡通动漫","type":2,"item_list":[{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":13562,"update_date":"2017年09月28日","sub_title":"昆塔：盒子总动员 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":10871,"update_date":"2017年08月28日","sub_title":"冰川时代4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":12362,"update_date":"2017年09月20日","sub_title":"机器总动员 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":11762,"update_date":"2017年09月02日","sub_title":"怪物史瑞克4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":14456,"update_date":"2017年08月28日","sub_title":"赛车总动员 英文版"}]},{"title":"经典影视","type":3,"item_list":[{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":13562,"update_date":"2017年09月28日","sub_title":"超人总动员 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":10871,"update_date":"2017年08月28日","sub_title":"冰川时代4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":12362,"update_date":"2017年09月20日","sub_title":"机器总动员 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":11762,"update_date":"2017年09月02日","sub_title":"怪物史瑞克4 英文版"},{"url":"http://en.wk2.com/ueditor/php/upload/image/20171012/1507769710256192.png","play_count":14456,"update_date":"2017年08月28日","sub_title":"赛车总动员 英文版"}]}]}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public class DataBean {
        private List<EnglishInfo> list;

        public List<EnglishInfo> getList() {
            return list;
        }

        public void setList(List<EnglishInfo> list) {
            this.list = list;
        }

    }
}
