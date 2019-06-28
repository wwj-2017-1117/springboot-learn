package com.rickiyang.learn.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rickiyang.learn.job.SpringSimpleJob;
import com.rickiyang.learn.job.SpringSimpleJob1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Configuration
public class SimpleJobConfig {
    /**
     * 注册中心配置
     */
    @Resource
    private ZookeeperRegistryCenter regCenter;

    /**
     * 将作业运行的痕迹进行持久化到DB的操作配置
     */
    @Autowired
    private JobEventConfiguration jobEventConfiguration;

    @Autowired
    private SpringSimpleJob springSimpleJob;

    @Autowired
    private SpringSimpleJob1 springSimpleJob1;

    @Value("${simpleJob.cron}")
    private String cron;
    @Value("${simpleJob.shardingTotalCount}")
    private int shardingTotalCount;
    @Value("${simpleJob.shardingItemParameters}")
    private String shardingItemParameters;

    @Value("${simpleJob.cron1}")
    private String cron1;
    @Value("${simpleJob.shardingTotalCount1}")
    private int shardingTotalCount1;
    @Value("${simpleJob.shardingItemParameters1}")
    private String shardingItemParameters1;

    @PostConstruct
    public void init() {
        new SpringJobScheduler(springSimpleJob, regCenter,
                getLiteJobConfiguration(springSimpleJob.getClass(),
                        cron,
                        shardingTotalCount,
                        shardingItemParameters), jobEventConfiguration).init();
        new SpringJobScheduler(springSimpleJob1, regCenter,
                getLiteJobConfiguration(springSimpleJob1.getClass(),
                        cron1,
                        shardingTotalCount1,
                        shardingItemParameters1), jobEventConfiguration).init();
    }

    /**
     * 作业配置
     * 作业配置分为3级，分别是JobCoreConfiguration，JobTypeConfiguration和LiteJobConfiguration。
     * <p>
     * LiteJobConfiguration使用JobTypeConfiguration;
     * JobTypeConfiguration使用JobCoreConfiguration，层层嵌套;
     * <p>
     * JobTypeConfiguration根据不同实现类型分为SimpleJobConfiguration，DataflowJobConfiguration和ScriptJobConfiguration。
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @return
     */
    public LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters).build(),
                jobClass.getCanonicalName())).overwrite(true).build();
    }
}
