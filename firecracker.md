## [Firecracker](https://github.com/firecracker-microvm/firecracker)

[Firecracker: Lightweight Virtualization for Serverless Applications](https://assets.amazon.science/96/c6/302e527240a3b1f86c86c3e8fc3d/firecracker-lightweight-virtualization-for-serverless-applications.pdf)

[Getting Started with Firecracker](https://github.com/firecracker-microvm/firecracker/blob/master/docs/getting-started.md)

[Creating Custom rootfs and kernel Images](https://github.com/firecracker-microvm/firecracker/blob/master/docs/rootfs-and-kernel-setup.md)

```
wget https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-5.10.13.tar.xz
```

[Using the balloon device with Firecracker](https://github.com/firecracker-microvm/firecracker/blob/master/docs/ballooning.md)

[Firecracker Snapshotting](https://github.com/firecracker-microvm/firecracker/blob/master/docs/snapshotting/snapshot-support.md)

## [firecracker-containerd](https://github.com/firecracker-microvm/firecracker-containerd)

[Getting started with Firecracker and containerd](https://github.com/firecracker-microvm/firecracker-containerd/blob/master/docs/getting-started.md)

[Create Firecracker VM images for use with firecracker-containerd](https://github.com/firecracker-microvm/firecracker-containerd/blob/master/tools/image-builder/README.md)

[Firecracker Root Filesystem](https://github.com/firecracker-microvm/firecracker-containerd/blob/master/docs/root-filesystem.md)

[Julia Evans - Firecracker: start a VM in less than a second](https://jvns.ca/blog/2021/01/23/firecracker--start-a-vm-in-less-than-a-second/)

[Weaveworks / Ignite - Base images and kernels](https://github.com/weaveworks/ignite#base-images-and-kernels)

### Running Firecracker

Convert a Docker Container into an image:
```
$ docker_id=$(docker run -it --rm --detach al2_root)
$ dd if=/dev/zero of=amazoncorretto11.ext4 bs=1M count=1024
$ mkfs.ext4 amazoncorretto11.ext4
$ sudo mount amazoncorretto11.ext4 /mnt/test/
$ sudo docker cp $docker_id:/ /mnt/test/
$ sudo umount /mnt/test
$ docker kill $docker_id
```

Resize an image file
```
$ file test.img
test.img: Linux rev 1.0 ext4 filesystem data, UUID=5f4a5cba-9cfa-4fe3-9db3-4cbfbf3bf738 (extents) (64bit) (large files) (huge files)
$ mkdir /tmp/mnt
$ sudo mount test.img /tmp/mnt/ -o loop
$ df -h /tmp/mnt/
Filesystem      Size  Used Avail Use% Mounted on
/dev/loop0      1,5G  5,7M  1,5G   1% /tmp/mnt
$ dd if=/dev/zero bs=1G count=1 >> ./test.img
1+0 records in
1+0 records out
1073741824 bytes (1,1 GB) copied, 0,860954 s, 1,2 GB/s
$ resize2fs test.img
resize2fs 1.42.9 (28-Dec-2013)
Please run 'e2fsck -f test.img' first.
$ e2fsck -f test.img
e2fsck 1.42.9 (28-Dec-2013)
Pass 1: Checking inodes, blocks, and sizes
Pass 2: Checking directory structure
Pass 3: Checking directory connectivity
Pass 4: Checking reference counts
Pass 5: Checking group summary information
/rw: 125/196608 files (0.8% non-contiguous), 21953/393216 blocks
$ resize2fs test.img
resize2fs 1.42.9 (28-Dec-2013)
Resizing the filesystem on test.img to 655360 (4k) blocks.
The filesystem on test.img is now 655360 blocks long.
$ sudo mount test.img /tmp/mnt/ -o loop
$ df -h /tmp/mnt/
Filesystem      Size  Used Avail Use% Mounted on
/dev/loop0      2,4G  5,7M  2,4G   1% /tmp/mnt
```

Deleting a network namespace
```
ip netns delete ns-0
```

```
$ sudo setfacl -m u:${USER}:rw /dev/kvm
$ rm -f /tmp/firecracker.socket
$ /share/software/firecracker_release-v0.24.1/firecracker-v0.24.1-x86_64 --api-sock /tmp/firecracker.socket
```

```
$ curl --unix-socket /tmp/firecracker.socket -i -X PUT 'http://localhost/boot-source' -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{ "kernel_image_path": "hello-vmlinux.bin", "boot_args": "console=ttyS0 reboot=k panic=1 pci=off" }'

$ curl --unix-socket /tmp/firecracker.socket -i -X PUT 'http://localhost/drives/rootfs' -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{ "drive_id": "rootfs", "path_on_host": "/priv/simonisv/firecracker/amazoncorretto11.ext4", "is_root_device": true, "is_read_only": false }'

$ curl --unix-socket /tmp/firecracker.socket -i -X PUT 'http://localhost/machine-config' -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{ "vcpu_count": 3, "mem_size_mib": 1024, "ht_enabled": false }'

$ curl --unix-socket /tmp/firecracker.socket -i -X PUT 'http://localhost/actions' -H  'Accept: application/json' -H  'Content-Type: application/json' -d '{ "action_type": "InstanceStart" }'

```

#### Extracting an uncompressed Linux kernel image from a compressed Linux kernel file
[How to extract and disassemble a Linux kernel image (vmlinuz)](https://blog.packagecloud.io/eng/2016/03/08/how-to-extract-and-disassmble-a-linux-kernel-image-vmlinuz/)
[SuperUser: How do I uncompress vmlinuz to vmlinux?](https://superuser.com/a/298827)
```
$ /usr/src/linux-headers-5.4.0-65-generic/scripts/extract-vmlinux vmlinuz-5.4.0-65-generic > vmlinux-5.4.0-65-generic
```

## Lambda

### [Building Lambda functions with Java](https://github.com/awsdocs/aws-lambda-developer-guide/blob/main/doc_source/lambda-java.md#building-lambda-functions-with-java)

- Create a [AWS Lambda execution role](https://github.com/awsdocs/aws-lambda-developer-guide/blob/main/doc_source/lambda-intro-execution-role.md)
- Create a Lambda function in the [Lambda console](https://console.aws.amazon.com/lambda) (requires [Isengard](https://isengard.amazon.com/console-access) authenification wit MidWay, don't have to be in the VPN).
This will create a simple class `example.Hello` which implements `com.amazonaws.services.lambda.runtime.RequestHandler<java.lang.Object, example.Response>` by defining the method `handleRequest(Ljava/lang/Object;Lcom/amazonaws/services/lambda/runtime/Context;)Lexample/Response;`. The `example.Hello` is packaged together with the required parts of the [AWS Lambda Java Core Library](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-core) (i.e. [aws-lambda-java-core-1.2.0.jar](https://repo1.maven.org/maven2/com/amazonaws/aws-lambda-java-core/1.2.0/aws-lambda-java-core-1.2.0.jar)). It can be downloaded from the Lambda console (i.e. `Actions`->`Export function`) or with the AWS CLI (see below).
- Create a test event in the Lambda console and call the created Lambda fuction by hitting the `Test` button. The function can also be called with the AWS CLI (see below).
- [Configure the AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html). For this you have to create an [access key ID and secret access key](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-creds) and add them to a new role:
```
$ aws configure --profile lambda-test
AWS Access Key ID [None]: ***********
AWS Secret Access Key [None]: *********************
Default region name [None]: eu-central-1
Default output format [None]: json
```
- Configure [AWS CLI command completion](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-completion.html) by adding `complete -C '/usr/local/bin/aws_completer' aws` to your `~/.bashrc`
- Now you can list the Lambda function you've just created with the AWS CLI:
```
$ aws --profile lambda-test lambda list-functions
{
    "Functions": [
        {
            "FunctionName": "my-first-hello-world",
            "FunctionArn": "arn:aws:lambda:eu-central-1:417149921571:function:my-first-hello-world",
            "Runtime": "java11",
            "Role": "arn:aws:iam::417149921571:role/lambda-role-first-test",
            "Handler": "example.Hello::handleRequest",
            "CodeSize": 8928,
            "Description": "",
            "Timeout": 15,
            "MemorySize": 512,
            "LastModified": "2021-02-12T09:57:05.962+0000",
            "CodeSha256": "jYP1nUIZ6A7Mqr3J5GwlBCMva56NpgTZDLKGRkT7LQo=",
            "Version": "$LATEST",
            "TracingConfig": {
                "Mode": "PassThrough"
            },
            "RevisionId": "b35e4260-e111-41d3-a27c-4d42f052e397",
            "PackageType": "Zip"
        }
    ]
}
```
- You can easily invoke your Lambda function from the AWS CLI like this (the return value of the function will go to the file you specify as the last command line argument (`/tmp/response.txt` in this case)):
```
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --color on /tmp/response.txt
{
    "StatusCode": 200,
    "LogResult": "U1RBUlQgUmVxdWVzdElkOiA5NjRjOTJlNC1jN2Q5LTQ1MTgtYTRkNi1mN2YxMmZlMWZkOGEgVmVyc2lvbjogJExBVEVTVApFTkQgUmVxdWVzdElkOiA5NjRjOTJlNC1jN2Q5LTQ1MTgtYTRkNi1mN2YxMmZlMWZkOGEKUkVQT1JUIFJlcXVlc3RJZDogOTY0YzkyZTQtYzdkOS00NTE4LWE0ZDYtZjdmMTJmZTFmZDhhCUR1cmF0aW9uOiAyNS42MCBtcwlCaWxsZWQgRHVyYXRpb246IDI2IG1zCU1lbW9yeSBTaXplOiA1MTIgTUIJTWF4IE1lbW9yeSBVc2VkOiA5MSBNQglJbml0IER1cmF0aW9uOiAzNjYuMDMgbXMJCg==",
    "ExecutedVersion": "$LATEST"
}
$ base64 --decode -
U1RBUlQgUmVxdWVzdElkOiA5NjRjOTJlNC1jN2Q5LTQ1MTgtYTRkNi1mN2YxMmZlMWZkOGEgVmVyc2lvbjogJExBVEVTVApFTkQgUmVxdWVzdElkOiA5NjRjOTJlNC1jN2Q5LTQ1MTgtYTRkNi1mN2YxMmZlMWZkOGEKUkVQT1JUIFJlcXVlc3RJZDogOTY0YzkyZTQtYzdkOS00NTE4LWE0ZDYtZjdmMTJmZTFmZDhhCUR1cmF0aW9uOiAyNS42MCBtcwlCaWxsZWQgRHVyYXRpb246IDI2IG1zCU1lbW9yeSBTaXplOiA1MTIgTUIJTWF4IE1lbW9yeSBVc2VkOiA5MSBNQglJbml0IER1cmF0aW9uOiAzNjYuMDMgbXMJCg==
START RequestId: 964c92e4-c7d9-4518-a4d6-f7f12fe1fd8a Version: $LATEST
END RequestId: 964c92e4-c7d9-4518-a4d6-f7f12fe1fd8a
REPORT RequestId: 964c92e4-c7d9-4518-a4d6-f7f12fe1fd8a	Duration: 25.60 ms	Billed Duration: 26 ms	Memory Size: 512 MB	Max Memory Used: 91 MB	Init Duration: 366.03 ms
$ cat /tmp/response.txt
{"body":"Hello from Lambda!","statusCode":200}
```
- You can get more information about a function by using `lambda get-function`:
```
$ aws --profile lambda-test lambda get-function --function-name my-first-hello-world --
{
    "Configuration": {
        "FunctionName": "my-first-hello-world",
        "FunctionArn": "arn:aws:lambda:eu-central-1:417149921571:function:my-first-hello-world",
        "Runtime": "java11",
        "Role": "arn:aws:iam::417149921571:role/lambda-role-first-test",
        "Handler": "example.Hello::handleRequest",
        "CodeSize": 8928,
        "Description": "",
        "Timeout": 15,
        "MemorySize": 512,
        "LastModified": "2021-02-12T16:54:51.033+0000",
        "CodeSha256": "jYP1nUIZ6A7Mqr3J5GwlBCMva56NpgTZDLKGRkT7LQo=",
        "Version": "$LATEST",
        "TracingConfig": {
            "Mode": "PassThrough"
        },
        "RevisionId": "d8317349-b583-446c-a4be-a8ecc6c6eb5b",
        "State": "Active",
        "LastUpdateStatus": "Successful",
        "PackageType": "Zip"
    },
    "Code": {
        "RepositoryType": "S3",
        "Location": "https://awslambda-eu-cent-1-tasks.s3.eu-central-1.amazonaws.com/snapshots/417149921571/my-first-hello-world-01e03c6a-9778-4a76-8f70-3119aae566bc?versionId=1czKg5m_hkjLEcpf34rYrg6g4jTWlPIE&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEOL%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDGV1LWNlbnRyYWwtMSJGMEQCIGMCt9id8HgiD2l1%2FUetwDMhHRSsVmmho1rkipsyAURuAiBflCcDD1mA8HJ7e2UI7QTjC5mt%2FOqPcTRGrfeXOhJLoSrDAwjb%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAMaDDY4MDY4NjU1OTQzNCIMzG8ADrmVCUS%2B%2BkdrKpcDmg2VXF9Q375rdwqKQLt3npQI%2FDd2k3YbIbjGn5piD3%2B%2Fa%2BzTCVObu3KZmzBe6KbMI%2BOkuczO7ajeKPJ8wfU0AW7XryA3Bi2JMM15dOx2PexxhY0xoIVmHuWmJEhvnfm30lgCzYOro%2FZ2rqbqzbWFEqXQTXzc4JIrIyV9ssYC%2B3FftDQrxfO48BJaKhqoC6aLvvPKw2zcKZ1%2Fo2EL4nnWSbCt1qvSdZOihs7nOylk7m6Rd%2Bxy%2B3m540FgCLOrokEcZUdW291ZZITeq0gSennWV8WIesDHwDeoi4Cpbp9p3cKRpk8c70AvLjcMuxQFL97mFA3p3PX%2F6hayHN2xnB6q1qKn4JGKoRg%2FMWjjOTFcLsP2NIciA07QEEOG6SXl46AoJrLnkG8JjR1yQKzNM6kzSCkQn%2BdgHNJIIT9jxkvQ78HCFh1uSF94Uo%2FBxqrwNQ2aakt4McaHcvAfJcn74pix5EhG6zxcYPQ36BjPe91aW5ceoTGg56nx8RxuacZltXd0tZFNTxmtw0ljj7%2Bhek409sfBa2PuxDMwy4WbgQY67AG%2FLAezZ0UYp3uMY3Vnmu2%2BlA92dcsjKDgEE2KtGRwGNFt8hK6mtO05ijNSvutWu1MDwu18Dxb3v2GUx1v3XecFOI1dkHb1RO%2B%2BNgT0Zw8FpFGX9Ztgiaj%2FCclPs19YO7qa7wBr7hZzW%2BayhPKF1B2YXRqGN%2BIoYrLJPl5SziOtoBsTl1966NNSnVTh8rvyvLS%2FFwYUwOObYmcZdcJ7Zm6GAOx66FOjLclx76pnreLhQ5mYcadaSY%2F3UL%2Bx9ymeF%2BUVmWvTgg75%2BzTfYDXFWV3iw8L%2B0FcAAXs1whpHTsWFDXXO5pWeHXbIv8Gcug%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210212T190805Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=ASIAZ47AUUDFPIO7BWBE%2F20210212%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=b152eab5804d83534763c31fdabb084a1761a5ac864df286feb65936e4c2670c"
    }
}
```
The `location` link can be used to download a zip file with the current function code.
- You can upload a new version of your function like this:
```
$ aws --profile lambda-test lambda update-function-code --function-name my-first-hello-world --zip-file fileb:///tmp/my-first-hello-world-358f51e7-ed7c-4d29-bf25-8dd58def0ca6.zip
{
    "FunctionName": "my-first-hello-world",
    "FunctionArn": "arn:aws:lambda:eu-central-1:417149921571:function:my-first-hello-world",
    "Runtime": "java11",
    "Role": "arn:aws:iam::417149921571:role/lambda-role-first-test",
    "Handler": "io.simonis.Test::test",
    "CodeSize": 8928,
    "Description": "",
    "Timeout": 15,
    "MemorySize": 512,
    "LastModified": "2021-02-12T16:51:20.048+0000",
    "CodeSha256": "jYP1nUIZ6A7Mqr3J5GwlBCMva56NpgTZDLKGRkT7LQo=",
    "Version": "$LATEST",
    "TracingConfig": {
        "Mode": "PassThrough"
    },
    "RevisionId": "6a670be6-2998-4be5-bdcd-bfceb7daba17",
    "State": "Active",
    "LastUpdateStatus": "Successful",
    "PackageType": "Zip"
}
```

The [Java sample applications for AWS Lambda](https://github.com/awsdocs/aws-lambda-developer-guide/blob/main/doc_source/java-samples.md#java-sample-applications-for-aws-lambda) and [AWS Lambda function handler in Java](https://github.com/awsdocs/aws-lambda-developer-guide/blob/main/doc_source/java-handler.md) documentation seems to imply that Lambda function handlers implemented in Java have to imlement the predefined [RequestHandler](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-core/src/main/java/com/amazonaws/services/lambda/runtime/RequestHandler.java) or [RequestStreamHandler](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-core/src/main/java/com/amazonaws/services/lambda/runtime/RequestStreamHandler.java) [handler interfaces](https://github.com/awsdocs/aws-lambda-developer-guide/blob/main/doc_source/java-handler.md#java-handler-interfaces). But that's actually not true. The most basic Lambda function written in Java can be as simple as:
```
package io.simonis;

public class Test {

  public static String test() {
    return "Hello";
  }
}
```
The [current Lambda documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html) doesn't describe the recognized handler signatures very well (except for [handlers which implement the predefined handler interfaces](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html#java-handler-interfaces)). While the ["Lambda Function Handler (Java)" pages from 2017](https://web.archive.org/web/20171116220230/http://docs.aws.amazon.com/lambda/latest/dg/java-programming-model-handler-types.html) which can still be found in the [internet archive](https://archive.org/) provide some more information, the full details can only be found in the source code of the [AWS Lambda Java Runtime Interface Client](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-runtime-interface-client) (RIC ) (i.e. [com.amazonaws.services.lambda.runtime.api.client.EventHandlerLoader](https://github.com/aws/aws-lambda-java-libs/blob/c491aa244ce5204bee290d5a0a345b2dcc96319d/aws-lambda-java-runtime-interface-client/src/main/java/com/amazonaws/services/lambda/runtime/api/client/EventHandlerLoader.java#L776-L791)):
```
         * We support the following signatures
         * Anything (InputStream, OutputStream, Context)
         * Anything (InputStream, OutputStream)
         * Anything (OutputStream, Context)
         * Anything (InputStream, Context)
         * Anything (InputStream)
         * Anything (OutputStream)
         * Anything (Context)
         * Anything (AlmostAnything, Context)
         * Anything (AlmostAnything)
         * Anything ()
         *
         * where AlmostAnything is any type except InputStream, OutputStream, Context
         * Anything represents any type (primitive, void, or Object)
         *
         * prefer methods with longer signatures, add extra weight to those ending with a Context object
```
It is interesting to see that the productive AWS infrastructure uses the class `lambdainternal.EventHandlerLoader` instead which is in a different package, but which seems to be otherwise equal its counterpart `com.amazonaws.services.lambda.runtime.api.client.EventHandlerLoader` from the RIC. We can easily confirm this by running the following simple Lambda function which returns a stack trace at the invocation time of the function:
```
  public static String stackTrace() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new Throwable().printStackTrace(new PrintStream(out));
    return out.toString();
  }
```
The output looks as follows and the line numbers of `lambdainternal.EventHandlerLoader` pretty much correspond to the `EventHandlerLoader` version from the RIC.
```
java.lang.Throwable
        at io.simonis.Test.stackTrace(Test.java:26)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
        at java.base/java.lang.reflect.Method.invoke(Unknown Source)
        at lambdainternal.EventHandlerLoader$PojoMethodRequestHandler.handleRequest(EventHandlerLoader.java:282)
        at lambdainternal.EventHandlerLoader$PojoHandlerAsStreamHandler.handleRequest(EventHandlerLoader.java:199)
        at lambdainternal.EventHandlerLoader$2.call(EventHandlerLoader.java:899)
        at lambdainternal.AWSLambda.startRuntime(AWSLambda.java:258)
        at lambdainternal.AWSLambda.startRuntime(AWSLambda.java:192)
        at lambdainternal.AWSLambda.main(AWSLambda.java:187)
```

#### Passing paramaters

Paramters passed to the Lambda function either through tests events in the Lambda console or with the AWS CLI have to be in [JSON format](https://www.w3schools.com/js/js_json_datatypes.asp) and will be deserialized into the corresponding Java POJOs by the Lambda runtime. E.g. a Java `String` will be encoded as *`"string"`* (the *`"`* belong to the encoding and have to be quoted on the command line) and a Java `String[]` will be encoded as *`["str1", "str2"]`*.

To illustrate this we define the following Lambda function which executes its string parameter as a native shell command:
```
  public static String shell(String cmd) throws IOException {
    Process p = new ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start();
    return new String(p.getInputStream().readAllBytes());
  }
```
It can be called as follows:
```
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"pwd; ls -la"'  /tmp/response.txt
```
Without the `--cli-binary-format raw-in-base64-out` option we would have the specify the `--payload` in [base64](https://en.wikipedia.org/wiki/Base64) encoded format. The output string is serialized into JSON format which escapes new-lines and tabs:
```
$ cat /tmp/response.txt
"/var/task\ntotal 4\ndrwxr-xr-x  3 root root   25 Feb 14 17:21 .\ndrwxr-xr-x 24 root root 4096 Jan 19 13:02 ..\ndrwxr-xr-x  3 root root   30 Feb 14  2021 io\n"
```
To view its original content we can use some *nix shell magic:
```
$ cat /tmp/response.txt | tail -c +2 | head -c -1 | sed 's/\\n/\n/g' | sed 's/\\t/        /g'
/var/task
total 4
drwxr-xr-x  3 root root   25 Feb 14 17:21 .
drwxr-xr-x 24 root root 4096 Jan 19 13:02 ..
drwxr-xr-x  3 root root   30 Feb 14  2021 io
```
The Lambda functions configuration (e.g. the handler, timeout or memory size) can be changed with the `update-function-configuration` command:
```
$ aws --profile lambda-test lambda update-function-configuration --function-name my-first-hello-world --handler "io.simonis.Test::timeN" --timeout 20 --memory-size 8192
{
    "FunctionName": "my-first-hello-world",
    "FunctionArn": "arn:aws:lambda:eu-central-1:417149921571:function:my-first-hello-world",
    "Runtime": "java11",
    "Role": "arn:aws:iam::417149921571:role/lambda-role-first-test",
    "Handler": "io.simonis.Test::timeN",
    "CodeSize": 3881,
    "Description": "",
    "Timeout": 20,
    "MemorySize": 8192,
    "LastModified": "2021-02-15T16:44:48.851+0000",
    "CodeSha256": "HhZj0bhlc8Vo2SJkySsysjGX+wbDbMcjFljLsGS3sQQ=",
    "Version": "$LATEST",
    "TracingConfig": {
        "Mode": "PassThrough"
    },
    "RevisionId": "46c6e784-f6f2-40c9-9748-2bf05d5b2f24",
    "State": "Active",
    "LastUpdateStatus": "Successful",
    "PackageType": "Zip"
}
```

#### Determining the CPU entitlement

In Lambda the number of vCPUs a function (or, to be more exact, the execution environment the function will be exected in) gets assigned depends on the amount of memory allocated for the function. But it's not only the amount of vCPUs that depends on the amount of the configured memory, but also the CPU entitlement (i.e. *how much* of a physical CPU a vCPU will be able to use). In Linux CPU constraining is usually accomplished with the help of the kernel control group (cgroup) mechanisms which is described in the [CFS Bandwith Control](https://www.kernel.org/doc/Documentation/scheduler/sched-bwc.txt) kernel documentation (CFS stands for [Completely Fair Scheduler](https://www.kernel.org/doc/Documentation/scheduler/sched-design-CFS.txt)). The blog [Unthrottled: Fixing CPU Limits in the Cloud](Unthrottled: Fixing CPU Limits in the Cloud) contains a nice conceptual model of CPU constraints.

For Lambda functions, this means that for a configured memory between 128mb and 3000mb, you'll always get 2 vCPUs but for a 128mb instance these two vCPUs will only have an entitlement of ~7% while you'll get 100% entitlement (i.e. two full CPUs) for 3000mb. Below is a short Lambda function which prints the number of vCPUs and an estimation for their entitlement:
```
public class Test {

  static volatile byte[] sink = new byte[4096];
  static long backgroundID;

  static {
    Thread background = new Thread("BackgroundThread") {
      public void run() {
        Random r = new Random();
        while (true) {
          r.nextBytes(sink);
        }
      }
    };
    backgroundID = background.getId();
    background.setDaemon(true);
    background.start();
  }

  private static final int SECONDS = 2;

  public static String time() throws InterruptedException {

    Thread.sleep(SECONDS * 1_000);

    ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
    return
      "this       (cpu time)  = " + tmx.getCurrentThreadCpuTime() + " " + Thread.currentThread().getId() + "\n" +
      "this       (user time) = " + tmx.getCurrentThreadUserTime() + " " + Thread.currentThread().getId() + "\n" +
      "background (cpu time)  = " + tmx.getThreadCpuTime(backgroundID) + "\n" +
      "background (user time) = " + tmx.getThreadUserTime(backgroundID) + "\n";
  }

  public static String timeN(int count) throws InterruptedException {
    ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
    long start = tmx.getThreadCpuTime(backgroundID);
    StringBuffer out = new StringBuffer();
    out.append("vCPUs = " + ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + "\n\n");
    for (int i = 0; i < count; i++) {
      out.append(time());
    }
    double time = ((double)(tmx.getThreadCpuTime(backgroundID) - start)) / 1_000_000_000;
    out.append("\nEntitlement = " + (time / (SECONDS * count)) + "\n");
    return out.toString();
  }
}
```
Running this function with different memory configurations results in the following output:
```
$ aws --profile lambda-test lambda update-function-configuration --function-name my-first-hello-world --handler "io.simonis.Test::timeN" --timeout 20 --memory-size 128
...
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"4"' /tmp/response.txt
...
$ cat /tmp/response.txt | tail -c +2 | head -c -1 | sed 's/\\n/\n/g' | sed 's/\\t/        /g'

vCPUs = 2

this       (cpu time)  = 274594665 1
this       (user time) = 240000000 1
background (cpu time)  = 360770657
background (user time) = 360000000
this       (cpu time)  = 275001733 1
this       (user time) = 240000000 1
background (cpu time)  = 505290234
background (user time) = 500000000
this       (cpu time)  = 275329724 1
this       (user time) = 240000000 1
background (cpu time)  = 650309521
background (user time) = 650000000
this       (cpu time)  = 275643617 1
this       (user time) = 240000000 1
background (cpu time)  = 792680210
background (user time) = 790000000

Entitlement = 0.071927492375

$ aws --profile lambda-test lambda update-function-configuration --function-name my-first-hello-world --handler "io.simonis.Test::timeN" --timeout 20 --memory-size 1024
...
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"4"' /tmp/response.txt
...
$ cat /tmp/response.txt | tail -c +2 | head -c -1 | sed 's/\\n/\n/g' | sed 's/\\t/        /g'

vCPUs = 2
...
Entitlement = 0.577702040875
$ aws --profile lambda-test lambda update-function-configuration --function-name my-first-hello-world --handler "io.simonis.Test::timeN" --timeout 20 --memory-size 2048
...
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"4"' /tmp/response.txt
...
$ cat /tmp/response.txt | tail -c +2 | head -c -1 | sed 's/\\n/\n/g' | sed 's/\\t/        /g'

vCPUs = 2

Entitlement = 0.9995579905
```

Getting the optimal price/performance memory configuration can be tricky. Have a look at the blog [New for AWS Lambda – Functions with Up to 10 GB of Memory and 6 vCPUs](https://aws.amazon.com/blogs/aws/new-for-aws-lambda-functions-with-up-to-10-gb-of-memory-and-6-vcpus/) and the [AWS Lambda Power Tuning](https://github.com/alexcasalboni/aws-lambda-power-tuning) tool to get more details.

The previous function can also be used to demonstrate that the runtime environment gets an [extra CPU boost before the very first invocation of the handler](https://hichaelmart.medium.com/shave-99-93-off-your-lambda-bill-with-this-one-weird-trick-33c0acebb2ea) and background threads are not executed any more after the handler returns:
```
$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"2"' /tmp/response.txt
...
this       (cpu time)  = 265581415 1
this       (user time) = 200000000 1
background (cpu time)  = 334378187
background (user time) = 330000000
this       (cpu time)  = 266072036 1
this       (user time) = 200000000 1
background (cpu time)  = 477326044
background (user time) = 470000000

Entitlement = 0.07233390375

// wait for two minutes...

$ aws --profile lambda-test lambda --output json invoke --function-name my-first-hello-world --invocation-type RequestResponse --log-type Tail --cli-binary-format raw-in-base64-out --payload '"2"' /tmp/response.txt
...
this       (cpu time)  = 270087132 1
this       (user time) = 200000000 1
background (cpu time)  = 629790939
background (user time) = 620000000
this       (cpu time)  = 270403089 1
this       (user time) = 200000000 1
background (cpu time)  = 772816430
background (user time) = 770000000

Entitlement = 0.07151966975
```
In this example we run with 128mb memory which gives us an entitlement of ~7% (i.e. from a second CPU time we'll only get about 7 miliseconds). You can see that after the first iteration in the handler (which took 2 seconds wall clock time) the background thread already consumed 0.33 seconds (33 miliseconds) CPU time altough, accrding to the entitlement, it should only get about 0.14 seconds. After the second iteration (and 2 more seconds wall clock time) the background thread consumed 0.47 seconds (so just about 0.14 more seconds CPU time) which corresponds exactly to our entitlement.

After we waited for a few minutes before invoking the function a second time, we can observe that after the first iteration of the second invocation, the background thread thread has consumed 0.62 seconds CPU time which is 0.15 seconds more than the 0.47 seconds after the last iteration of the first invocation. This means that the background thread (and the whole runtime) didn't consume any CPU time between the two invocations. That's because the whole execution environment was [freezed](https://www.kernel.org/doc/Documentation/cgroup-v1/freezer-subsystem.txt) between calls to the handler.

#### Lambda example with Spring Boot

[AWS Lambda with Spring Boot](https://dzone.com/articles/aws-lambda-with-spring-boot)

[A trivial Spring Boot application with a single REST API](https://github.com/gemerick/spring-boot-lambda)

[AWS Lambda and Java Spring Boot: Getting Started](https://epsagon.com/tools/aws-lambda-and-java-spring-boot-getting-started/) is an introduction which uses the [aws-serverless-java-container tool](https://github.com/awslabs/aws-serverless-java-container).



[AWS re:Invent 2018: A Serverless Journey: AWS Lambda Under the Hood (Holly Mesrobian, Marc Brooker)](https://www.youtube.com/watch?v=QdzV04T_kec).

[Lambda Internals: Exploring AWS Lambda](https://epsagon.com/observability/lambda-internals-exploring-aws-lambda/) describes how to ssh into a Lambda execution environment. Part two ([AWS Lambda Internals  –  Part 2: Going Deeper](https://epsagon.com/observability/lambda-internals-part-two/)) has some more details about the Lambda runtime and the slicer.


[AWS Lambda base images for Java](https://gallery.ecr.aws/lambda/java)

https://github.com/aws/aws-lambda-base-images
https://github.com/aws/aws-lambda-base-images/tree/java8
https://github.com/aws/aws-lambda-base-images/blob/java8/Dockerfile.java8

The AWS Lambda base images for Java have `/lambda-entrypoint.sh` as default [ENTRYPOINT](https://github.com/aws/aws-lambda-base-images/blob/af636701f1a4d8a3bbe193a7b77a2ba1f139cbb2/Dockerfile.java8#L21):

```
# cat /lambda-entrypoint.sh
#!/bin/sh
# Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

if [ $# -ne 1 ]; then
  echo "entrypoint requires the handler name to be the first argument" 1>&2
  exit 142
fi
export _HANDLER="$1"

RUNTIME_ENTRYPOINT=/var/runtime/bootstrap
if [ -z "${AWS_LAMBDA_RUNTIME_API}" ]; then
  exec /usr/local/bin/aws-lambda-rie $RUNTIME_ENTRYPOINT
else
  exec $RUNTIME_ENTRYPOINT
fi
```

If `AWS_LAMBDA_RUNTIME_API` is not set, it will execute `/usr/local/bin/aws-lambda-rie /var/runtime/bootstrap` where `aws-lambda-rie` is the [AWS Lambda Runtime Interface Emulator (RIE)](https://docs.aws.amazon.com/lambda/latest/dg/images-test.html). The RIE is [needed for local testing](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-runtime-interface-client#local-testing). `/var/runtime/bootstrap` is a script which will be executed by `aws-lambda-rie` once it receives a request:
```
#!/bin/bash
# quirk, java8 always had the 'wrong' working directory
cd /
exec /var/runtime/bin/aws-lambda-java
```
The bootstrap script calls the native executable `/var/runtime/bin/aws-lambda-java` which starts `java` with specific command line parameters (e.g. `-XX:-TieredCompilation`) and runs `LambdaJavaRTEntry-1.0.jar`. I couldn't find the source code for `aws-lambda-java` yet. The main class of `LambdaJavaRTEntry-1.0.jar` is `lambdainternal.LambdaRTEntry` (which I couldn't find the sources code of as well) with `aws-lambda-java-core-1.2.0.jar` in the class path. The latter is the [AWS Lambda Java Core Library](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-core) which is part of the [AWS Lambda Java Support Libraries](https://github.com/aws/aws-lambda-java-libs) and defines the Lambda [Context](http://docs.aws.amazon.com/lambda/latest/dg/java-context-object.html) object as well as [interfaces](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html#java-handler-interfaces) that Lambda accepts.


The [AWS Lambda Java Runtime Interface Client](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-runtime-interface-client) implements the Lambda [Runtime API](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html), allowing you to seamlessly extend your preferred base images to be Lambda compatible. The Lambda Runtime Interface Client is a lightweight interface that allows your runtime to receive requests from and send requests to the Lambda service.




[AWSLambda.java](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-runtime-interface-client/src/main/java/com/amazonaws/services/lambda/runtime/api/client/AWSLambda.java)
```
/**
 * The entrypoint of this class is {@link AWSLambda#startRuntime}. It performs two main tasks:
 *
 * <p>
 * 1. loads the user's handler.
 * <br/>
 * 2. enters the Lambda runtime loop which handles function invocations as defined in the Lambda Custom Runtime API.
 *
 * <p>
 * Once initialized, {@link AWSLambda#startRuntime} will halt only if an irrecoverable error occurs.
 */
 ```

[Modifying the runtime environment](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-modify.html)

Example: Intercept Lambda invokes with `javaagent`


[Cold Starts in AWS Lambda](https://mikhail.io/serverless/coldstarts/aws/)



```
$ docker run -d -v /:/host_root -v `pwd`/aws-lambda-rie:/aws-lambda -p 9000:8080 --entrypoint /aws-lambda/aws-lambda-rie local-lambda:0.1 --log-level trace /usr/bin/java -cp './*' com.amazonaws.services.lambda.runtime.api.client.AWSLambda io.simonis.Test::test

$ sudo netstat -tapn | grep 9000
[sudo] password for simonisv:
tcp6       0      0 :::9000                 :::*                    LISTEN      27813/docker-proxy

$ docker logs awesome_lumiere
time="2021-02-10T10:53:09.081" level=info msg="exec '/usr/bin/java' (cwd=/function, handler=io.simonis.Test::test)"
time="2021-02-10T13:05:59.417" level=debug msg="Runtime API Server listening on 127.0.0.1:9001"

$ docker exec -it laughing_hoover /bin/bash

# ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  0 19:43 ?        00:00:00 /aws-lambda/aws-lambda-rie /usr/bin/java -cp ./* com.amazonaws.services.lambda.r
root        14     0  0 19:43 pts/0    00:00:00 /bin/bash
#  /host_root/bin/netstat -tapn
Active Internet connections (servers and established)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp        0      0 127.0.0.1:9001          0.0.0.0:*               LISTEN      1/aws-lambda-rie
tcp6       0      0 :::8080                 :::*                    LISTEN      1/aws-lambda-rie
```

The Lambda *Runtime Interface Emulator* (RIE) starts listening on port 8080 (this seems to be hard-coded in [`cmd/aws-lambda-rie/main.go`](https://github.com/aws/aws-lambda-runtime-interface-emulator/blob/68b32d675f31b1c692da68ed3b747119d993301b/cmd/aws-lambda-rie/main.go#L44) inside the Docker container (which is forwarded to port 9000 outside the container). The RIE also starts to listen on `localhost:9001` inside the container and exports this endpoint as `AWS_LAMBDA_RUNTIME_API` (see [lambda/rapid/sandbox.go](https://github.com/aws/aws-lambda-runtime-interface-emulator/blob/68b32d675f31b1c692da68ed3b747119d993301b/lambda/rapid/sandbox.go#L63)).

When the RIE receives the first request on `8080` it will start its first argument (i.e. `/usr/bin/java` in this example). The executed main class `com.amazonaws.services.lambda.runtime.api.client.AWSLambda` is from the Lambda *Java Runtime Interface Client* (RIC). The RIC reads the `AWS_LAMBDA_RUNTIME_API` environemnt variable and [connects to RIE](https://github.com/aws/aws-lambda-java-libs/blob/c491aa244ce5204bee290d5a0a345b2dcc96319d/aws-lambda-java-runtime-interface-client/src/main/java/com/amazonaws/services/lambda/runtime/api/client/AWSLambda.java#L206-L207) on that address.

```
$ curl -v -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 9000 (#0)
> POST /2015-03-31/functions/function/invocations HTTP/1.1
> Host: localhost:9000
> User-Agent: curl/7.58.0
> Accept: */*
> Content-Length: 2
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 2 out of 2 bytes
< HTTP/1.1 200 OK
< Date: Tue, 09 Feb 2021 19:51:16 GMT
< Content-Length: 7
< Content-Type: text/plain; charset=utf-8
<
* Connection #0 to host localhost left intact
"Hello"

$ docker logs awesome_lumiere
...
START RequestId: a7502fd0-519a-4740-be51-187c70542f8e Version: $LATEST
END RequestId: a7502fd0-519a-4740-be51-187c70542f8e
REPORT RequestId: a7502fd0-519a-4740-be51-187c70542f8e	Init Duration: 0.41 ms	Duration: 336.54 ms	Billed Duration: 400 msMemory Size: 3008 MB	Max Memory Used: 3008 MB

# ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  0 19:43 ?        00:00:00 /aws-lambda/aws-lambda-rie /usr/bin/java -cp ./* com.amazonaws.services.lambda.r
root        14     0  0 19:43 pts/0    00:00:00 /bin/bash
root        21     1  0 19:51 ?        00:00:00 /usr/bin/java -cp ./* com.amazonaws.services.lambda.runtime.api.client.AWSLambda
# /host_root/bin/netstat -tapn
Active Internet connections (servers and established)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp        0      0 127.0.0.1:9001          0.0.0.0:*               LISTEN      1/aws-lambda-rie
tcp        0      0 127.0.0.1:60132         127.0.0.1:9001          ESTABLISHED 21/java
tcp        0      0 127.0.0.1:9001          127.0.0.1:60132         ESTABLISHED 1/aws-lambda-rie
tcp6       0      0 :::8080                 :::*                    LISTEN      1/aws-lambda-rie

$ curl -v -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'
$ docker logs awesome_lumiere
...
START RequestId: a7502fd0-519a-4740-be51-187c70542f8e Version: $LATEST
END RequestId: a7502fd0-519a-4740-be51-187c70542f8e
REPORT RequestId: a7502fd0-519a-4740-be51-187c70542f8e	Init Duration: 0.41 ms	Duration: 336.54 ms	Billed Duration: 400 msMemory Size: 3008 MB	Max Memory Used: 3008 MB
START RequestId: bc78bf9d-2391-4dcc-b3c3-e79d37ce7e69 Version: $LATEST
END RequestId: bc78bf9d-2391-4dcc-b3c3-e79d37ce7e69
REPORT RequestId: bc78bf9d-2391-4dcc-b3c3-e79d37ce7e69	Duration: 1.56 ms	Billed Duration: 100 ms	Memory Size: 3008 MB	Max Memory Used: 3008 MB

```

## Checkpoint and restore in User Space (CRIU)

```
$ sudo criu dump --tree 14910 --images-dir /tmp/random_init --shell-job --leave-running
```

```
$ sudo unshare -p -m -f bash
# mount -t proc none /proc/
# criu restore --images-dir /tmp/random_init --shell-job
```

- [Faster start-up for Java applications on Open Liberty with CRIU snapshots](https://openliberty.io/blog/2020/02/12/faster-startup-Java-applications-criu.html)
- The corresponding https://github.com/ashu-mehra/criu-ol GitHub repo contains usefull scripts and examples on how to run CRIU

## [Kata Containers Architecture](https://github.com/kata-containers/documentation/blob/master/design/architecture.md)

## Install new kernel on Ubuntu
```
$ mkdir linux-5.19.17/
$ cd linux-5.19.17/
$ wget https://kernel.ubuntu.com/~kernel-ppa/mainline/v5.19.17/amd64/linux-headers-5.19.17-051917-generic_5.19.17-051917.202210240939_amd64.deb
$ wget https://kernel.ubuntu.com/~kernel-ppa/mainline/v5.19.17/amd64/linux-headers-5.19.17-051917_5.19.17-051917.202210240939_all.deb
$ wget https://kernel.ubuntu.com/~kernel-ppa/mainline/v5.19.17/amd64/linux-image-unsigned-5.19.17-051917-generic_5.19.17-051917.202210240939_amd64.deb
$ wget https://kernel.ubuntu.com/~kernel-ppa/mainline/v5.19.17/amd64/linux-modules-5.19.17-051917-generic_5.19.17-051917.202210240939_amd64.deb
  
$ sudo apt install ./linux-*.deb
```

## Compiling Ubuntu/Debian packages from source
```
$ wget https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-5.19.17.tar.xz
$ tar -xJf linux-5.19.17.tar.xz
$ cd linux-5.19.17
$ cp /boot/config-$(uname -r) .config
$ make olddefconfig
$ make -j 12 deb-pkg LOCALVERSION=-custom
$ sudo dpkg -i ../linux-image-5.19.17-custom_5.19.17-custom-1_amd64.deb ../linux-headers-5.19.17-custom_5.19.17-custom-1_amd64.deb
```

## Setting up Ubuntu machine for development

```
$ sudo apt-get update
$ sudo apt-get install autoconf make zip unzip gcc g++ gcc g++ libx11-dev libxext-dev libxrender-dev libxrandr-dev libxtst-dev libxt-dev libcups2-dev libasound2-dev libfreetype6-dev libfontconfig-dev ccache libnet-dev libnl-route-3-dev gcc bsdmainutils build-essential git-core iptables libaio-dev libcap-dev libgnutls28-dev libgnutls30 libnl-3-dev libprotobuf-c-dev libprotobuf-dev libselinux-dev libbsd-dev pkg-config protobuf-c-compiler protobuf-compiler emacs-nox apt-transport-https ca-certificates curl software-properties-common libclang-dev net-tools acl
```
## Install Docker: https://docs.docker.com/engine/install/ubuntu/
```
$ sudo adduser ubuntu docker
```
