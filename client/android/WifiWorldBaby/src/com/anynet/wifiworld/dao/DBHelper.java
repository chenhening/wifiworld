/*
 * 文件名称 : DBHelper.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-9-17, 下午3:21:42
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.dao;

import java.util.List;
import android.content.Context;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.Msg;
import com.anynet.wifiworld.dao.SysMessageDao.Properties;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class DBHelper
{
    private static Context mContext;
    
    private static DBHelper instance;
    
    private SysMessageDao sysMessageDao;
    
    private DBHelper()
    {
    }
    
    public static DBHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DBHelper();
            if (mContext == null)
            {
                mContext = context;
            }
            
            // 数据库对象
            DaoSession daoSession = WifiWorldApplication.getDaoSession(mContext);
            instance.sysMessageDao = daoSession.getSysMessageDao();
        }
        return instance;
    }
    
    /**
     * 增加信息
     * 
     * @param serverId
     * @param content
     * @param type
     * @param date
     * @param state
     *            状态
     */
    
    public void addMsg(long serverId, String content, String title, int type, long date)
    {
        
        //
        SysMessage sysMessage = new SysMessage(null, serverId, content, title, type, date, 1);
        sysMessageDao.insert(sysMessage);
        
    }
    
    public void addSystemMsg(Msg[] msgs)
    {
        
        if (msgs == null)
        {
            return;
        }
        
        for (Msg msg : msgs)
        {
            
            SysMessage sysMessage = new SysMessage(null, msg.getId(), msg.getContent(), msg.getTitle(), 1, msg.getDate(), 1);
            sysMessageDao.insert(sysMessage);
        }
        
    }
    
    public long getMaxServId()
    {
        
        long servID = -1;
        
        List<SysMessage> SystemMsgList = sysMessageDao.queryBuilder().orderDesc(SysMessageDao.Properties.ServerId).limit(1).list();
        if (SystemMsgList.size() > 0)
        {
            servID = SystemMsgList.get(0).getServerId();
        }
        
        return servID;
    }
    
    /** 查询 */
    public List<SysMessage> getSysMessageList()
    {
        
        //查询未被删除的
        QueryBuilder<SysMessage> qb = sysMessageDao.queryBuilder().orderDesc(SysMessageDao.Properties.Date).where(Properties.State.notEq(-1));
        
        return qb.list();
    }
    
    /** 删除服务端 */
    public void deleteMessage(Long id)
    {
        QueryBuilder<SysMessage> qb = sysMessageDao.queryBuilder();
        DeleteQuery<SysMessage> bd = qb.where(SysMessageDao.Properties.Id.eq(id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    /** 删除服务端 */
    public void changeToDelMessage(Long id)
    {
        QueryBuilder<SysMessage> qb = sysMessageDao.queryBuilder().where(Properties.Id.eq(id));
        
        for (SysMessage sysMsg : qb.list())
        {
            //设置状态位被删除
            sysMsg.setState(-1);
            sysMessageDao.update(sysMsg);
        }
    }
    
    /** 更新状态 */
    public void updateMessage()
    {
        QueryBuilder<SysMessage> qb = sysMessageDao.queryBuilder().where(Properties.State.eq("1"));
        
        for (SysMessage sysMsg : qb.list())
        {
            sysMsg.setState(0);
            sysMessageDao.update(sysMsg);
        }
    }
    
}
