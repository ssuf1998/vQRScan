package indi.ssuf1998.vqrscan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HistoryItem {
    @Id(autoincrement = true)
    private Long id;

    private String resultText;
    private int resultType;
    private long createTime;

    @Keep
    public HistoryItem(String resultText, int resultType, long createTime) {
        this.resultText = resultText;
        this.resultType = resultType;
        this.createTime = createTime;
    }

    @Keep
    public HistoryItem() {
        this("", Utils.ResultType.PLAIN_TEXT, 0);
    }


    @Generated(hash = 1354959903)
    public HistoryItem(Long id, String resultText, int resultType,
            long createTime) {
        this.id = id;
        this.resultText = resultText;
        this.resultType = resultType;
        this.createTime = createTime;
    }

    public String getResultText() {
        return resultText;
    }

    public HistoryItem setResultText(String resultText) {
        this.resultText = resultText;
        return this;
    }

    public int getResultType() {
        return resultType;
    }

    public HistoryItem setResultType(int resultType) {
        this.resultType = resultType;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public HistoryItem setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
