package util;

/**
 * Created by Esk on 2017-05-21.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

// 시간 측정용 클래스
public class TimeCheck
{
    private long start;
    private long end;
    private String name;
    private boolean isStart;
    public TimeCheck(final String name)
    {
        start = 0L;
        end = 0L;
        this.name = name;
        isStart = false;
    }
    public void CheckStart()
    {
        start = System.currentTimeMillis();
        isStart = true;
    }
    public void CheckEnd()
    {
        if(isStart)
        {
            end = System.currentTimeMillis();
            isStart = false;
        }
    }
    public String CheckResult()
    {
        if(start != 0L && end != 0L)
        {
            return name + " 실행시간: " + (end - start) / 1000.0;
        }
        else
            return "";
    }

}
