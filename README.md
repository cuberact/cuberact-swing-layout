# cuberact-swing-layout
Swing Table Layout (Composite). Based on [EsotericSoftware/TableLayout](https://github.com/EsotericSoftware/tablelayout) . Added new features and improved for Swing.



## Example

```java
Composite composite = new Composite();
composite.setBorder(BorderFactory.createLineBorder(Color.RED));
composite.pad(20);
composite.defaults().space(5);
composite.addCell("User name").align(Cell.RIGHT);
composite.addCell(new JTextField()).prefWidth(150).minWidth(30);
composite.row();
composite.addCell("Password").align(Cell.RIGHT);
composite.addCell(new JPasswordField()).prefWidth(150).minWidth(30);
composite.row();
composite.addCell(new JButton("enter")).padTop(10).align(Cell.RIGHT).colspan(2).getWidget().addActionListener(event -> {
    // save credentials
});
```

## Configuration

##### Maven

```xml
<dependency>
  <groupId>org.cuberact</groupId>
  <artifactId>cuberact-swing-layout</artifactId>
  <version>1.0.0</version>
</dependency>
```

##### Gradle

```groovy
compile 'org.cuberact:cuberact-swing-layout:1.0.0'
```

##### Ivy

```xml
<dependency org="org.cuberact" name="cuberact-swing-layout" rev="1.5.0">
  <artifact name="cuberact-json" type="jar" />
</dependency>
```
## Original licence 


  Copyright (c) 2011, Nathan Sweet <nathan.sweet@gmail.com>
  All rights reserved.
 
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  
  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
  * Neither the name of the <organization> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
