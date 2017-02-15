package net.theoverride.zedmemeapp;



public class MyItem {
   String meme_comment, url,comments, key;
    int commentNumber = 0 ;
    Object upvoteNumber;


    public MyItem() {
    }

    public String getMeme_comment() {
        return meme_comment;
    }

    public void setMeme_comment(String meme_comment) {
        this.meme_comment = meme_comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getUpvoteNumber() {
        return upvoteNumber;
    }

    public void setUpvoteNumber(Object upvoteNumber) {
        this.upvoteNumber = upvoteNumber;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
