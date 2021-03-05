package com.itan.util;

public class PageSupport {
    //当前页码，来自于用户输入，即选择的页数
    private int currentPageNo = 1;

    //总记录数量，查询数据库表总数得到
    private int totalCount = 0;

    //页面容量大小
    private int pageSize = 0;

    //总页数，通过运算得到
    private int totalPageCount = 1;

    //OOP：封装，多态，继承
    //封装不止有私有属性，get、set方法 还有在set中进行一些不安全情况的判断。也可以在业务层做此功能但是代码繁琐，所以在此做。
    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        if (currentPageNo>0){
            this.currentPageNo = currentPageNo;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        if (totalCount>0){
            this.totalCount = totalCount;
            //设置总页数
            this.setTotalPageCountByRs();
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize>0){
            this.pageSize = pageSize;
        }
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        if (totalPageCount>0){
            this.totalPageCount = totalPageCount;
        }
    }

    public void setTotalPageCountByRs(){
        //如果不进行判断，会抛出500异常java.lang.arithmeticexception: / by zero
        //但是此处不需要判断，因为前面设置的时候限定pagesize>0，不过之前是因为在设置pagesize
        //之前就执行了这个方法，所以导致pagsize=0，所以可以进行判断，设置提示信息
        if (this.pageSize!=0){
            if (this.totalCount%this.pageSize==0){
                this.totalPageCount=this.totalCount/this.pageSize;
            }else if (this.totalCount%this.pageSize>0){
                this.totalPageCount=this.totalCount/this.pageSize+1;
            }else{
                this.totalPageCount=0;
            }
        }else{
            System.out.println("请先设置pagesize，否则此处除数为0，会报异常");
        }
    }
}
