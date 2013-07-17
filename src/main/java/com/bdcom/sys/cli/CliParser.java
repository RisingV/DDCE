package com.bdcom.sys.cli;

import com.bdcom.biz.pojo.BaseTestRecord;
import org.jargp.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-17    <br/>
 * Time: 10:49  <br/>
 */
public class CliParser {

    /** Command line parameter definitions. */
    private static final ParameterDef[] BASE_PARM_DEFS = {
            new StringDef('p', "testerNum", "number of the tester"),
            new StringDef('t', "type", "test type"),
            new StringDef('b', "beginTime", "begin time of test"),
            new StringDef('s', "script", "name of test script"),
            new StringDef('c', "consoleName", "name of console"),
            new StringDef('n', "serialNumber", "serial number"),
            new StringDef('i', "id", "ID"),
            new StringDef('e', "verOfEPROM", "version of EPROM"),
            new StringDef('f', "volOfFlash", "volume of flash"),
            new StringDef('r', "volOfDRam", "volume of SDRam"),
            new StringDef('v', "softwareInfo", "information of software"),
            new StringDef('h', "hardwareInfo", "information of hardware"),
            new StringDef('m', "modelType", "model type"),
            new StringDef('d', "endTime", "end time of test"),
            new StringDef('a', "status", "test status"),
            new StringDef('z', "mac", "MAC"),
            new StringDef('u', "step", "STEP"),
            new StringDef('o', "memo", "MEMO"),
            new StringDef('#', "randomID", "random ID"),
            new BoolDef('?', "helpFlag", "display usage information"),
            new BoolDef('$', "ifCommit", "if commit this test record")
    };

    final ArgumentProcessor proc;

    public CliParser() {
        proc = new ArgumentProcessor(BASE_PARM_DEFS);
    }

    public BaseTestRecord parse(String[] args) {
        BaseTestRecord record = new BaseTestRecord();

        if (args.length > 0) {
            proc.processArgs(args, record);
            StringTracker xargs = proc.getArgs();
            while (xargs.hasNext()) {
                System.out.println("extra argument: " + xargs.next());
            }
        } else {
            record.setHelpFlag(true);
        }

        // print usage information if problem with parameters
        if ( record.isHelpFlag() ) {
            System.out.println("\nUsage: java com.bdcom.datadispacher.CMDParser [-options] extra\n" +
                    "Options are:");
            proc.listParameters(80, System.out);
        }

        return record;

    }

}
