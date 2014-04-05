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
