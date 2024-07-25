//package com.xrbin.utils;
//
//
//import ddlog.converted_logic.*;
//import ddlogapi.DDlogAPI;
//import ddlogapi.DDlogCommand;
//import ddlogapi.DDlogException;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.function.Predicate;
//
//public class Ddlog {
//
//    private DDlogAPI api = null;
//
//    private int outputCount = 0;
//
//    public Ddlog() {
//        try {
//            api = new DDlogAPI(4, false);
//            api.recordCommands(null, false);
//        } catch (DDlogException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Ddlog(int i) {
//        // 仅用于本地测试 因为本地缺少环境，这导致我们没法真的运行程序
//    }
//
//    void runAnalysis_Example(String[] commands) throws DDlogException {
//        /* start transaction */
//        {
//            this.api.transactionStart();
//            converted_logicUpdateBuilder c = new converted_logicUpdateBuilder();
//
//            // TODO 实际运行中，commands将从文件中获取
//            // String[] commands = {"insert R_StringRaw_shadow(\"1.2\",\"1.2\"),",
//            // "insert R_MethodInvocation_Line_shadow(\"<cn.nju.edu.shentianqi.Log: void
//            // main(java.lang.String[])>/java.lang.Integer.valueOf/1\",45),",
//            // "insert R_TableSwitch_Target_shadow(\"<cn.nju.edu.shentianqi.Main: void
//            // parseArgs(java.lang.String[])>/table-switch/0\",2,54),",
//            // "insert R_StringRaw_shadow(\"<<\\\\\\\\\\\":
//            // \\\\\\\\\\\">>\",\"<<\\\\\\\\\\\": \\\\\\\\\\\">>\"),"};
//
//            try {
//                processCommands(c, commands);
//            } catch (InvocationTargetException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            c.applyUpdates(this.api);
//            // 这个方法是把我们在c上记录的内容全部写入程序中
//
//            converted_logicUpdateParser.transactionCommitDumpChanges(this.api, this::onCommit);
//            // 以上函数等价于 commit dump_changes;
//            // 这个命令会结束 transaction 所以开启下一次transaction需要重新start
//            // 并且使用新的converted_logicUpdateBuilder
//            // 这个开销是值得的
//        }
//
//        /* end program 注意 这是退出程序的意思 */
//        this.api.stop();
//
//        System.out.printf("total output: %d%n", outputCount);
//
//    }
//
//    void processCommands(converted_logicUpdateBuilder c, String[] commands)
//            throws InvocationTargetException, IllegalAccessException {
//        // 处理commands 产生了relationName 和 args
//        List<Method> c_methods = Arrays.asList(c.getClass().getMethods());
//        Map<String, Method> methodMap = new HashMap<>();
//        List<String> stringList = new ArrayList<>();
//        for (String command : commands) {
//            String relationName;
//            String[] args;
//            boolean insert = true;
//            if (command.startsWith("insert ")) {
//            } else if (command.startsWith("delete ")) {
//                insert = false;
//            } else
//                continue;
//            command = command.substring(7);
//            // "insert R_StringRaw_shadow(\"1.2\",\"1.2\"),"
//            // "R_StringRaw_shadow(\"1.2\",\"1.2\"),"
//            if (command.charAt(command.length() - 1) != ')' && command.charAt(command.length() - 2) == ')')
//                command = command.substring(0, command.length() - 1);// "R_StringRaw_shadow(\"1.2\",\"1.2\")"
//            int firstPos = command.indexOf('(');
//            relationName = command.substring(0, firstPos);// "R_StringRaw_shadow"
//            command = command.substring(firstPos + 1, command.length() - 1);// "\"1.2\",\"1.2\""
//            dealArgsInCommand(command, stringList);
//            args = stringList.toArray(new String[0]);
//            commandInput(c, c_methods, relationName, args, insert, methodMap);
//        }
//    }
//
//    void dealArgsInCommand(String subcommand, List<String> stringList) {
//        stringList.clear();
//        int pos = 0, length = subcommand.length();
//        while (pos < length) {
//            if (subcommand.charAt(pos) == '"') {
//                // 这里是字符串类型 我们需要找到一个匹配的'\"' 且它的后面应该是','
//                // " x x x \ \ \ " " , x
//                // 2 3 4 5 6 7 8 9 10 11 12
//                // 10是正确匹配的情况
//                int i;
//                for (i = pos + 1; i < length; i++) {
//                    switch (subcommand.charAt(i)) {
//                        case '\\':
//                            // 忽视所有的转义字符
//                            i++;
//                            break;
//                        case '\"':
//                            if (i == length - 1) {
//                                // "xxx" 这种情况
//                                stringList.add(subcommand.substring(pos + 1, i));
//                                pos = i + 1;
//                            } else {
//                                if (subcommand.charAt(i + 1) == ',') {
//                                    stringList.add(subcommand.substring(pos + 1, i));
//                                    // "a","x"
//                                    // p i
//                                    pos = i + 2;
//                                    i = length;// 为了退出循环
//                                }
//                            }
//                            break;
//                        default:
//                    }
//                }
//            } else {
//                // 这里是int类型 直接在这里找'，'
//                int posTmp = subcommand.indexOf(',', pos);
//                if (posTmp == -1) {
//                    // 这说明后面不再有','了
//                    stringList.add(subcommand.substring(pos));
//                    pos = length;
//                } else {
//                    stringList.add(subcommand.substring(pos, posTmp));
//                    pos = posTmp + 1;
//                }
//            }
//        }
//    }
//
//    void commandInput(converted_logicUpdateBuilder c, List<Method> c_methods, String relationName, String[] args,
//                      boolean insert, Map<String, Method> methodMap) throws InvocationTargetException, IllegalAccessException {
//        // input relation RMainClass_shadow(__class:TClassType
//        // 这里将使用反射API来处理相关问题
//        // onCommit不使用反射是因为不好用 这里需要的是调用函数
//        // 注意 相关函数参数只有String int两种类型 所以处理起来相对简单
//
//        // c.getClass().getMethod()
//        String methodName = (insert ? "insert_" : "delete_") + relationName;
//        Method method = methodMap.get(methodName);
//        if (method == null) {
//            method = FindUtils.findByMethodName(c_methods, methodName);
//            methodMap.put(methodName, method);
//        }
//        // 以上使用Map作为缓存加速获取信息
//        if (method == null) {
//            throw new IllegalArgumentException(
//                    String.format("unknown relationName %s, could not find method %s ", relationName, methodName));
//        } else {
//            Class<?>[] parameterClazz = method.getParameterTypes();
//            List<Object> listValue = new ArrayList<Object>();
//            if (parameterClazz.length != args.length)
//                throw new IllegalArgumentException(
//                        String.format("relationName %s, args length err! args: %s need parameters: %s",
//                                relationName, Arrays.toString(args), Arrays.toString(parameterClazz)));
//            for (int i = 0; i < parameterClazz.length; i++) {
//                if (parameterClazz[i].getTypeName().equals("int")) {
//                    listValue.add(Integer.parseInt(args[i]));
//                } else if (parameterClazz[i].getTypeName().equals("java.lang.String")) {
//                    listValue.add(args[i]);
//                } else {
//                    throw new IllegalArgumentException(
//                            String.format("meet a new parameter type at relation: %s, method: %s.",
//                                    relationName, method));
//                }
//            }
//            // 调用参数准备完毕
//            method.invoke(c, listValue.toArray());
//        }
//    }
//
//    void onCommit(DDlogCommand<Object> command) {
//        // if (command.kind() == DDlogCommand.Kind.Insert) {
//        // //是一个新增的结果；
//        // } else if (command.kind() == DDlogCommand.Kind.DeleteVal) {
//        // //表示这个结果被删除了
//        // }
//        if (command.weight() > 0) {
//            // 是一个新增的结果；
//        } else {
//            // 表示这个结果被删除了
//        }
//        // 下面的写法减少了函数调用栈 效率更高
//        outputCount++;
//        switch (command.relid()) {
//            case converted_logicRelation.RApplicationMethod:
//                RApplicationMethodReader rApplicationMethodReader = (RApplicationMethodReader) command.value();
//                rApplicationMethodReader.__method();
//                break;
//            case converted_logicRelation.RStats_Metrics:
//                RStats_MetricsReader rStats_metricsReader = (RStats_MetricsReader) command.value();
//                rStats_metricsReader._order();
//                rStats_metricsReader._msg();
//                rStats_metricsReader._c();
//                break;
//            case converted_logicRelation.RMockObject:
//                RMockObjectReader rMockObjectReader = (RMockObjectReader) command.value();
//                rMockObjectReader.__value();// 使用这种方式获得相关数据，下略
//                rMockObjectReader.__class();
//                break;
//            case converted_logicRelation.RisOpaqueInstruction:
//                RisOpaqueInstructionReader risOpaqueInstructionReader = (RisOpaqueInstructionReader) command.value();
//                risOpaqueInstructionReader.__insn();
//                break;
//            case converted_logicRelation.RMVPT:
//                RMVPTReader rmvptReader = (RMVPTReader) command.value();
//                rmvptReader.__hctx();
//                rmvptReader.__value();
//                rmvptReader.__ctx1();
//                rmvptReader.__ctx2();
//                rmvptReader.__var();
//                break;
//            case converted_logicRelation.R_AssignHeapAllocation:
//                R_AssignHeapAllocationReader r_assignHeapAllocationReader = (R_AssignHeapAllocationReader) command
//                        .value();
//                r_assignHeapAllocationReader.__instruction();
//                r_assignHeapAllocationReader.__index();
//                r_assignHeapAllocationReader.__heap();
//                r_assignHeapAllocationReader.__to();
//                r_assignHeapAllocationReader.__inmethod();
//                r_assignHeapAllocationReader.__linenumber();
//                break;
//            case converted_logicRelation.R_MethodInvocation_Line:
//                R_MethodInvocation_LineReader r_methodInvocation_lineReader = (R_MethodInvocation_LineReader) command
//                        .value();
//                r_methodInvocation_lineReader.__instruction();
//                r_methodInvocation_lineReader._line();
//                break;
//            case converted_logicRelation.RmainAnalysis_MockedMethodReturns:
//                RmainAnalysis_MockedMethodReturnsReader rmainAnalysis_mockedMethodReturnsReader = (RmainAnalysis_MockedMethodReturnsReader) command
//                        .value();
//                rmainAnalysis_mockedMethodReturnsReader.__method();
//                rmainAnalysis_mockedMethodReturnsReader.__alloc();
//                rmainAnalysis_mockedMethodReturnsReader.__type();
//            case converted_logicRelation.RmainAnalysis_AssignInvokedynamic:
//                RmainAnalysis_AssignInvokedynamicReader rmainAnalysis_assignInvokedynamicReader = (RmainAnalysis_AssignInvokedynamicReader) command
//                        .value();
//                rmainAnalysis_assignInvokedynamicReader.__insn();
//                rmainAnalysis_assignInvokedynamicReader.__ret();
//                rmainAnalysis_assignInvokedynamicReader.__value();
//                rmainAnalysis_assignInvokedynamicReader.__type();
//                break;
//            case converted_logicRelation.RmainAnalysis_AppLoadInstanceField:
//                RmainAnalysis_AppLoadInstanceFieldReader rmainAnalysis_appLoadInstanceFieldReader = (RmainAnalysis_AppLoadInstanceFieldReader) command
//                        .value();
//                rmainAnalysis_appLoadInstanceFieldReader.__base();
//                rmainAnalysis_appLoadInstanceFieldReader.__sig();
//                rmainAnalysis_appLoadInstanceFieldReader.__to();
//                rmainAnalysis_appLoadInstanceFieldReader.__inmethod();
//                break;
//            case converted_logicRelation.RmainAnalysis_AppStoreInstanceField:
//                RmainAnalysis_AppStoreInstanceFieldReader rmainAnalysis_appStoreInstanceFieldReader = (RmainAnalysis_AppStoreInstanceFieldReader) command
//                        .value();
//                rmainAnalysis_appStoreInstanceFieldReader.__from();
//                rmainAnalysis_appStoreInstanceFieldReader.__base();
//                rmainAnalysis_appStoreInstanceFieldReader.__signature();
//                rmainAnalysis_appStoreInstanceFieldReader.__inmethod();
//                break;
//            case converted_logicRelation.RmainAnalysis_AppCallGraphEdge:
//                RmainAnalysis_AppCallGraphEdgeReader rmainAnalysis_appCallGraphEdgeReader = (RmainAnalysis_AppCallGraphEdgeReader) command
//                        .value();
//                // .type HContext = [value:Value]
//                // .type Context = [value1:Value. value2:Value]
//                rmainAnalysis_appCallGraphEdgeReader.__callerCtx();
//                rmainAnalysis_appCallGraphEdgeReader.__invocation();
//                rmainAnalysis_appCallGraphEdgeReader.__calleeCtx();
//                rmainAnalysis_appCallGraphEdgeReader.__method();
//                if (rmainAnalysis_appCallGraphEdgeReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_None) {
//                    // none类型 其实是没有read操作的
//                    rmainAnalysis_appCallGraphEdgeReader.__callerCtx().toString();
//                } else if (rmainAnalysis_appCallGraphEdgeReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_Some) {
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_appCallGraphEdgeReader
//                            .__callerCtx()).x().a0();
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_appCallGraphEdgeReader
//                            .__callerCtx()).x().a1();
//                }
//                // rmainAnalysis_appCallGraphEdgeReader.__calleeCtx() 也是类似的
//                break;
//            case converted_logicRelation.RmainAnalysis_AVPT:
//                RmainAnalysis_AVPTReader rmainAnalysis_avptReader = (RmainAnalysis_AVPTReader) command.value();
//                rmainAnalysis_avptReader.__hctx();
//                rmainAnalysis_avptReader.__value();
//                rmainAnalysis_avptReader.__ctx();
//                rmainAnalysis_avptReader.__var();
//                if (rmainAnalysis_avptReader.__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_None) {
//                } else if (rmainAnalysis_avptReader.__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_Some) {
//                    ((ddlog_std_Option__stringReader.ddlog_std_Some) rmainAnalysis_avptReader.__hctx()).x();// String
//                }
//                if (rmainAnalysis_avptReader
//                        .__ctx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_None) {
//                } else if (rmainAnalysis_avptReader
//                        .__ctx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_Some) {
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_avptReader.__ctx()).x()
//                            .a0();
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_avptReader.__ctx()).x()
//                            .a1();
//                }
//                break;
//            case converted_logicRelation.RmainAnalysis_InstanceFieldPointsTo:
//                RmainAnalysis_InstanceFieldPointsToReader rmainAnalysis_instanceFieldPointsToReader = (RmainAnalysis_InstanceFieldPointsToReader) command
//                        .value();
//                rmainAnalysis_instanceFieldPointsToReader.__hctx();
//                rmainAnalysis_instanceFieldPointsToReader.__value();
//                rmainAnalysis_instanceFieldPointsToReader.__sig();
//                rmainAnalysis_instanceFieldPointsToReader.__basehctx();// Same to __hctx
//                rmainAnalysis_instanceFieldPointsToReader.__basevalue();
//                if (rmainAnalysis_instanceFieldPointsToReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_None) {
//                } else if (rmainAnalysis_instanceFieldPointsToReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_Some) {
//                    ((ddlog_std_Option__stringReader.ddlog_std_Some) rmainAnalysis_instanceFieldPointsToReader.__hctx())
//                            .x();
//                }
//                break;
//            case converted_logicRelation.RmainAnalysis_StaticFieldPointsTo:
//                RmainAnalysis_StaticFieldPointsToReader rmainAnalysis_staticFieldPointsToReader = (RmainAnalysis_StaticFieldPointsToReader) command
//                        .value();
//                rmainAnalysis_staticFieldPointsToReader.__hctx();
//                rmainAnalysis_staticFieldPointsToReader.__value();
//                rmainAnalysis_staticFieldPointsToReader.__sig();
//                if (rmainAnalysis_staticFieldPointsToReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_None) {
//                } else if (rmainAnalysis_staticFieldPointsToReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_Some) {
//                    ((ddlog_std_Option__stringReader.ddlog_std_Some) rmainAnalysis_staticFieldPointsToReader.__hctx())
//                            .x();
//                }
//                break;
//            case converted_logicRelation.RmainAnalysis_CallGraphEdge:
//                RmainAnalysis_CallGraphEdgeReader rmainAnalysis_callGraphEdgeReader = (RmainAnalysis_CallGraphEdgeReader) command
//                        .value();
//                rmainAnalysis_callGraphEdgeReader.__callerCtx();
//                rmainAnalysis_callGraphEdgeReader.__invocation();
//                rmainAnalysis_callGraphEdgeReader.__calleeCtx();// same to __callerCtx
//                rmainAnalysis_callGraphEdgeReader.__method();
//                if (rmainAnalysis_callGraphEdgeReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_None) {
//                } else if (rmainAnalysis_callGraphEdgeReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_Some) {
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_callGraphEdgeReader
//                            .__callerCtx()).x().a0();
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_callGraphEdgeReader
//                            .__callerCtx()).x().a1();
//                }
//                break;
//            case converted_logicRelation.RmainAnalysis_Reachable:
//                RmainAnalysis_ReachableReader rmainAnalysis_reachableReader = (RmainAnalysis_ReachableReader) command
//                        .value();
//                rmainAnalysis_reachableReader.__method();
//                break;
//            case converted_logicRelation.RmainAnalysis_ReachableContext:
//                RmainAnalysis_ReachableContextReader rmainAnalysis_reachableContextReader = (RmainAnalysis_ReachableContextReader) command
//                        .value();
//                rmainAnalysis_reachableContextReader.__ctx();
//                rmainAnalysis_reachableContextReader.__method();
//                if (rmainAnalysis_reachableContextReader
//                        .__ctx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_None) {
//                } else if (rmainAnalysis_reachableContextReader
//                        .__ctx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_Some) {
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_reachableContextReader
//                            .__ctx()).x().a0();
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_reachableContextReader
//                            .__ctx()).x().a1();
//                }
//                break;
//            case converted_logicRelation.RmainAnalysis_SimulatedNativeAllocation:
//                RmainAnalysis_SimulatedNativeAllocationReader rmainAnalysis_simulatedNativeAllocationReader = (RmainAnalysis_SimulatedNativeAllocationReader) command
//                        .value();
//                rmainAnalysis_simulatedNativeAllocationReader.__heap();
//                rmainAnalysis_simulatedNativeAllocationReader.__method();
//                break;
//            case converted_logicRelation.RmainAnalysis_CollectionClass:
//                RmainAnalysis_CollectionClassReader rmainAnalysis_collectionClassReader = (RmainAnalysis_CollectionClassReader) command
//                        .value();
//                rmainAnalysis_collectionClassReader.__class();
//                break;
//            case converted_logicRelation.RmainAnalysis_AnyCallGraphEdge:
//                RmainAnalysis_AnyCallGraphEdgeReader rmainAnalysis_anyCallGraphEdgeReader = (RmainAnalysis_AnyCallGraphEdgeReader) command
//                        .value();
//                rmainAnalysis_anyCallGraphEdgeReader.__instr();
//                rmainAnalysis_anyCallGraphEdgeReader.__method();
//                break;
//            case converted_logicRelation.RmainAnalysis_configuration_ContextResponse:
//                RmainAnalysis_configuration_ContextResponseReader rmainAnalysis_configuration_contextResponseReader = (RmainAnalysis_configuration_ContextResponseReader) command
//                        .value();
//                rmainAnalysis_configuration_contextResponseReader.__callerCtx();
//                rmainAnalysis_configuration_contextResponseReader.__hctx();
//                rmainAnalysis_configuration_contextResponseReader.__invo();
//                rmainAnalysis_configuration_contextResponseReader.__value();
//                rmainAnalysis_configuration_contextResponseReader.__method();
//                rmainAnalysis_configuration_contextResponseReader.__calleeCtx();// same to __callerCtx()
//                if (rmainAnalysis_configuration_contextResponseReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_None) {
//                } else if (rmainAnalysis_configuration_contextResponseReader
//                        .__callerCtx() instanceof ddlog_std_Option___string__string_Reader.ddlog_std_Some) {
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_configuration_contextResponseReader
//                            .__callerCtx()).x().a0();
//                    ((ddlog_std_Option___string__string_Reader.ddlog_std_Some) rmainAnalysis_configuration_contextResponseReader
//                            .__callerCtx()).x().a1();
//                }
//                if (rmainAnalysis_configuration_contextResponseReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_None) {
//                } else if (rmainAnalysis_configuration_contextResponseReader
//                        .__hctx() instanceof ddlog_std_Option__stringReader.ddlog_std_Some) {
//                    ((ddlog_std_Option__stringReader.ddlog_std_Some) rmainAnalysis_configuration_contextResponseReader
//                            .__hctx()).x();
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("not output relation, id " + command.relid());
//        }
//    }
//
//    public static void main(String[] args) {
//        // testReflectionApi();
//        // testRealCommandFile();
//        run();
//    }
//
//    static void testReflectionApi() {
//        try {
//            Object clazz = Class.forName("FindUtils").newInstance();
//            for (Method method : clazz.getClass().getMethods()) {
//                System.out.println(method.getName());
//                Class<?>[] parameterClazz = method.getParameterTypes();
//                for (Class<?> p : parameterClazz) {
//                    System.out.println("\t" + p.getTypeName());
//                }
//            }
//            /*
//             * findByProperty
//             * findByMethodName
//             * wait
//             * wait
//             * wait
//             * equals
//             * toString
//             * hashCode
//             * getClass
//             * notify
//             * notifyAll
//             */
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static void testRealCommandFile() {
//        // 小程序上测试已经通过 接下来将尝试从实际程序中运行这些玩意
//        // 在之前的程序上已经通过了格式检查，证明我们的程序可以处理这些输入。
//        converted_logicUpdateBuilder c = new converted_logicUpdateBuilder();
//        Ddlog test = new Ddlog();
//
//        String[] commands = { "insert R_StringRaw_shadow(\"1.2\",\"1.2\"),",
//                "insert R_MethodInvocation_Line_shadow(\"<cn.nju.edu.shentianqi.Log: void main(java.lang.String[])>/java.lang.Integer.valueOf/1\",45),",
//                "insert R_TableSwitch_Target_shadow(\"<cn.nju.edu.shentianqi.Main: void parseArgs(java.lang.String[])>/table-switch/0\",2,54),",
//                "insert R_StringRaw_shadow(\"<<\\\\\\\\\\\": \\\\\\\\\\\">>\",\"<<\\\\\\\\\\\": \\\\\\\\\\\">>\"),",
//                "start;" };
//
//        // /Users/shentianqi/Downloads/多线程实验7/7-2/7-2/DDPT1/converted_logic.dat
//        List<String> list = new ArrayList<>();
//        try {
//            Scanner scanner = new Scanner(
//                    new File("/home/xrbin/OtherProjects/yanniss-doop-r-4.24.10/out/all-facts-only/converted.dat"));
//            while (scanner.hasNextLine()) {
//                list.add(scanner.nextLine());
//            }
//            scanner.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            test.processCommands(c, list.toArray(new String[0]));
//        } catch (InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static void run() {
//        Ddlog test = new Ddlog();
//
//        List<String> list = new ArrayList<>();
//        try {
//            Scanner scanner = new Scanner(new File("/home/xrbin/OtherProjects/yanniss-doop-r-4.24.10/out/all-facts-only/converted.dat"));
//            while (scanner.hasNextLine()) {
//                list.add(scanner.nextLine());
//            }
//            scanner.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            test.runAnalysis_Example(list.toArray(new String[0]));
//        } catch (DDlogException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//final class FindUtils {
//    public static <T> T findByProperty(Collection<T> col, Predicate<T> filter) {
//        return col.stream().filter(filter).findFirst().orElse(null);
//    }
//
//    public static Method findByMethodName(Collection<Method> methods, String methodName) {
//        return FindUtils.findByProperty(methods, method -> method.getName().equals(methodName));
//    }
//
//    public void forTest(String a, int b, String c) {
//    }
//}
///*
//    //First DDlog example
//
//    typedef Category = CategoryStarWars
//            | CategoryOther
//
//    input relation Word1(word: string, cat: Category)
//    input relation Word2(word: string, cat: Category)
//
//    output relation Phrases(phrase: string)
//
//    // Rule
//    Phrases(w1 ++ " " ++ w2) :- Word1(w1, cat), Word2(w2, cat).
// */