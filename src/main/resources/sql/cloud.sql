-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- 主机： localhost
-- 生成日期： 2021-05-12 12:48:29
-- 服务器版本： 5.7.32-log
-- PHP 版本： 7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `cloud`
--

-- --------------------------------------------------------

--
-- 表的结构 `files`
--

CREATE TABLE `files` (
                         `file_id` bigint(20) NOT NULL COMMENT '文件ID',
                         `file_md5` varchar(32) NOT NULL COMMENT '文件md5',
                         `file_path` varchar(1024) NOT NULL COMMENT '文件路径',
                         `file_size` bigint(20) NOT NULL COMMENT '文件大小(字节为单位)',
                         `storage_location` int(11) NOT NULL DEFAULT '0' COMMENT '存储位置 0本地磁盘',
                         `citations_count` bigint(20) NOT NULL COMMENT '引用次数',
                         `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                         `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件 ';

-- --------------------------------------------------------

--
-- 表的结构 `file_history`
--

CREATE TABLE `file_history` (
                                `history_id` bigint(20) NOT NULL COMMENT '历史id',
                                `file_id` bigint(20) NOT NULL COMMENT '文件id',
                                `user_file_id` bigint(20) NOT NULL COMMENT '用户文件id',
                                `file_name` varchar(32) NOT NULL COMMENT '历史文件名',
                                `file_size` varchar(32) NOT NULL COMMENT '历史文件大小',
                                `update_person` bigint(20) NOT NULL COMMENT '修改人 0为匿名用户',
                                `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                                `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件历史 ';

-- --------------------------------------------------------

--
-- 表的结构 `file_inbox`
--

CREATE TABLE `file_inbox` (
                              `inbox_id` bigint(20) NOT NULL COMMENT '收件箱id',
                              `title` varchar(128) NOT NULL COMMENT '标题',
                              `publisher` bigint(20) NOT NULL COMMENT '发布人',
                              `content` varchar(1024) NOT NULL COMMENT '内容',
                              `input_tips` varchar(128) NOT NULL COMMENT '输入提示',
                              `commit_count` int(11) NOT NULL COMMENT '提交人数',
                              `save_path` varchar(1024) NOT NULL COMMENT '保存路径',
                              `end_time` datetime NOT NULL COMMENT '截至时间',
                              `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                              `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件收件箱 ';

-- --------------------------------------------------------

--
-- 表的结构 `groups`
--

CREATE TABLE `groups` (
                          `group_id` bigint(20) NOT NULL COMMENT '组id',
                          `group_name` varchar(128) NOT NULL COMMENT '组名',
                          `number_of_persones` int(11) NOT NULL COMMENT '人数',
                          `user_id` bigint(20) NOT NULL COMMENT '所有者',
                          `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                          `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组 ';

-- --------------------------------------------------------

--
-- 表的结构 `group_relationship`
--

CREATE TABLE `group_relationship` (
                                      `group_relationship_id` bigint(20) NOT NULL COMMENT '组关系id',
                                      `group_id` bigint(20) NOT NULL COMMENT '组id',
                                      `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                      `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                                      `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组关系 ';

-- --------------------------------------------------------

--
-- 表的结构 `inner_share`
--

CREATE TABLE `inner_share` (
                               `inner_share_id` bigint(20) NOT NULL COMMENT '内部分享id',
                               `share_id` bigint(20) NOT NULL COMMENT '分享id',
                               `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '用户Id',
                               `group_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '组id',
                               `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                               `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内部分享 ';


-- --------------------------------------------------------

--
-- 表的结构 `receiving_record`
--

CREATE TABLE `receiving_record` (
                                    `receiving_id` bigint(20) NOT NULL COMMENT '收件ID',
                                    `file_id` bigint(20) NOT NULL COMMENT '文件id',
                                    `user_file_id` bigint(20) NOT NULL COMMENT '用户文件id',
                                    `inbox_id` bigint(20) NOT NULL COMMENT '收集id',
                                    `over` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否被覆盖',
                                    `input_name` varchar(1024) NOT NULL COMMENT '输入名',
                                    `commit_file_name` varchar(1024) NOT NULL COMMENT '提交原文件名',
                                    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                                    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收件记录 ';

-- --------------------------------------------------------

--
-- 表的结构 `recycle`
--

CREATE TABLE `recycle` (
                           `recycle_id` bigint(20) NOT NULL COMMENT '回收站id',
                           `file_name` varchar(128) NOT NULL COMMENT '文件名',
                           `file_type` varchar(128) NOT NULL COMMENT '文件类型',
                           `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                           `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收站 ';

-- --------------------------------------------------------

--
-- 表的结构 `system_settings`
--

CREATE TABLE `system_settings` (
                                   `setting_key` varchar(128) NOT NULL COMMENT '设置key',
                                   `setting_value` varchar(1024) NOT NULL COMMENT '设置值',
                                   `setting_comment` varchar(1024) NOT NULL COMMENT '设置注释',
                                   `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                                   `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置项 ';


-- --------------------------------------------------------

--
-- 表的结构 `tags`
--

CREATE TABLE `tags` (
                        `tag_id` bigint(20) NOT NULL COMMENT '标签id',
                        `tag_name` varchar(128) NOT NULL COMMENT '标签名',
                        `tag_owner` bigint(20) NOT NULL DEFAULT '0' COMMENT '标签所属者 0代表公有',
                        `file_count` int(11) NOT NULL DEFAULT '0' COMMENT '文件数量',
                        `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                        `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签 ';

-- --------------------------------------------------------

--
-- 表的结构 `tag_relationship`
--

CREATE TABLE `tag_relationship` (
                                    `tag_relationship_id` bigint(20) NOT NULL COMMENT '标签对应关系',
                                    `user_file_id` bigint(20) NOT NULL COMMENT '用户文件Id',
                                    `tag_id` bigint(20) NOT NULL COMMENT '标签id',
                                    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                                    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签关系 ';

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
                        `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                        `user_name` varchar(128) NOT NULL COMMENT '用户名',
                        `user_password` varchar(128) NOT NULL COMMENT '密码',
                        `user_avatar` varchar(1024) DEFAULT NULL COMMENT '头像',
                        `user_gender` int(11) NOT NULL DEFAULT '0' COMMENT '性别 0男1女',
                        `user_email` varchar(128) NOT NULL COMMENT '电子邮箱',
                        `user_phone` varchar(11) NOT NULL COMMENT '手机号',
                        `email_checked` varchar(1) NOT NULL DEFAULT '0' COMMENT '电子邮箱是否验证通过',
                        `phone_checked` varchar(1) NOT NULL DEFAULT '0' COMMENT '手机号是否验证通过',
                        `space_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '空间大小（字节为单位）',
                        `used_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '已使用空间大小',
                        `admin` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否具有管理权限',
                        `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                        `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表 ';

-- --------------------------------------------------------

--
-- 表的结构 `user_files`
--

CREATE TABLE `user_files` (
                              `user_file_id` bigint(20) NOT NULL COMMENT '存储id',
                              `file_id` bigint(20) NOT NULL COMMENT '文件id',
                              `file_name` varchar(128) NOT NULL COMMENT '文件名',
                              `file_size` bigint(20) NOT NULL COMMENT '文件大小',
                              `file_parent_path` varchar(1024) NOT NULL COMMENT '父目录的全路径 存id譬如1/2/3/4',
                              `file_type` varchar(32) NOT NULL COMMENT '文件类型',
                              `recycle_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '回收站批次',
                              `share_count` int(11) NOT NULL COMMENT '共享次数',
                              `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                              `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户文件 ';

-- --------------------------------------------------------

--
-- 表的结构 `user_share`
--

CREATE TABLE `user_share` (
                              `share_id` varchar(6) NOT NULL COMMENT '分享id',
                              `user_id` bigint(20) NOT NULL COMMENT '用户id',
                              `user_file_id` bigint(20) NOT NULL COMMENT '文件id',
                              `share_type` int(11) NOT NULL COMMENT '分享类型 0-公有分享\r\n1-企业内分享（登录后可以查看）\r\n2-私有分享（指定人查看）\r\n3-密码查看\r\n4-仅通过访问码查看',
                              `max_download_times` int(11) NOT NULL COMMENT '最大下载次数',
                              `max_file_dump_times` int(11) NOT NULL COMMENT '最大转存次数',
                              `expire_time` datetime NOT NULL COMMENT '过期时间 -1为永不过期即 1970-01-01T07:59:59',
                              `download_times` int(11) NOT NULL COMMENT '下载次数',
                              `file_dump_time` int(11) NOT NULL COMMENT '转存次数',
                              `share_password` varchar(128) DEFAULT NULL COMMENT '密码',
                              `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
                              `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户分享 ';

--
-- 转储表的索引
--

--
-- 表的索引 `files`
--
ALTER TABLE `files`
    ADD PRIMARY KEY (`file_id`);

--
-- 表的索引 `file_history`
--
ALTER TABLE `file_history`
    ADD PRIMARY KEY (`history_id`);

--
-- 表的索引 `file_inbox`
--
ALTER TABLE `file_inbox`
    ADD PRIMARY KEY (`inbox_id`);

--
-- 表的索引 `groups`
--
ALTER TABLE `groups`
    ADD PRIMARY KEY (`group_id`);

--
-- 表的索引 `group_relationship`
--
ALTER TABLE `group_relationship`
    ADD PRIMARY KEY (`group_relationship_id`);

--
-- 表的索引 `inner_share`
--
ALTER TABLE `inner_share`
    ADD PRIMARY KEY (`inner_share_id`);

--
-- 表的索引 `receiving_record`
--
ALTER TABLE `receiving_record`
    ADD PRIMARY KEY (`receiving_id`);

--
-- 表的索引 `recycle`
--
ALTER TABLE `recycle`
    ADD PRIMARY KEY (`recycle_id`);

--
-- 表的索引 `system_settings`
--
ALTER TABLE `system_settings`
    ADD PRIMARY KEY (`setting_key`);

--
-- 表的索引 `tags`
--
ALTER TABLE `tags`
    ADD PRIMARY KEY (`tag_id`);

--
-- 表的索引 `tag_relationship`
--
ALTER TABLE `tag_relationship`
    ADD PRIMARY KEY (`tag_relationship_id`);

--
-- 表的索引 `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`user_id`);

--
-- 表的索引 `user_files`
--
ALTER TABLE `user_files`
    ADD PRIMARY KEY (`user_file_id`);

--
-- 表的索引 `user_share`
--
ALTER TABLE `user_share`
    ADD PRIMARY KEY (`share_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
