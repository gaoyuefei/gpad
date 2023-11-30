package com.gpad.common.core.utils.uuid;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.gpad.common.core.enums.TraceIdType;
import com.gpad.common.core.utils.StringUtils;
import org.slf4j.MDC;

public class TraceIdUtil {

    private static final String TRACE_ID = "traceId";

    public TraceIdUtil() {
    }

    public static String generate(TraceIdType type) {
        switch(type) {
        case MQ:
            return generateMqTraceId();
        case JOB:
            return generateJobTraceId();
        case OUT:
            return generateOutTraceId();
        default:
            return IdWorker.get32UUID();
        }
    }

    public static String generateMqTraceId()
    {
        return TraceIdType.MQ.getPrefix() + IdWorker.get32UUID();
    }

    public static String generateHeaderTraceId()
    {
        return MDC.get(TRACE_ID);

    }

    public static Boolean fromMq(String traceId)
    {
        return StringUtils.isNotEmpty(traceId) && traceId.startsWith(TraceIdType.MQ.getPrefix());
    }

    public static String generateJobTraceId()
    {
        return TraceIdType.JOB.getPrefix() + IdWorker.get32UUID();
    }

    public static Boolean fromJob(String traceId)
    {
        return StringUtils.isNotEmpty(traceId) && traceId.startsWith(TraceIdType.JOB.getPrefix());
    }

    public static String generateOutTraceId()
    {
        return TraceIdType.OUT.getPrefix() + IdWorker.get32UUID();
    }

    public static Boolean fromOut(String traceId)
    {
        return StringUtils.isNotEmpty(traceId) && traceId.startsWith(TraceIdType.OUT.getPrefix()) || traceId.length() == 32 || !fromJob(traceId) && !fromMq(traceId);
    }

    public static TraceIdType getTraceIdType(String traceId)
    {
        if (fromMq(traceId)) {
            return TraceIdType.MQ;
        } else {
            return fromJob(traceId) ? TraceIdType.JOB : TraceIdType.OUT;
        }
    }

}