/*
 * Copyright 2017 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.featran

import breeze.linalg.{DenseVector, SparseVector}
import org.scalacheck._

import scala.reflect.ClassTag

class FeatureBuilderSpec extends Properties("FeatureBuilder") {

  private def list[T](implicit arb: Arbitrary[Option[T]]): Gen[List[Option[T]]] =
    Gen.listOfN(100, arb.arbitrary)

  private def test[T: ClassTag : Numeric, F](xs: List[Option[T]], fb: FeatureBuilder[F])
                                            (toSeq: F => Seq[T]): Prop = {
    val num = implicitly[Numeric[T]]
    fb.init(xs.size)
    xs.foreach {
      case Some(x) => fb.add(num.toDouble(x))
      case None => fb.skip()
    }
    toSeq(fb.result) == xs.map(_.getOrElse(num.zero))
  }

  property("float array") = Prop.forAll(list[Float]) { xs =>
    test(xs, implicitly[FeatureBuilder[Array[Float]]])(_.toSeq)
  }

  property("double array") = Prop.forAll(list[Double]) { xs =>
    test(xs, implicitly[FeatureBuilder[Array[Double]]])(_.toSeq)
  }

  property("float dense vector") = Prop.forAll(list[Float]) { xs =>
    test(xs, implicitly[FeatureBuilder[DenseVector[Float]]])(_.data.toSeq)
  }

  property("double dense vector") = Prop.forAll(list[Double]) { xs =>
    test(xs, implicitly[FeatureBuilder[DenseVector[Double]]])(_.data.toSeq)
  }

  property("float sparse vector") = Prop.forAll(list[Float]) { xs =>
    test(xs, implicitly[FeatureBuilder[SparseVector[Float]]])(_.toDenseVector.data.toSeq)
  }

  property("double sparse vector") = Prop.forAll(list[Double]) { xs =>
    test(xs, implicitly[FeatureBuilder[SparseVector[Double]]])(_.toDenseVector.data.toSeq)
  }

}