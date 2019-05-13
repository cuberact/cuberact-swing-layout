![](https://raw.githubusercontent.com/cuberact/cuberactorg.github.io/master/cuberact_128x128.png) 

# cuberact-swing-layout

Swing panel with table layout (Composite). Based on [EsotericSoftware/TableLayout](https://github.com/EsotericSoftware/tablelayout) .

* possibility of different number of components in individual rows
* aligning components within cells (align)
* indentation inside cells (padding, spacing)
* complex cell sizing (min, pref, max)
* joining columns (col span)
* joining rows must be solved through embedded composite (matroska)

The original idea of TableLayout is excellent. In a small space, you can describe a very complex layout. It is still very human readable and even very clear.
Whole client code is pure java (refactor friendly). No constraint description in strings. 
 
## Changes against the original

* Cell is generic by widget/component (better chaining)
* Composite border working correctly 
* Composite background painting (optionally)
* some optimizations (speed, instances count)
* API cleaned. The original project is multi-use (Swing, JavaFX, libGdx). This layout is for Swing only. This allows me to fine-tune the API more.
* added possibility remove single widget from Composite without remove all and add all composite children again
* no other dependencies on other libraries
* targeted to the smallest code as possible
* debug option (draw debug rectangles) is removed

#### Class names

The name "Table" is used in Swing, so the client code is often confusing. That's why I chose another name.

| cuberact-swing-layout | EsotericSoftware/TableLayout |
| --------------------- | ---------------------------- |
| Composite             | Table                        |
| CompositeLayout       | TableLayout                  |
| Cell                  | Cell                         |
| Stack                 | Stack                        |

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
![](https://raw.githubusercontent.com/cuberact/cuberactorg.github.io/master/images/cuberact-swing-layout/credentials-layout-showcase.png)

## Configuration

[Maven central repo](https://search.maven.org/artifact/org.cuberact/cuberact-swing-layout)

## License

__cuberact-swing-layout__ is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2019 Michal Nikodim <michal.nikodim@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Original licence from EsotericSoftware/TableLayout


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
