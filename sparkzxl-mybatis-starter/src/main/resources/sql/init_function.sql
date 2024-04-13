DROP FUNCTION IF EXISTS `find_in_set_multiple`;
delimiter ;;
CREATE FUNCTION `find_in_set_multiple`(str_list VARCHAR(255), search_str VARCHAR(255))
    RETURNS int(11)
               DETERMINISTIC
BEGIN
    DECLARE pos INT DEFAULT 1;          -- 当前查找的起始位置
    DECLARE result INT DEFAULT 0;       -- 默认返回的匹配结果false
    DECLARE current_str VARCHAR(255);   -- 当前字符串
    DECLARE comma_pos INT;              -- 逗号的位置
    DECLARE length_long INT DEFAULT 0;            -- 字符串长度

    -- 去除数据库中字段为空的数据
    SET length_long = ISNULL(str_list);
    IF length_long = 1  THEN
        set pos = 0;
END IF;

    WHILE pos > 0 DO
        SET comma_pos = INSTR(str_list, ',');   -- 查找逗号的位置

        IF comma_pos = 0 THEN                   -- 如果没有逗号，那么字符串就是全部的字符串
            SET current_str = str_list;
            SET pos = 0;
ELSE
            SET current_str = SUBSTRING(str_list, 1, comma_pos - 1);    -- 截取逗号之前的字符串
            SET str_list = SUBSTRING(str_list, comma_pos + 1);
END IF;

        -- 如果当前字符串存在于搜索的字符串中，设置结果为1，结束循环
        IF FIND_IN_SET(current_str, search_str) > 0 THEN
            SET result = 1;
            SET pos = 0;
END IF;
END WHILE;

RETURN result;
END
;;
delimiter ;
