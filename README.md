# ImageDiff

This Java GUI application was developed to help software development community to reduce time spent on manual GUI testing.

As part of the comprehensive solution to the problem this application allows to view and compare several images (screenshots) at the same time.

## License
This application is available under GPLv3 license. See [LICENSE](LICENSE) for details.

### A proposed usage scenario (_solution_):
* an automated GUI test is developed and run, capturing and saving screenshots in some key points during the test
* saved images are compared with the sample image (e.g. from previous release) as part of regression testing to assure no differences introduced

#### The reasoning behind the solution:
* all actions conducted by a QA engineer manually to get the final screen to test (the result) are can be perfectly automated
* the only thing that cannot be conducted well by a machine is comparing the results of the test and reasoning whether the result is acceptable or not, then reporting problems if any found
* time spent on the preparation step to get the final result to compare builds up on average 80% of the entire test; this time should be eliminated by delegating this work to the machine (for example for after-work hours)

#### Screenshots directory structure
Place your screenshots in a directory with the following structure:
<pre>
test_screenshot_directory
|-- test1_directory
|   |-- sample_image.png
|   |-- browser1.png
|   |-- browser2.png
|   `-- info.feature
|-- test2_directory
|   |-- browser1.png
|   |-- browser2.png
|   |-- browser3.png
|   `-- info.feature
`-- test3_directory
    |-- browser1.png
    |-- browser2.png
    |-- browser3.png
    `-- info.feature
</pre>
where testX_directory contains:
* one image (screenshot) for each browser for a particular test (or a specific key point in the test); these images will be compared with the sample_image.png
* info.feature contains the description for the test, available on pressing the Alt button

#### Configuration file
Configuration file config.properties is used to let the application know how to order images on the screen and how to name them.
Refer to the contents of the sample file here to find a basic example.

The names of the images provided in the configuration file are assigned to the buttons on the toolbar and are displayed in the left upper corner of the images on Shift press.
