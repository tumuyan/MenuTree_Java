package com.company;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 生成文件树程序，带两个参数：path 需要遍历的路径  regex 需要匹配的文件名后缀。
 * 当任意参数为exit 时，退出程序
 * 否则生成文件树到当前目录下的MenuTree.txt 文件中
 *
 * @author whateverxl@outlook.com
 * <p>@createTime 2018-07-11 15:49</p>
 **/
public class MenuTree {
    private final static Logger logger = Logger.getLogger(MenuTree.class.toString());
    /**
     * 递归的层级
     */
    private Integer level = 0;
    /**
     * 用来标记| 符号应该出现的位置
     */
    private Stack<Integer> marks = new Stack<>();

    /**
     * 迭代的主要逻辑区域
     * @param start 路径
     * @param regex 文件匹配式
     * @return StringBuild对象，包含文件树
     */
    private StringBuilder recurseDirs(File start, String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        File[] files = start.listFiles();
        if (files != null) {
            /*出现空目录*/
            if (files.length == 0) {
                content(level + 1, stringBuilder);
                stringBuilder.append("  \n");
            }
            for (int i = 0; i < files.length; i++) {
                File item = files[i];
                //如果父目录的文件数大于1，即有兄弟文件，记录此层级，因为自身为目录会出现迭代，需要记住当前标记，连接兄弟文件
                if (files.length > 1 && i == 0) {
                    marks.push(level);
                }
                if (item.isDirectory()) {
                    makeFormatName(stringBuilder, item);
                    /*进入更深的一次遍历*/
                    level++;
                    /*无论是目录还是文件当到达末尾的时候释放当前层数标记*/
                    if (i == files.length - 1 && files.length > 1) {
                        marks.pop();
                    }
                    stringBuilder.append(recurseDirs(item, regex));
                    level--;
                } else {
                    if (item.getName().matches(regex)) {
                        makeFormatName(stringBuilder, item);
                        checkLastElem(i, stringBuilder, files);
                    }
                }
            }

        } else {
            logger.warning("当前目录" + start + "为空");
        }
        return stringBuilder;
    }

    private StringBuilder packageRecurseDirs(File start,String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        makeFormatName(stringBuilder, start);
        level++;
        stringBuilder.append(recurseDirs(start, regex));
        return stringBuilder;
    }
    private void makeFormatName(StringBuilder stringBuilder,File item) {
        content(level, stringBuilder);
        stringBuilder.append("+- ").append(item.getName()).append("  \n");
    }
    private void checkLastElem(int i,StringBuilder stringBuilder,File[] files) {
        if (i == files.length - 1) {
            content(level, stringBuilder);
            stringBuilder.append("  \n");
        }
        /*无论是目录还是文件当到达末尾的时候释放当前层数标记*/
        if (i == files.length - 1 && files.length > 1) {
            marks.pop();
        }
    }
    /**
     * 输出层级
     *
     * @param level         递归的层级
     * @param stringBuilder 字符编辑器
     * @author xuliang@asiainfo.com 2018/7/12 12:13
     */
    private void content(Integer level, StringBuilder stringBuilder) {
        for (int i = 0; i < level; i++) {
            if (marks.search(i) != -1) {
                stringBuilder.append("|   ");
            } else {
                stringBuilder.append("    ");
            }
        }
    }

    public String walk(String path, String regex) {
        return packageRecurseDirs(new File(path), regex).toString();
    }

    public String walk(File path) {
        return packageRecurseDirs(path, ".*").toString();
    }

    public String walk(String path) {
        return walk(new File(path));
    }

    public String walk() {
        return packageRecurseDirs(new File("."), ".*").toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("欢迎使用目录树生成工具！！！（windows)");
        System.out.println("请务必将MenuTree.bat文件和MenuTree.jar放置在同级目录并确保本机已有java JRE运行环境");
        System.out.println("本程序可以遍历输入目录下的所有目录和文件，并以层级的形式展示\n"+"名称部分展示格式为+-  文件名或者目录名 \\n");
        System.out.println("每块目录结束后会格式化空格");
        System.out.println("如需退出，请输入exit");
        System.out.println("生成的文件MenuTree.txt在本程序同级目录下");
        System.out.println("制作人：徐亮");
        System.out.println("联系方式：whateverxl@outlook.com\n\n");
        // all→finest→finer→fine→config→info→warning→server→off
        // 级别依次升高，后面的日志级别会屏蔽之前的级别
        logger.setLevel(Level.INFO);
        logger.info("program start\n");
        MenuTree menuTree = new MenuTree();
        if (args.length <= 0) {
            args = menuTree.getArgs();
        }
        long t = System.currentTimeMillis();
        String menutree = menuTree.run(args, menuTree);
        long l = System.currentTimeMillis();
        logger.info("program end and begin to saving file Menu.txt.\n this time token" + (l - t) + "ms");
        menuTree.savingFile(menutree);
        logger.info("saving file done token time " + (System.currentTimeMillis() - l) + " ms");
    }

    /**
     * 获取参数
     * @return String[] 返回获取到的参数
     */
    private String[] getArgs() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入扫描路径（直接回车或输入空扫描当前目录）：\n");
        String path = scanner.nextLine();
        exit(path);
        System.out.println("请输入需要扫描的文件后缀例如.txt（回车或不输入扫描所有文件）：\n");
        String regex = scanner.nextLine();
        exit(regex);
        return defaultArgs(path, regex);
    }

    /**
     * 当无输入或者输入为空格时配置默认参数
     *
     * @param path  文件起始路径
     * @param regex 文件匹配后缀
     * @return 启动参数
     * @author whateverxl@outlook.com 2018/7/13 11:57
     */

    private String[] defaultArgs(String path, String regex) {
        if (path.trim().equals("")) {
            path = ".";
        }
        if (regex.trim().equals("")) {
            regex = ".*";
        }
        return new String[]{path, regex};
    }

    /**
     * 检查输入的内容中是否有退出命令，如果有，就执行退出
     *
     * @param command 传入的命令
     * @author whateverxl@outlook.com 2018/7/13 11:57
     */

    private void exit(String command) {
        if (command.equals("exit")) {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("cmd.exe /C start wmic process where name='cmd.exe' call terminate");
                logger.info("程序终止");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 主要逻辑运行方法
     *
     * @param args     运行参数
     * @param menuTree 执行类
     * @return 返回文档树
     * @author whateverxl@outlook.com 2018/7/13 11:56
     */

    private String run(String[] args, MenuTree menuTree) {
        String s = "";
        if (args.length == 0) {
            logger.info("遍历当前目录 \n 获取所有文件  .*\n");
            s = menuTree.walk();
        } else if (args.length == 1) {
            logger.info("遍历目录:" + args[0] + " \n获取所有文件  .*\n");
            File file = new File(args[0]);
            if (file.isDirectory() && file.exists()) {
                s = menuTree.walk(file);
            } else {
                s = menuTree.walk(args[0]);
            }
        } else if (args.length == 2) {
            logger.info("遍历目录:" + args[0] + " \n获取文件格式为  " + args[1] + "\n");
            s = menuTree.walk(args[0], args[1]);
        } else {
            for (int i = 0; i < args.length; i++) {
                logger.info(args[i] + "+++++" + i + "\n");
            }
            logger.info(args.length + "传入参数长度\n");
            logger.info("传入参数有误！！！\n");
        }
        logger.info("输出\n" + s);
        return s;
    }

    /**
     * 保存字符串到当前目录下的Menu.txt文件中
     *
     * @param s 需要保存的字符串
     * @author whateverxl@outlook.com 2018/7/13 11:54
     */
    private void savingFile(String s) throws Exception {
        String savingPath = "./MenuTree.txt";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(savingPath);
            fileWriter.write(s);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }
    }

}
